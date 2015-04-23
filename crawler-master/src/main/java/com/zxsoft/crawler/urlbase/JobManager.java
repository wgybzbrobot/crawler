package com.zxsoft.crawler.urlbase;

import java.net.NoRouteToHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

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
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.common.WorkerConf;
import com.zxsoft.crawler.master.impl.WorkerScheduler;
import com.zxsoft.crawler.slave.SlavePath;

/**
 * 添加任务到redis中
 */
public class JobManager {

    private static final Logger LOG = LoggerFactory.getLogger(JobManager.class);

    private static final String URLBASE = "urlbase";

    private final JedisPool pool;

    public JobManager(String host, int port, String passwd) {
        super();
        pool = JedisPoolFactory.getInstance(host, port, passwd);
    }

    public int addToRedis(JobConf jobConf) throws CrawlerException {

        if (exsit(jobConf))
            throw new CrawlerException(ErrorCode.SYSTEM_ERROR, "Job is exist");
        Jedis jedis = pool.getResource();
        try {
            double score = 1.0d / (System.currentTimeMillis() / 60000.0d);
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
                        continue;
                    }
                    String _url = jc.getUrl();
                    if (_url.endsWith("/")) {
                        _url = _url.substring(0, _url.lastIndexOf("/"));
                    }
                    if ((url.equals(_url) && jobConf.isRecurrence() == true
                                    && jobConf.isRecurrence() == jc.isRecurrence()) || jobConf.getJobId() == jc.getJobId()) {
                        
                        // identify_md5都不存在，则规定为任务一致
                        if (StringUtils.isEmpty(jobConf.getIdentify_md5()) && StringUtils.isEmpty(jc.getIdentify_md5()))  {
                            return true;
                        } else if (jobConf.getIdentify_md5().trim().equals(jc.getIdentify_md5().trim())) {
                            // identify_md5有一个不存在，或者不一样，则规定任务不一致
                            return false;
                        }
                        
                        return true;
                    }
                }
                begin = end;
                end = end + 100;
            }
        } finally {
            pool.returnResource(jedis);
        }

        if (invalidJsons.size() > 0) { // 删除格式不正确的redis字段值
            jedis.zrem(URLBASE, invalidJsons.toArray(new String[] {}));
        }

        return false;
    }

    /**
     * 提交任务给worker节点
     * 
     * @param jobConf
     * @throws CrawlerException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public void submitToWorker(JobConf jobConf) throws CrawlerException,
                    InterruptedException, ExecutionException, NoRouteToHostException {
        boolean flag = true;
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        while (flag) {
            WorkerConf worker = WorkerScheduler.getWorker();

            String url = "http://" + worker.getHostPort() + "/" + SlavePath.PATH + "/"
                            + SlavePath.JOB_RESOURCE_PATH;

            jobConf.setWorkerId(worker.getWorkerId());

            com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client
                            .create();
            WebResource webResource = client.resource(url);

            String json = gson.toJson(jobConf, JobConf.class);
            ClientResponse response = null;
            try {
                LOG.info("Distributing Job: " + jobConf.toString());
                response = webResource.type("application/json").post(
                                ClientResponse.class, json);
                // TODO: 处理worker节点RejectedExecutionException 任务满的异常， 应该等待再分配
//                JobCode jobCode = response.getEntity(JobCode.class);
//                if (jobCode.getCode() != ErrorCode.SUCCESS.code) {
//                    LOG.warn(jobCode.getMessage());
//                }
            } catch (ClientHandlerException e) {
                LOG.error("URL为" + url + "的slave不能执行任务,他可能很忙,也可能挂了. 准备换一个slave试试...", e);
                WorkerScheduler.removeWorker(worker);
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
        throw new CrawlerException(ErrorCode.SYSTEM_ERROR, "No Workers available");
    }

    // TODO: 查询任务
    public Page<JobConf> get(String query, int start, int end) {
        if (StringUtils.isEmpty(query)) {
            
        }
        return null;
    }

    public void delete(int jobId) throws CrawlerException {
        Jedis jedis = pool.getResource();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        long count = 0, begin = 0, end = 100;
        count = jedis.zcard(URLBASE);
        boolean found = false;
        try {
            while (begin < count && !found) {
                Set<String> set = jedis.zrevrange(URLBASE, begin, end);
                if (CollectionUtils.isEmpty(set))
                    break;
                for (String json : set) {
                    JobConf jc = null;
                    try {
                        jc = gson.fromJson(json, JobConf.class);
                    } catch (JsonSyntaxException jse) {
                        continue;
                    }
                    if (jobId == jc.getJobId()) {
                        jedis.zrem(URLBASE, json);
                        found = true;
                    }
                }
                begin = end;
                end = end + 100;
            }
        } finally {
            pool.returnResource(jedis);
        }

        if (!found)
            throw new CrawlerException(ErrorCode.SYSTEM_ERROR,
                            "Job not found with jobId:" + jobId);
    }
}
