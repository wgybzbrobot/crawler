package com.zxsoft.crawler.parse;

public class FetchStatus {
	
	public enum Status {
		PROTOCOL_FAILURE, PARSING, SUCCESS, OUTPUT_FAILURE, PARSE_FAILURE, NOT_CHANGE, CONF_ERROR
	}
	
	private String url;
	private String message;
	/**
	 * 抓取的记录数
	 */
	private int count;
	private Status status;
	
	public FetchStatus() {}
	
	public FetchStatus(String url) {
		this.url = url;
	}
	
	public FetchStatus(String url, String message) {
	    super();
	    this.url = url;
	    this.message = message;
    }
	
	public int getCount() {
		return count;
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
