package com.zxsoft.crawler;
import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.zxsoft.crawler.parse.ParserController;
import com.zxsoft.crawler.parse.ParserNotFoundException;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.storage.WebPage;

public class CrawlJob implements Runnable {

	private static Logger LOG = LoggerFactory.getLogger(CrawlJob.class);
	private ApplicationContext ctx;
	private Configuration conf;
	private String url;
	private WebPage page;
	public CrawlJob (ApplicationContext ctx, WebPage page, Configuration conf) {
		this.ctx = ctx;
		this.page = page;
		this.conf = conf;
	}

	@Autowired
	private ParserController parseUtil;
	
	public void run() {
		url = page.getBaseUrl();
		if (url == null) {
			return;
		}
		boolean ajax = page.isAjax();
		HttpFetcher httpFetcher = ctx.getBean(HttpFetcher.class);
		ProtocolOutput output = httpFetcher.fetch(url, ajax);
		
		if (output == null || !output.getStatus().isSuccess()) {
			return;
		}
		
		Document document = output.getDocument();
		page.setDocument(document);
		
		ParserController parseUtil = new ParserController(conf);
		try {
			parseUtil.parse(page);
		} catch (ParserNotFoundException e) {
			LOG.error("Parser Not Found " + url, e);
			return;
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
