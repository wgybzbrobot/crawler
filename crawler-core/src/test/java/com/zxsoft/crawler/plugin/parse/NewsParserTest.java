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
import com.zxsoft.crawler.parse.ParseStatus;
import com.zxsoft.crawler.parse.ParseTool;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.parse.ParserController;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.CrawlerConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CrawlerServer.class)
public class NewsParserTest {

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
	public void test() throws Exception {
		
	}
}
