package com.zxsoft.crawler.master;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zxsoft.crawler.api.Params;

/**
 * 爬虫将根据这个对象信息进行循环抓取, 任务默认类型是<b>网络巡检</b>
 * @see Prey.urlType
 */
public class Prey implements Serializable {

	/**
	 * 
	 */
    private static final long serialVersionUID = -5812527440561239425L;

	/**
	 * 网站地址
	 */
	private String site;
	
	/**
	 * 版块地址
	 */
	private String url;
	
	/**
	 * @see Params.JOB_TYPE, 默认是网络巡检
	 */
	private String jobType = SlaveManager.JobType.NETWORK_INSPECT.toString();
	
	/**
	 * 代理类型
	 */
	private String proxyType;
	
	/**
	 * 每隔fetchinteval(分钟)进行循环抓取
	 */
	private int fetchinterval;
	
	/**
	 * 上次抓取时间，默认为0, 单位毫秒(ms)
	 */
	private long prevFetchTime;

	/**
	 * Only Constructor
	 * @param site
	 * @param url
	 * @param urlType
	 * @param fetchinterval
	 * @param prevFetchTime
	 */
	public Prey(String site, String url, String jobType, String proxyType, int fetchinterval, long prevFetchTime) {
	    super();
	    this.site = site;
	    this.url = url;
	    this.jobType = jobType;
	    this.proxyType = proxyType;
	    this.fetchinterval = fetchinterval;
	    this.prevFetchTime = prevFetchTime;
    }


	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}


	public String getProxyType() {
		return proxyType;
	}


	public void setProxyType(String proxyType) {
		this.proxyType = proxyType;
	}


	public int getFetchinterval() {
		return fetchinterval;
	}

	public void setFetchinterval(int fetchinterval) {
		this.fetchinterval = fetchinterval;
	}

	public long getPrevFetchTime() {
		return prevFetchTime;
	}

	public void setPrevFetchTime(long prevFetchTime) {
		this.prevFetchTime = prevFetchTime;
	}
	
	/**
	 * 返回Json
	 */
	@Override
	public String toString() {
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(this); 
		json = json.replaceAll("\u003d", "=");
		return json;
	}
}
