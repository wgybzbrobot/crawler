package com.zxsoft.crawler.plugin.parse;

import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.zxsoft.crawler.CrawlerServer;
import com.zxsoft.crawler.parse.ParseTool;
import com.zxsoft.crawler.parse.ParserController;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.CrawlerConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CrawlerServer.class)
public class ForumParserTest {

	@Autowired
	private HttpFetcher httpFetcher;

	private Configuration conf;
	@Autowired
	private ApplicationContext context;

	@Before
	public void setUp() {
		conf = CrawlerConfiguration.create();
		ParseTool.init(context);
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
		
		ParserController parseUtil = new ParserController(conf);

		Parser parser = new ForumParser();
		parser.parse(page);
	}

	@Test
	public void testParseTianYa() throws Exception {
		String urlStr = "http://bbs.tianya.cn/post-free-4502784-1.shtml";
		ProtocolOutput protocolOutput = httpFetcher.fetch(urlStr, false);
		Assert.notNull(protocolOutput);
		Document document = protocolOutput.getDocument();
		Assert.notNull(document);
		WebPage page = new WebPage("title", urlStr, System.currentTimeMillis(), document);
		page.setAjax(false);
		
		ParserController parseUtil = new ParserController(conf);
		
		Parser parser = new ForumParser();
		parser.parse(page);
	}
}
