package com.zxsoft.crawler.storage;

import java.io.Serializable;

import org.jsoup.nodes.Document;

public class WebPage implements Serializable {

	private static final long serialVersionUID = 6780885020548185632L;
	private String baseUrl;
	private int status;
	private long fetchTime;
	private long prevFetchTime;
	private int fetchInterval;
	private int retriesSinceFetch;
	private Document document;

	private String title;
	private boolean ajax;
	/**
	 * 网站类型
	 * @see Proxy
	 */
	private String type;
	
	public WebPage () {}
	
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
	
	public WebPage(String title, String baseUrl, long fetchTime, Document document) {
		super();
		this.title = title;
		this.baseUrl = baseUrl;
		this.fetchTime = fetchTime;
		this.document = document;
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

	public int getFetchInterval() {
		return fetchInterval;
	}

	public void setFetchInterval(int fetchInterval) {
		this.fetchInterval = fetchInterval;
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
