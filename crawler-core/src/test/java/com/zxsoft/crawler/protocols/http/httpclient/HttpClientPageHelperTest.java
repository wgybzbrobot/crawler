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
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.protocols.http.htmlunit.HtmlUnit;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClientPageHelper;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CrawlerServer.class)
public class HttpClientPageHelperTest {
	
	@Autowired
	HttpBase httpClient;
	
	@Test
	public void testLoadLastPage() throws IOException, PageBarNotFoundException {
		Document currentDoc = Jsoup.connect("http://tieba.baidu.com/p/3171682576").get();
		ProtocolOutput protocolOutput = httpClient.getProtocolOutputOfLastPage(currentDoc);
		Assert.notNull(protocolOutput);
		Assert.notNull(protocolOutput.getDocument());
		Assert.isTrue(protocolOutput.getDocument().location().matches("http://tieba.baidu.com/p/3171682576\\?pn=\\d+"));
	}
	
	@Test
	public void testLoadPrevPage() throws IOException, PrevPageNotFoundException {
		Document currentDoc = Jsoup.connect("http://tieba.baidu.com/p/3171682576?pn=3").get();
		ProtocolOutput protocolOutput = httpClient.getProtocolOutputOfPrevPage(3, currentDoc);
		Assert.notNull(protocolOutput);
		Assert.notNull(protocolOutput.getDocument());
		Assert.isTrue(protocolOutput.getDocument().location().equals("http://tieba.baidu.com/p/3171682576?pn=2"));
	}
	
	@Test
	public void testLoadNextPage() throws IOException, PageBarNotFoundException {
		Document currentDoc = Jsoup.connect("http://tieba.baidu.com/p/3171682576").get();
		ProtocolOutput protocolOutput = httpClient.getProtocolOutputOfNextPage(1, currentDoc);
		Assert.notNull(protocolOutput);
		Assert.notNull(protocolOutput.getDocument());
		Assert.isTrue(protocolOutput.getDocument().location().equals("http://tieba.baidu.com/p/3171682576?pn=2"));
	}
	
	

}
