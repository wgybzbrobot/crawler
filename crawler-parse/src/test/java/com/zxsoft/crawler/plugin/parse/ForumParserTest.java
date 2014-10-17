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

	private HttpFetcher httpFetcher ;

	private Configuration conf;

	@Before
	public void setUp() {
		conf = CrawlerConfiguration.create();
		httpFetcher = new HttpFetcher(conf);
	}

	@Test
	public void testParseAnhuiNews() throws Exception {
		String urlStr = "http://bbs.anhuinews.com/thread-1102159-1-2.html";
		WebPage page = new WebPage(urlStr, false);
		ProtocolOutput protocolOutput = httpFetcher.fetch(page);
		Assert.notNull(protocolOutput);
		Document document = protocolOutput.getDocument();
		Assert.notNull(document);
		page = new WebPage("title", urlStr, System.currentTimeMillis(), document);
		page.setAjax(false);
		
		NetworkInspectParserController parseUtil = new NetworkInspectParserController(conf);

		Parser parser = new ForumParser();
		parser.parse(page);
	}

	@Test
	public void testParseTianYa() throws Exception {
		String urlStr = "http://bbs.tianya.cn/post-free-4697781-1.shtml";
		WebPage page = new WebPage(urlStr, false);
		page = new WebPage("title", urlStr, System.currentTimeMillis(), null);
		page.setAjax(false);
		page.setPrevFetchTime(6 * 60 * 60 * 1000);
		
		NetworkInspectParserController parseUtil = new NetworkInspectParserController(conf);
		
		Parser parser = new ForumParser();
		FetchStatus status = parser.parse(page);
		Assert.isTrue(status.getStatus() == FetchStatus.Status.SUCCESS);
	}
}
