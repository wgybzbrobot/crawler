package com.zxsoft.crawler.web.model;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

public class NewsConf {

	@NotEmpty
	private String testUrl;
	
	@Valid
	private ListConf listConf;
	@Valid
	private NewsDetailConf detailConf;
	
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
	public NewsDetailConf getDetailConf() {
		return detailConf;
	}
	public void setDetailConf(NewsDetailConf detailConf) {
		this.detailConf = detailConf;
	}
	
}
