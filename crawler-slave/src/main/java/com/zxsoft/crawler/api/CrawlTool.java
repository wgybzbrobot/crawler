package com.zxsoft.crawler.api;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.zxsoft.crawler.common.JobConf;

public abstract class CrawlTool  implements Serializable {

	/**
	 * 
	 */
    private static final long serialVersionUID = 7157000320559623897L;
	public HashMap<String, Object> results = new HashMap<String, Object>();
	public Map<String, Object> status = Collections
	        .synchronizedMap(new HashMap<String, Object>());
	public CrawlJob currentJob;
	public int numJobs;
	public int currentJobNum;

	public CrawlTool() {
	}


	public boolean stopJob() {
		return true;
	}

	/**
	 * Kill the job immediately. Clients should assume that any results that the
	 * job produced so far are in inconsistent state or missing.
	 * 
	 * @return true if succeeded, false otherwise.
	 * @throws Exception
	 */
	public boolean killJob() throws Exception {
//		if (currentJob != null && !currentJob.isComplete()) {
//			try {
//				currentJob.killJob();
//				return true;
//			} catch (Exception e) {
//				e.printStackTrace();
//				return false;
//			}
//		}
		return false;
	}

	public Map<String, Object> getStatus() {
		return null;
	}

	public float getProgress() {
		return 1;
	}

	public abstract Map<String, Object> run(JobConf jobConf) throws Exception;

}
