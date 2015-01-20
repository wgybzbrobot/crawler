package com.zxsoft.crawler.api;

import java.io.Serializable;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnore;


public class JobStatus implements Serializable {
	/**
	 * 
	 */
    private static final long serialVersionUID = -6304887236506896262L;

	public static enum State {
		IDLE, RUNNING, FINISHED, FAILED, KILLED, STOPPING, KILLING, ANY
	};

	public String id;
	public JobType type;
	public String comment;
	public Map<String, Object> args;
	public Map<String, Object> result;
	@JsonIgnore
	public CrawlTool tool;
	public State state;
	public String msg;

	public JobStatus() {
	}

	public JobStatus(String id, JobType type, String comment, Map<String, Object> args, State state, String msg) {
		this.id = id;
		this.type = type;
		this.comment = comment;
		this.args = args;
		this.state = state;
		this.msg = msg;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"id\":\"" + id + "\",");
		sb.append("\"type\":" + type.toString() + ",");
		sb.append("\"state\":" + state + ",");
		sb.append("\"msg\":" + msg + "}");
	        return sb.toString();
		
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("id", id);
//		map.put("jobType", type);
//		map.put("comment", comment);
//		map.put("args", args);
//		map.put("result", result);
//		map.put("state", state);
//		map.put("msg", msg);
//
//		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
//		String json = gson.toJson(map, Map.class);
//		return json;
	}

}
