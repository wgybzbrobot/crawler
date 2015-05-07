package com.zxsoft.crawler.master.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.api.JobCode;
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.api.Machine;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.common.WorkerConf;
import com.zxsoft.crawler.master.SlaveManager;
import com.zxsoft.crawler.urlbase.JobManager;
import com.zxsoft.crawler.urlbase.Page;

public class RAMSlaveManager implements SlaveManager {

    private static Logger LOG = LoggerFactory.getLogger(RAMSlaveManager.class);

    private final JobManager jobManager;

    public RAMSlaveManager(JobManager jobManager) {
        super();
        this.jobManager = jobManager;
    }

    public static Set<Machine> runningMachines = new HashSet<Machine>(100);

    /**
     * 新增任务，若是巡检任务，首先判断是否在url队列中已经存在。 直接提交给worker节点。
     */
    @Override
    public JobCode create(JobConf jobConf) {
        
        //测试进入详细页抽取数据
        jobConf.setGoInto(true);
        if (StringUtils.isEmpty(jobConf.getIdentify_md5()))
            jobConf.setIdentify_md5("gointo");
        
        if (JobType.NETWORK_SEARCH.equals(jobConf.getJobType())) {
            jobConf.setRecurrence(false);
        } else if (JobType.NETWORK_INSPECT.equals(jobConf.getJobType())) {
            jobConf.setRecurrence(true);
        } else {
            return new JobCode(ErrorCode.CONF_ERROR.code, "No job type specified");
        }

        try {
            jobManager.addToRedis(jobConf);
            return new JobCode(ErrorCode.SUCCESS.code, "create job success");
        } catch (CrawlerException e) {
            return new JobCode(e.code(), e.getMessage());
        } 
    }

    @Override
    public List<WorkerConf> getWorkers() {
        List<WorkerConf> list = new ArrayList<WorkerConf>();
        ConcurrentMap<String,WorkerConf> confs = WorkerScheduler.getWorkers();
        Set<String> keys = confs.keySet();
        for (String key : keys) {
            WorkerConf worker = confs.get(key);
            list.add(worker);
        }
        return list;
    }

    @Override
    public void workerTick(WorkerConf worker) {
        WorkerScheduler.addWorker(worker);
    }

    @Override
    public Page<JobConf> getJobs(String query, int start, int end) {
        Page<JobConf> page =  jobManager.get(query, start, end);
        return page;
    }

    @Override
    public void deleteJob(int jobId) throws CrawlerException {
        jobManager.delete(jobId);
    }

}
