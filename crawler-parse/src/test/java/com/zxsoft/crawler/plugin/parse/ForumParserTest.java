package com.zxsoft.crawler.plugin.parse;

import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.thinkingcloud.framework.util.Assert;

import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.NetworkInspectParserController;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.CrawlerConfiguration;

public class ForumParserTest {

	private HttpFetcher httpFetcher;

	private Configuration conf;

	@Before
	public void setUp() {
		conf = CrawlerConfiguration.create();
	}

	@Test
	public void testParseAnhuiNews() throws Exception {
		String urlStr = "http://bbs.anhuinews.com/thread-1102159-1-2.html";
		ProtocolOutput protocolOutput = httpFetcher.fetch(urlStr, false);
		Assert.notNull(protocolOutput);
		Document document = protocolOutput.getDocument();
		Assert.notNull(document);
		WebPage page = new WebPage("title", urlStr, System.currentTimeMillis(), document);
		page.setAjax(false);
		
		NetworkInspectParserController parseUtil = new NetworkInspectParserController(conf);

		Parser parser = new ForumParser();
		parser.parse(page);
	}

	@Test
	public void testParseTianYa() throws Exception {
		String urlStr = "http://bbs.tianya.cn/post-free-3256021-1.shtml";
		ProtocolOutput protocolOutput = httpFetcher.fetch(urlStr, false);
		Assert.notNull(protocolOutput);
		Document document = protocolOutput.getDocument();
		Assert.notNull(document);
		WebPage page = new WebPage("title", urlStr, System.currentTimeMillis(), document);
		page.setAjax(false);
		
		NetworkInspectParserController parseUtil = new NetworkInspectParserController(conf);
		
		Parser parser = new ForumParser();
		FetchStatus status = parser.parse(page);
		Assert.isTrue(status.getStatus() == FetchStatus.Status.SUCCESS);
	}
}
