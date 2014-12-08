package com.zxsoft.crawler.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JobCode {

	private int code;
	private String message;
	
	@Override
	public String toString() {
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(this); 
		return json;
	}
	
	public JobCode(int code, String msg) {
		this.code = code;
		this.message = msg;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
