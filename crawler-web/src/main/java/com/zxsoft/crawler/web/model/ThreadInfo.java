package com.zxsoft.crawler.web.model;

import java.util.Date;

/**
 *  列表页验证返回的实体对象
 */
public class ThreadInfo {

	private String url;
	private String title;
	private Date update;
	public ThreadInfo() {}
	public ThreadInfo(String url, String title, Date update) {
	    super();
	    this.url = url;
	    this.title = title;
	    this.update = update;
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
	public Date getupdate() {
		return update;
	}
	public void setupdate(Date update) {
		this.update = update;
	}
	
	
}
