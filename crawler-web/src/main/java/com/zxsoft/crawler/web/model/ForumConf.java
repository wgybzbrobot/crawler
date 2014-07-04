package com.zxsoft.crawler.web.model;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

public class ForumConf {

	
	@NotEmpty
	private String testUrl;
	
	@Valid
	private ListConf listConf;
	@Valid
	private ForumDetailConf forumDetailConf;
	
	
	public String getTestUrl() {
		return testUrl;
	}
	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}
	public ListConf getListConf() {
		return listConf;
	}
	public void setListConf(ListConf listConf) {
		this.listConf = listConf;
	}
	public ForumDetailConf getForumDetailConf() {
		return forumDetailConf;
	}
	public void setForumDetailConf(ForumDetailConf forumDetailConf) {
		this.forumDetailConf = forumDetailConf;
	}
	
	
	
}
