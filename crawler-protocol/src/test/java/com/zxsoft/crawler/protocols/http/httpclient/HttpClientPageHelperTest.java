package com.zxsoft.crawler.protocols.http.httpclient;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.thinkingcloud.framework.util.Assert;

import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

public class HttpClientPageHelperTest {
	
	HttpBase httpClient = new HttpClient();
	
	@Test
	public void testLoadLastPage() throws IOException, PageBarNotFoundException {
		Document currentDoc = Jsoup.connect("http://tieba.baidu.com/p/3171682576").get();
		WebPage page = new WebPage();
		page.setDocument(currentDoc);
		page.setBaseUrl(currentDoc.location());
		page.setAjax(false);
		ProtocolOutput protocolOutput = httpClient.getProtocolOutputOfLastPage(page);
		Assert.notNull(protocolOutput);
		Assert.notNull(protocolOutput.getDocument());
		Assert.isTrue(protocolOutput.getDocument().location().matches("http://tieba.baidu.com/p/3171682576\\?pn=\\d+"));
	}
	
	@Test
	public void testLoadPrevPage() throws IOException, PrevPageNotFoundException, PageBarNotFoundException {
		Document currentDoc = Jsoup.connect("http://tieba.baidu.com/p/3171682576?pn=3").get();
		WebPage page = new WebPage();
		page.setDocument(currentDoc);
		page.setBaseUrl(currentDoc.location());
		page.setAjax(false);
		ProtocolOutput protocolOutput = httpClient.getProtocolOutputOfPrevPage(3, page);
		Assert.notNull(protocolOutput);
		Assert.notNull(protocolOutput.getDocument());
		Assert.isTrue(protocolOutput.getDocument().location().equals("http://tieba.baidu.com/p/3171682576?pn=2"));
	}
	
	@Test
	public void testLoadNextPage() throws IOException, PageBarNotFoundException {
		Document currentDoc = Jsoup.connect("http://tieba.baidu.com/p/3171682576").get();
		WebPage page = new WebPage();
		page.setDocument(currentDoc);
		page.setBaseUrl(currentDoc.location());
		page.setAjax(false);
		ProtocolOutput protocolOutput = httpClient.getProtocolOutputOfNextPage(1, page);
		Assert.notNull(protocolOutput);
		Assert.notNull(protocolOutput.getDocument());
		Assert.isTrue(protocolOutput.getDocument().location().equals("http://tieba.baidu.com/p/3171682576?pn=2"));
	}
	
	

}
