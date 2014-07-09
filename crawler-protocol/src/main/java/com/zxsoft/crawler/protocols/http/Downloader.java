package com.zxsoft.crawler.protocols.http;

import java.io.IOException;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.zxsoft.crawler.storage.Seed;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.storage.WebPageMy;
import com.zxsoft.crawler.util.Utils;

public class Downloader {

	private static Logger LOG = LoggerFactory.getLogger(Downloader.class);


	/**
	 * 访问URL
	 */
	public WebPage connect(String url) {

		LOG.info("connecting " + url);

		WebPage page = new WebPage();
		
		SmartLoader loader = new SmartLoader();
		return page;
	}


}
