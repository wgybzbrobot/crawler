package com.zxsoft.crawler.parse;

import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.thinkingcloud.framework.util.Assert;

import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.CrawlerConfiguration;

public class TestNetworkInspectParserController {

	private HttpFetcher httpFetcher;
	
	private Configuration conf;
	
	@Before
	public void setup() {
		conf = CrawlerConfiguration.create();
		httpFetcher = new HttpFetcher(conf);
	}
	
	@Test
	public void testParseTieBa() throws ParserNotFoundException {
		String urlStr = "http://tieba.baidu.com/f?kw=%B0%F6%B2%BA";
		ProtocolOutput protocolOutput = httpFetcher.fetch(urlStr);
		Assert.notNull(protocolOutput);
		Document document = protocolOutput.getDocument();
		Assert.notNull(document);
		WebPage page = new WebPage("title", urlStr, System.currentTimeMillis(), document);
		page.setAjax(false);
		
		NetworkInspectParserController parserController = new NetworkInspectParserController(conf);
		parserController.parse(page);
	}

	@Test
	public void testParseNuomi() throws ParserNotFoundException {
		String urlStr = "http://www.nuomi.com/?cid=002540";
		ProtocolOutput protocolOutput = httpFetcher.fetch(urlStr);
		Assert.notNull(protocolOutput);
		Document document = protocolOutput.getDocument();
		Assert.notNull(document);
		WebPage page = new WebPage("title", urlStr, System.currentTimeMillis(), document);
		page.setAjax(false);
		
		NetworkInspectParserController parserController = new NetworkInspectParserController(conf);
		parserController.parse(page);
	}
	
	@Test
	public void testParseTianYa() throws ParserNotFoundException {
		String urlStr = "http://bbs.tianya.cn/list-free-1.shtml";
		ProtocolOutput protocolOutput = httpFetcher.fetch(urlStr);
		Assert.notNull(protocolOutput);
		Document document = protocolOutput.getDocument();
		Assert.notNull(document);
		WebPage page = new WebPage("title", urlStr, System.currentTimeMillis(), document);
		page.setAjax(false);
		
		NetworkInspectParserController parserController = new NetworkInspectParserController(conf);
		parserController.parse(page);
	}
	
	@Test
	public void testParseZhongAn() throws ParserNotFoundException {
		String urlStr = "http://bbs.anhuinews.com/forum-319-1.html";
		ProtocolOutput protocolOutput = httpFetcher.fetch(urlStr);
		Assert.notNull(protocolOutput);
		Document document = protocolOutput.getDocument();
		Assert.notNull(document);
		WebPage page = new WebPage("title", urlStr, System.currentTimeMillis(), document);
		page.setAjax(false);
		
		NetworkInspectParserController parserController = new NetworkInspectParserController(conf);
		parserController.parse(page);
	}
	
	
	@Test
	public void testParseSinaNews() throws ParserNotFoundException, ProtocolException {
		String urlStr = "http://roll.news.sina.com.cn/s/channel.php";
		ProtocolOutput protocolOutput = httpFetcher.fetch(urlStr, true);
		Assert.notNull(protocolOutput);
		Document document = protocolOutput.getDocument();
		Assert.notNull(document);
//		System.out.println(document.html());
		WebPage page = new WebPage("title", urlStr, System.currentTimeMillis(), document);
		page.setAjax(true);
		
		NetworkInspectParserController parserController = new NetworkInspectParserController(conf);
		FetchStatus status = parserController.parse(page);
		Assert.isTrue(status.getStatus() == FetchStatus.Status.SUCCESS);

	}
	
}
