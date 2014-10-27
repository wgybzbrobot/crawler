package com.zxsoft.crawler.web.controller.crawler;

import java.io.Serializable;
import java.util.Map;

public class JobStatus implements Serializable {
	/**
	 * 
	 */
    private static final long serialVersionUID = -6304887236506896262L;

	public static enum State {
		IDLE, RUNNING, FINISHED, FAILED, KILLED, STOPPING, KILLING, ANY
	};
	public static enum JobType {
		NETWORK_SEARCH, NETWORK_INSPECT
	};

	public String id;
	public JobType type;
	public Map<String, Object> args;
	public Map<String, Object> result;
	public State state;
	public String msg;

	public JobStatus() {
	}

	public JobStatus(String id, JobType type, Map<String, Object> args, State state, String msg) {
		this.id = id;
		this.type = type;
		this.args = args;
		this.state = state;
		this.msg = msg;
	}

	public String toString() {
		return "helooooooooo";
	}

}