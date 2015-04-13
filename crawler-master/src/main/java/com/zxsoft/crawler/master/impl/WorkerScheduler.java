package com.zxsoft.crawler.master.impl;

import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.common.WorkerConf;

public class WorkerScheduler {
	
	private static Logger LOG = LoggerFactory.getLogger(WorkerScheduler.class);
	
    private  volatile static SortedMap<String,WorkerConf> workers  = new TreeMap<String,WorkerConf>();
	
	private WorkerScheduler() {}
	
	public static SortedMap<String,WorkerConf> getWorkers() {
	    return workers;
	}
	
	public static WorkerConf getWorker() throws CrawlerException {
		if (workers == null || workers.isEmpty())
			throw new CrawlerException(ErrorCode.SYSTEM_ERROR, "No workers");
		String key = workers.lastKey();
		WorkerConf worker = workers.get(key);
		 worker.setRunningCount(worker.getRunningCount() + 1);
		 workers.put(worker.getHostPort(), worker);
		return worker;
	}
	
	public static final void addWorker(WorkerConf worker) {
		workers.put(worker.getHostPort(),worker);
		LOG.debug("worker: " + worker.describe() + "\t running count:" + worker.getRunningCount());
	}

	public static final void removeWorker(WorkerConf worker) {
	    workers.remove(worker.getHostPort());
	    LOG.debug("worker: " + worker.describe());
	}
	
	   
}
