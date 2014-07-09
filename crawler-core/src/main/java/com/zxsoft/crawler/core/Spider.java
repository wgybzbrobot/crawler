package com.zxsoft.crawler.core;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.protocols.http.Downloader;
import com.zxsoft.crawler.protocols.http.SmartLoader;
import com.zxsoft.crawler.storage.Seed;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.tools.Tools;
import com.zxsoft.crawler.util.parse.ParseUtil;
import com.zxsoft.crawler.util.parse.ParserNotFoundException;

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
		
		SmartLoader loader = new SmartLoader();
		try {
	        Document document = loader.load(url);
	        LOG.info(document.toString());
        } catch (Exception e) {
	        e.printStackTrace();
        }

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
