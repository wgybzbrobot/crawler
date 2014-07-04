package com.zxsoft.crawler.web.model;

import java.util.Date;

/**
 *  列表页验证返回的实体对象
 */
public class ThreadInfo {

	private String url;
	private String title;
	private Date releasedate;
	public ThreadInfo() {}
	public ThreadInfo(String url, String title, Date releasedate) {
	    super();
	    this.url = url;
	    this.title = title;
	    this.releasedate = releasedate;
    }
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getReleasedate() {
		return releasedate;
	}
	public void setReleasedate(Date releasedate) {
		this.releasedate = releasedate;
	}
	
	
}
