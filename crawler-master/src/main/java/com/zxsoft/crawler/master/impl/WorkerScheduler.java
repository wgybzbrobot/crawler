package com.zxsoft.crawler.master.impl;

import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.common.WorkerConf;

public class WorkerScheduler {
	
	private static Logger LOG = LoggerFactory.getLogger(WorkerScheduler.class);
	
    private   static ConcurrentMap<String, WorkerConf> workers  = new ConcurrentHashMap<String, WorkerConf>();
	
	private WorkerScheduler() {}
	
	public static ConcurrentMap<String, WorkerConf> getWorkers() {
	    return workers;
	}
	
	public static WorkerConf getWorker() throws CrawlerException {
		if (workers == null || workers.isEmpty())
			throw new CrawlerException(ErrorCode.SYSTEM_ERROR, "No workers");
		
		Set<String> keys = workers.keySet();
		
		int minCount = 100;
		WorkerConf worker = null;
		
		for (String key : keys) {
            WorkerConf _w = workers.get(key);
            if (minCount > _w.getRunningCount() && 100 > _w.getRunningCount()) {
                minCount = _w.getRunningCount();
                worker = _w;
            }
        }
		
		if (worker == null)
		    throw new CrawlerException(ErrorCode.SYSTEM_ERROR, "No workers available");

		worker.setRunningCount(worker.getRunningCount() + 1);
		
		workers.put(worker.getHostPort(), worker);
		
		return worker;
	}
	
	public static final void addWorker(WorkerConf worker) {
	   
	        workers.put(worker.getHostPort(),worker);

	        for (String key : workers.keySet()) {
	            WorkerConf w = workers.get(key);
	            LOG.debug("worker: " + w.describe() + "\t running count:" + w.getRunningCount());
                
            }
		
	}

	public static final void removeWorker(WorkerConf worker) {
	    workers.remove(worker.getHostPort());
	    LOG.debug("worker: " + worker.describe());
	}
	
	   
}
