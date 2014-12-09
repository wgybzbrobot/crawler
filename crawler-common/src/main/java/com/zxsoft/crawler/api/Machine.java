package com.zxsoft.crawler.api;

/**
 * 说明Master或Slave所在机器的ip和port。
 */
public class Machine {
	
	private String id;
	private String ip;
	private int port;
	private String comment;
	
	public Machine () {}
	
	
	public Machine(String id, String ip, int port, String comment) {
	    super();
	    this.id = id;
	    this.ip = ip;
	    this.port = port;
	    this.comment = comment;
    }


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String toString() {
		return id + "(" + ip + ":" + port + " -- " + comment + ")";
	}

}
