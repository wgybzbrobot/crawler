package com.zxsoft.crawler.protocols.http.htmlunit;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.Assert;

import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.util.CrawlerConfiguration;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

public class HtmlUnitTest {
	
	private static Logger LOG = LoggerFactory.getLogger(HtmlUnitTest.class);
	
	@BeforeClass
	public void setup() {
		Configuration conf = CrawlerConfiguration.create();
		 htmlUnit = new HtmlUnit(conf);
		 httpFetcher = new HttpFetcher(conf);
	}
	
	HttpBase htmlUnit;
	
	HttpFetcher httpFetcher;
	
	@Test
	public void testLoadCurrentPage() throws ProtocolException, IOException {
//		String url = "http://roll.news.sina.com.cn/s/channel.php";
		String url = "http://roll.sohu.com/";
		ProtocolOutput protocolOutput = htmlUnit.getProtocolOutput(url);
		Assert.notNull(protocolOutput);
		Document currentDoc = protocolOutput.getDocument();
		Assert.notNull(currentDoc);
		LOG.info(currentDoc.html());
	}
	
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
	public void testLoadPrevPage() throws IOException, PrevPageNotFoundException, PageBarNotFoundException {
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
