package com.zxsoft.crawler.storage;

public class Account implements java.io.Serializable {

	/**
	 * 
	 */
    private static final long serialVersionUID = 5502242494979474026L;
	private String id;
	private String username;
	private String password;

	public Account() {
	}

	public Account(String id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}


	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
