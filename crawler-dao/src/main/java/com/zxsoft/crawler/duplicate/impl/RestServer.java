package com.zxsoft.crawler.duplicate.impl;

public class RestServer {

	private String user;
    private String password;
    private String host;
    private int port;
    
    
	public RestServer(String user, String password, String host, int port) {
	    super();
	    this.user = user;
	    this.password = password;
	    this.host = host;
	    this.port = port;
    }
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
    
    
}
