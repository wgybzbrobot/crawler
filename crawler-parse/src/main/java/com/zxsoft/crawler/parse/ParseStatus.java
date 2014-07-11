package com.zxsoft.crawler.parse;

public class ParseStatus {
	
	private String url;
	
	private String message;
	
	public ParseStatus() {}
	
	public ParseStatus(String url) {
		this.url = url;
	}
	
	public ParseStatus(String url, String message) {
	    super();
	    this.url = url;
	    this.message = message;
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
