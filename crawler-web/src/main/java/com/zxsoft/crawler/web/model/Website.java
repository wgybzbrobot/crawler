package com.zxsoft.crawler.web.model;

/**
 * 网站, 不是子网站
 * Example: http://www.baidu.com, 不是http://www.tieba.baidu.com
 */
public class Website {

	private String site;
	
	/**
	 * @see SiteType.type
	 */
	private String type;
	private String username;
	private String password;
	
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	
	
}
