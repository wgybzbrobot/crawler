package com.zxsoft.proxy;

import java.io.Serializable;

public class Proxy implements Serializable {

	private static final long serialVersionUID = 518169468536248943L;
	private String ip;
	private int port;
	private String username;
	private String password;
	/**
	 * 用于网站的类型
	 * @see SiteType.type 
	 * @see WebPage.type
	 */
	private String type;

	public Proxy() {
	}

	public Proxy(String type, String ip, int port, String username, String password) {
		super();
		this.username = username;
		this.password = password;
		this.ip = ip;
		this.port = port;
		this.type = type;
	}

	public String getTargetType() {
		return type;
	}

	public void setTargetType(String targetType) {
		this.type = targetType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String toString() {
		return username + ":" + password + ":" + ip + ":" + port;
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

	public String getIp() {
		return ip;
	}

	public void setIp(String host) {
		this.ip = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
