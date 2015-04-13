package com.zxsoft.crawler.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 表示使用Master或Slave节点创建任务返回的状态
 */
public class JobCode {

	private int code;
	private String message;
	
	
	/**
	 * Master创建任务成功时返回。
	 */
	private String slave;
	
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

	public JobCode(int code, String msg, String slave) {
		this.code = code;
		this.message = msg;
		this.slave = slave;
	}
	
	public String getSlave() {
		return slave;
	}

	public void setSlave(String slave) {
		this.slave = slave;
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
