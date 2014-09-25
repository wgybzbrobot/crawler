package com.zxsoft.crawler.storage;

import java.io.Serializable;

import org.jsoup.nodes.Document;

public class WebPage implements Serializable {

	private static final long serialVersionUID = 6780885020548185632L;
	
	public enum JOB_TYPE {
		/** 网络巡检 */
		NETWORK_INSPECT, 
		/** 全网搜索 */
		NETWORK_SEARCH
	}
	
	private String baseUrl; 
	private String keyword; 
	
	private String listUrl; // 列表页URL
	
	private int status;
	private long fetchTime;
	private long prevFetchTime;
	private long interval;
	private int retriesSinceFetch;
	private Document document;

	private JOB_TYPE jobType;
	
	private String title;
	private boolean ajax;
	/**
	 * 网站类型
	 * @see Proxy
	 */
	private String type;
	
	public WebPage () {}
	
	public WebPage (String baseUrl, boolean ajax) {
		super();
		this.baseUrl = baseUrl;
		this.ajax =  ajax;
	}
	
	public WebPage (String keyword, String baseUrl, String urlType) {
		this.keyword = keyword;
		this.baseUrl = baseUrl;
		this.type = urlType;
	}
	
	public WebPage(String baseUrl, long fetchTime, Document document) {
	    super();
	    this.baseUrl = baseUrl;
	    this.fetchTime = fetchTime;
	    this.document = document;
    }
	
	public WebPage(String baseUrl, boolean  ajax, long prevFetchTime) {
		super();
		this.baseUrl = baseUrl;
		this. ajax =  ajax;
		this.prevFetchTime = prevFetchTime;
	}
	public WebPage(String baseUrl, String  urlType, long interval) {
		super();
		this.baseUrl = baseUrl;
		this.type = urlType;
		this.interval = interval;
	}
	
	public WebPage(String title, String baseUrl, long fetchTime, Document document) {
		super();
		this.title = title;
		this.baseUrl = baseUrl;
		this.fetchTime = fetchTime;
		this.document = document;
	}
	
	
	public String getListUrl() {
		return listUrl;
	}

	public void setListUrl(String listUrl) {
		this.listUrl = listUrl;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public JOB_TYPE getJobType() {
		return jobType;
	}

	public void setJobType(JOB_TYPE jobType) {
		this.jobType = jobType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isAjax() {
		return ajax;
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getFetchTime() {
		return fetchTime;
	}

	public void setFetchTime(long fetchTime) {
		this.fetchTime = fetchTime;
	}

	public long getPrevFetchTime() {
		return prevFetchTime;
	}

	public void setPrevFetchTime(long prevFetchTime) {
		this.prevFetchTime = prevFetchTime;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public int getRetriesSinceFetch() {
		return retriesSinceFetch;
	}

	public void setRetriesSinceFetch(int retriesSinceFetch) {
		this.retriesSinceFetch = retriesSinceFetch;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

}
