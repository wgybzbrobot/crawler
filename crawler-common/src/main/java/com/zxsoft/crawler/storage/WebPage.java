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
	private long modifiedTime;
	private long prevModifiedTime;
	private Document document;

	private String title;
	private boolean ajax;
	
	
	public WebPage () {}
	
	public WebPage(String baseUrl, long fetchTime, Document document) {
	    super();
	    this.baseUrl = baseUrl;
	    this.fetchTime = fetchTime;
	    this.document = document;
    }
	
	public WebPage(String title, String baseUrl, long fetchTime, Document document) {
		super();
		this.title = title;
		this.baseUrl = baseUrl;
		this.fetchTime = fetchTime;
		this.document = document;
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

	public long getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public long getPrevModifiedTime() {
		return prevModifiedTime;
	}

	public void setPrevModifiedTime(long prevModifiedTime) {
		this.prevModifiedTime = prevModifiedTime;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

}
