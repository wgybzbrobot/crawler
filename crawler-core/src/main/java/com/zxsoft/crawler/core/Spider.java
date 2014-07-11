package com.zxsoft.crawler.core;


import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClient;
import com.zxsoft.crawler.protocols.http.proxy.ProxyRandom;
import com.zxsoft.crawler.storage.WebPage;


public class Spider implements Runnable {

	private static Logger LOG = LoggerFactory.getLogger(Spider.class);

	private Configuration conf;
	private String url;
	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	public Spider(String url) {
		this.url = url;
	}
	
	public void run() {
		if (url == null) {
			return;
		}
//		Downloader downloader = new Downloader();
//		WebPage page = null;
//		page = downloader.connect(url);
		
//		SmartLoader loader = new SmartLoader();
//		try {
//	        Document document = loader.load(url);
//	        LOG.info(document.toString());
//        } catch (Exception e) {
//	        e.printStackTrace();
//        }
		Proxy proxy = ProxyRandom.random(); 
		WebPage page = new WebPage();
		
		HttpBase httpBase = new HttpClient();
		httpBase.getProtocolOutput(url, proxy, page);

//		if (page == null)
//			return;
//		ParseUtil parseUtil = new ParseUtil();
//		parseUtil.setConf(conf);
//		try {
//			parseUtil.parse(page);
//		} catch (ParserNotFoundException e) {
//			LOG.error("Parser Not Found " + url, e);
//			return;
//		}
	}
}
