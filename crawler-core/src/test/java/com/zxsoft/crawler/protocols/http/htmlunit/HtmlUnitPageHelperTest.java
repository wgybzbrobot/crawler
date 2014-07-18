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
import com.zxsoft.crawler.protocols.http.HttpFetcher;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CrawlerServer.class)
public class HtmlUnitPageHelperTest {
	
	@Autowired
	HtmlUnitPageHelper pageHelper;
	
	@Autowired
	HttpFetcher httpFetcher;
	
	@Test
	public void testLoadLastPageAjax() throws IOException {
		ProtocolOutput protocolOutput = httpFetcher.fetch("http://roll.news.sina.com.cn/s/channel.php", true);
		Assert.notNull(protocolOutput);
		Document currentDoc = protocolOutput.getDocument();
		Assert.notNull(currentDoc);
		protocolOutput = pageHelper.loadLastPage(currentDoc);
		Assert.notNull(protocolOutput);
		currentDoc = protocolOutput.getDocument();
		Assert.notNull(currentDoc);
		System.out.println(currentDoc.html());
	}

}
