package com.zxsoft.crawler.parse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FetchStatus {
	
	public enum Status {
		PROTOCOL_FAILURE, PARSING, SUCCESS, OUTPUT_FAILURE, PARSE_FAILURE, NOT_CHANGE, CONF_ERROR
	}
	
	private String url;
	private int code;
	private String message;
	private String description;
	
	/**
	 * 抓取的记录数
	 */
	private int count;
	private Status status;
	
	public FetchStatus() {}
	
	public FetchStatus(String url, int code, Status status) {
	    super();
	    this.url = url;
	    this.code = code;
	    this.status = status;
    }

	public FetchStatus(String url, int code, Status status, int count) {
		super();
		this.url = url;
		this.code = code;
		this.status = status;
		this.count = count;
	}

	public FetchStatus(String url, int code, Status status, int count, String msg) {
		super();
		this.url = url;
		this.code = code;
		this.status = status;
		this.count = count;
		this.message = msg;
	}

	public FetchStatus(String url, String description) {
		this.description = description;
		this.url = url;
	}
	
	public FetchStatus(String message) {
	    super();
	    this.message = message;
    }
	
	public String toString() {
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(this); 
		return json;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCount() {
		return count;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
