package com.zxsoft.crawler;

import java.util.Map;

import com.zxsoft.crawler.JobManager.JobType;

public class JobStatus {
	public static enum State {
		IDLE, RUNNING, FINISHED, FAILED, KILLED, STOPPING, KILLING, ANY
	};

	public String id;
	public JobType type;
	public Map<String, Object> args;
	public Map<String, Object> result;
	public CrawlTool tool;
	public State state;
	public String msg;
	
	public JobStatus() {}

	public JobStatus(String id, JobType type, Map<String, Object> args, State state,
	        String msg) {
		this.id = id;
		this.type = type;
		this.args = args;
		this.state = state;
		this.msg = msg;
	}

}
