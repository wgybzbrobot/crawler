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
	HttpFetcher httpClient;
	
	@Test
	public void testLoadLastPage() throws IOException {
		Document currentDoc = Jsoup.connect("http://tieba.baidu.com/p/3171682576").get();
		ProtocolOutput protocolOutput = pageHelper.loadLastPage(currentDoc);
		Assert.notNull(protocolOutput);
		Assert.notNull(protocolOutput.getDocument());
		Assert.isTrue(protocolOutput.getDocument().location().matches("http://tieba.baidu.com/p/3171682576\\?pn=\\d+"));
	}
	
	@Test
	public void testLoadPrevPage() throws IOException {
		Document currentDoc = Jsoup.connect("http://tieba.baidu.com/p/3171682576?pn=3").get();
		ProtocolOutput protocolOutput = pageHelper.loadPrevPage(3, currentDoc);
		Assert.notNull(protocolOutput);
		Assert.notNull(protocolOutput.getDocument());
		Assert.isTrue(protocolOutput.getDocument().location().equals("http://tieba.baidu.com/p/3171682576?pn=2"));
	}
	
	@Test
	public void testLoadNextPage() throws IOException {
		Document currentDoc = Jsoup.connect("http://tieba.baidu.com/p/3171682576").get();
		ProtocolOutput protocolOutput = pageHelper.loadNextPage(1, currentDoc);
		Assert.notNull(protocolOutput);
		Assert.notNull(protocolOutput.getDocument());
		Assert.isTrue(protocolOutput.getDocument().location().equals("http://tieba.baidu.com/p/3171682576?pn=2"));
	}
	
	

}
