package com.zxsoft.crawler.cache.entry;

public class Proxy implements CacheEntry {

	private String username;
	private String password;
	private String host;
	private int port;
	
	
	public Proxy(String username, String password, String host, int port) {
	    super();
	    this.username = username;
	    this.password = password;
	    this.host = host;
	    this.port = port;
    }
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
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
