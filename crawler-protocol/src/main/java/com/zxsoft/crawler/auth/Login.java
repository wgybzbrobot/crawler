package com.zxsoft.crawler.auth;

public interface Login {

	/**
	 * @return login cookie
	 */
	String login(String username, String password) throws Exception ;
}
