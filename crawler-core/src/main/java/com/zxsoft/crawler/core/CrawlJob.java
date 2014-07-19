package com.zxsoft.crawler.core;
import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.zxsoft.crawler.parse.ParseUtil;
import com.zxsoft.crawler.parse.ParserNotFoundException;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.storage.WebPage;

//@Component
//@Scope("prototype")
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
	private ParseUtil parseUtil;
	
	public void run() {
		url = page.getBaseUrl();
		if (url == null) {
			return;
		}
		boolean ajax = page.isAjax();
		HttpFetcher httpFetcher = ctx.getBean(HttpFetcher.class);
		ProtocolOutput output = httpFetcher.fetch(url, ajax);
		Document document = output.getDocument();
//		System.out.println(document.html());
		page.setDocument(document);
		
		ParseUtil parseUtil = new ParseUtil(ctx, conf);
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
