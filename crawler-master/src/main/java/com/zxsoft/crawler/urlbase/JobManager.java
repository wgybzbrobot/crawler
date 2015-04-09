package com.zxsoft.crawler.urlbase;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.api.Machine;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.master.SlaveCache;
import com.zxsoft.crawler.master.SlaveStatus;
import com.zxsoft.crawler.master.SlaveStatus.State;
import com.zxsoft.crawler.master.impl.ScoredMachine;
import com.zxsoft.crawler.master.impl.SlaveScheduler;
import com.zxsoft.crawler.slave.SlavePath;

/**
 * 添加巡检任务到redis中
 */
public class JobCreator {

    private static final Logger LOG = LoggerFactory.getLogger(JobCreator.class);

    private static final String URLBASE = "urlbase";

    private final JedisPool pool;

    public JobCreator(String host, int port, String passwd) {
        super();
        pool = JedisPoolFactory.getInstance(host, port, passwd);
    }

    public int addToRedis(JobConf jobConf) throws CrawlerException {

        if (exsit(jobConf))
            throw new CrawlerException(ErrorCode.SYSTEM_ERROR, "Job is exist");
        Jedis jedis = pool.getResource();
        try {
            double score = 1.0d / (System.currentTimeMillis() / 60000);
            jedis.zadd(URLBASE, score, jobConf.toString());
        } finally {
            pool.returnResource(jedis);
        }
        return 0;
    }

    /**
     * 判断任务是否已存在。 此外若发现不符合jobconf的json，则删除。
     * 
     * @param jobConf
     * @return
     */
    private boolean exsit(JobConf jobConf) {
        String url = jobConf.getUrl();
        Jedis jedis = pool.getResource();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        long count = 0, begin = 0, end = 100;
        count = jedis.zcard(URLBASE);
        Set<String> invalidJsons = new HashSet<String>();
        try {
            while (begin < count) {
                Set<String> set = jedis.zrevrange(URLBASE, begin, end);
                if (CollectionUtils.isEmpty(set))
                    break;
                for (String json : set) {
                    JobConf jc = null;
                    try {
                        jc = gson.fromJson(json, JobConf.class);
                    } catch (JsonSyntaxException jse) {
                        invalidJsons.add(json);
                    }
                    String _url = jc.getUrl();
                    if (_url.endsWith("/")) {
                        _url = _url.substring(0, _url.lastIndexOf("/"));
                    }
                    if (url.equals(_url) && jobConf.isRecurrence() == true && jobConf.isRecurrence() == jc.isRecurrence()) {
                        return true;
                    }
                }
                begin = end;
                end = end + 100;
            }
        } finally {
            pool.returnResource(jedis);
        }

        if (invalidJsons.size() > 0) {
            jedis.zrem(URLBASE, invalidJsons.toArray(new String[] {}));
        }

        return false;
    }

    private static SlaveScheduler scheduler = SlaveScheduler.getInstance();

    private class MyPoolExecutor extends ThreadPoolExecutor {
        public MyPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                        TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }
    }

    public List<SlaveStatus> workerStatus() throws InterruptedException,
                    ExecutionException {
        List<SlaveStatus> res = new ArrayList<SlaveStatus>();
        List<Machine> machines = SlaveCache.machines;
        ThreadPoolExecutor exec = new MyPoolExecutor(10, 100, 10, TimeUnit.SECONDS,
                        new ArrayBlockingQueue<Runnable>(100));
        List<Callable<SlaveStatus>> tasks = new ArrayList<Callable<SlaveStatus>>();
        for (Machine machine : machines) {
            Vistor vistor = new Vistor(machine);
            tasks.add(vistor);
        }
        List<Future<SlaveStatus>> futures = exec.invokeAll(tasks);
        /*
         * 评分
         */
        for (Future<SlaveStatus> future : futures) {
            SlaveStatus status = future.get();
            if (status.state == State.STOP) {
                status.score = 0.0f;
                LOG.error(status.machine.toString() + "挂了, 或者防火墙阻止了端口访问.");
            } else {
                status.score = 1.0f / (1.0f + status.runningNum);
                ScoredMachine sm = new ScoredMachine(status.machine, status.runningNum,
                                status.score);
                scheduler.addSlave(sm);
                LOG.info(status.machine.getId() + ":" + status.score);
            }
            res.add(status);
        }
        exec.shutdown();
        return res;
    }

    class Vistor implements Callable<SlaveStatus> {
        private Machine machine;

        public Vistor(Machine machine) {
            this.machine = machine;
        }

        public SlaveStatus call() throws Exception {
            String url = "http://" + machine.getIp() + ":" + machine.getPort() + "/"
                            + SlavePath.PATH + "/" + SlavePath.JOB_RESOURCE_PATH;
            SlaveStatus slaveStatus = null;
            com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client
                            .create();
            WebResource webResource = client.resource(url);
            try {
                String text = webResource.get(String.class);
                NumNum tm = new Gson().fromJson(text, NumNum.class);
                slaveStatus = new SlaveStatus(machine, tm.runningNum, tm.historyNum,
                                2000, "success", State.RUNNING);
            } catch (ClientHandlerException e) {
                slaveStatus = new SlaveStatus(machine, 0, 0, 4040, e.getMessage(),
                                State.STOP);
            } finally {
                client.destroy();
            }
            return slaveStatus;
        }
    }

    class NumNum {
        int runningNum;
        int historyNum;
    }

    /**
     * 选择worker节点
     */
    public ScoredMachine chooseSlave() {
        ScoredMachine sm = scheduler.selectSlave();
        return sm;
    }

    /**
     * 提交任务给worker节点
     * @param jobConf
     * @throws CrawlerException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public void submitToWorker(JobConf jobConf) throws CrawlerException,
                    InterruptedException, ExecutionException {

        int i = 0;
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        while (i++ < SlaveCache.machines.size()) {
            ScoredMachine sm = chooseSlave();
            
            String url = "http://" + sm.machine.getIp() + ":" + sm.machine.getPort() + "/"
                            + SlavePath.PATH + "/" + SlavePath.JOB_RESOURCE_PATH;

            int server_id = 0;
            try {
                server_id = Integer.valueOf(sm.machine.getComment());
            } catch (Exception e) {
                LOG.warn("Error Config in slaves.ini, slave comment should be int type. Message: "
                                + e.getMessage());
                // server_id = IPUtil.getServerId();
            }
            jobConf.setWorkerId(server_id);

            com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create();
            WebResource webResource = client.resource(url);

            String json = gson.toJson(jobConf, JobConf.class);
            ClientResponse response = null;
            try {
                response = webResource.type("application/json").post(ClientResponse.class, json);
            } catch (ClientHandlerException e) {
                LOG.error(e.getMessage());
                LOG.error("URL为" + url + "的slave不能执行任务,他可能很忙,也可能挂了. 准备换一个slave试试...");
                workerStatus(); // 重新获取每个slave状态
                continue;
            } finally {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.destroy();
                }
            }
            return;
        }
        throw new CrawlerException(ErrorCode.SYSTEM_ERROR, "No slave working");
    }
}
