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
public class TieBaParserTest {

	@Autowired
	private HttpFetcher httpFetcher;

	private Configuration conf;
	@Autowired
	private ApplicationContext context;

	@Before
	public void setUp() {
		conf = CrawlerConfiguration.create();
	}

	@Test
	public void testParse() throws Exception {
		String urlStr = "http://tieba.baidu.com/p/3182601829";
		ProtocolOutput protocolOutput = httpFetcher.fetch(urlStr);
		Assert.notNull(protocolOutput);
		Document document = protocolOutput.getDocument();
		Assert.notNull(document);
		WebPage page = new WebPage("title", urlStr, System.currentTimeMillis(), document);
		page.setAjax(false);
		
		ParseTool.init(context);
		
		ParserController parseUtil = new ParserController(conf);

		Parser parser = new TieBaParser();
		parser.parse(page);
	}
}
