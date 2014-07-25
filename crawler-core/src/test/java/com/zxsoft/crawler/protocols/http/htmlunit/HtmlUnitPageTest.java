package com.zxsoft.crawler.protocols.http.htmlunit;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.zxsoft.crawler.CrawlerServer;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CrawlerServer.class)
public class HtmlUnitPageTest {
	
	@Autowired
	HttpBase htmlUnit;
	
	@Autowired
	HttpFetcher httpFetcher;
	
	@Test
	public void testLoadLastPage() throws IOException, PageBarNotFoundException {
		ProtocolOutput protocolOutput = httpFetcher.fetch("http://roll.news.sina.com.cn/s/channel.php", true);
		Assert.notNull(protocolOutput);
		Document currentDoc = protocolOutput.getDocument();
		Assert.notNull(currentDoc);
		protocolOutput = htmlUnit.getProtocolOutputOfLastPage(currentDoc);
		Assert.notNull(protocolOutput);
		currentDoc = protocolOutput.getDocument();
		Assert.notNull(currentDoc);
		System.out.println(currentDoc.html());
	}
	
	@Test
	public void testLoadNextPage() throws IOException, PageBarNotFoundException {
		ProtocolOutput protocolOutput = httpFetcher.fetch("http://roll.news.sina.com.cn/s/channel.php", true);
		Assert.notNull(protocolOutput);
		Document currentDoc = protocolOutput.getDocument();
		Assert.notNull(currentDoc);
		protocolOutput = htmlUnit.getProtocolOutputOfNextPage(1, currentDoc);
		Assert.notNull(protocolOutput);
		currentDoc = protocolOutput.getDocument();
		Assert.notNull(currentDoc);
		System.out.println(currentDoc.html());
	}
	
	@Test
	public void testLoadPrevPage() throws IOException, PrevPageNotFoundException {
		ProtocolOutput protocolOutput = httpFetcher.fetch("http://roll.news.sina.com.cn/s/channel.php#col=89&spec=&type=&ch=&k=&offset_page=0&offset_num=0&num=60&asc=&page=2", true);
		Assert.notNull(protocolOutput);
		Document currentDoc = protocolOutput.getDocument();
		Assert.notNull(currentDoc);
		
		protocolOutput = htmlUnit.getProtocolOutputOfPrevPage(2, currentDoc);
		Assert.notNull(protocolOutput);
		currentDoc = protocolOutput.getDocument();
		Assert.notNull(currentDoc);
//		System.out.println(currentDoc.html());
	}

}
