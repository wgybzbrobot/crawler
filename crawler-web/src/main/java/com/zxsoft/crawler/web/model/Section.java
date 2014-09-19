package com.zxsoft.crawler.web.model;

/**
 * 版块
 * Example: http://tieba.baidu.com/f?kw=%D0%A6%BB%B0
 */
public class Section {

	/**
	 * 版块地址
	 */
	private String url;
	
	/**
	 * 版块所属的网站
	 * @see Website.site
	 */
	private String site;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}
	
	
}
