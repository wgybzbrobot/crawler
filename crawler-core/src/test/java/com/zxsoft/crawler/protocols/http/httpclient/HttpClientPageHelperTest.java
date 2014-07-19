package com.zxsoft.crawler.protocols.http.httpclient;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.zxsoft.crawler.CrawlerServer;
import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.protocols.http.htmlunit.HtmlUnit;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClientPageHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CrawlerServer.class)
public class HttpClientPageHelperTest {
	
	@Autowired
	HttpClientPageHelper pageHelper;
	
	@Autowired
	HttpFetcher httpFetcher;
	
	@Test
	public void testLoadLastPage() throws IOException {
		Document currentDoc = Jsoup.connect("http://tieba.baidu.com/p/3171682576").get();
		ProtocolOutput protocolOutput = pageHelper.loadLastPage(currentDoc, false);
		Assert.notNull(protocolOutput);
		Assert.notNull(protocolOutput.getDocument());
		Assert.isTrue("http://tieba.baidu.com/p/3171682576?pn=8".equals(protocolOutput.getDocument().location()));

		currentDoc = Jsoup.connect("http://tieba.baidu.com/p/3172028013").get();
		protocolOutput = pageHelper.loadLastPage(currentDoc, false);
		Assert.isNull(protocolOutput);
	}
	
	

}
