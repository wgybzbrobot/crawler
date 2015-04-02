package com.zxsoft.crawler.master.impl;

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

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.IPUtil;
import com.zxsoft.crawler.api.JobCode;
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.api.Machine;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.master.SlaveCache;
import com.zxsoft.crawler.master.SlaveManager;
import com.zxsoft.crawler.master.SlaveStatus;
import com.zxsoft.crawler.master.SlaveStatus.State;
import com.zxsoft.crawler.slave.SlavePath;
import com.zxsoft.crawler.urlbase.JobCreator;

public class RAMSlaveManager implements SlaveManager {

    private static Logger LOG = LoggerFactory.getLogger(RAMSlaveManager.class);

    private class MyPoolExecutor extends ThreadPoolExecutor {
        public MyPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                        TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }
    }

    private final JobCreator jobCreator;

    public RAMSlaveManager(JobCreator inspectJobCreator) {
        super();
        this.jobCreator = inspectJobCreator;
    }

    public static Set<Machine> runningMachines = new HashSet<Machine>(100);

    static {
        List<Machine> list = SlaveCache.machines;
        if (CollectionUtils.isEmpty(list)) {
            throw new NullPointerException(
                            "No Slave machines found, please configure it.");
        }
        for (Machine machine : list) {
            runningMachines.add(machine);
        }
    }

    private static SlaveScheduler scheduler = SlaveScheduler.getInstance();

    @Override
    @SuppressWarnings("fallthrough")
    public List<SlaveStatus> list() throws Exception {
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
     * 选择slave节点
     */
    public ScoredMachine chooseSlave() {
        ScoredMachine sm = scheduler.selectSlave();
        return sm;
    }

    /**
     * 新增任务，若是巡检任务，首先判断是否在url队列中已经存在。 直接提交给worker节点。
     */
    @Override
    public JobCode create(JobConf jobConf) {

        if (JobType.NETWORK_INSPECT.equals(jobConf.getJobType())) {
            try {
                jobCreator.addToRedis(jobConf);
            } catch (CrawlerException e) {
                return new JobCode(e.code(), e.getMessage());
            }
        }

        try {
            jobCreator.submitToWorker(jobConf);
            return new JobCode(ErrorCode.SUCCESS.code, "create job success");
        } catch (CrawlerException e) {
            return new JobCode(e.code(), e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            return new JobCode(ErrorCode.SYSTEM_ERROR.code, e.getMessage());
        }
    }

}
