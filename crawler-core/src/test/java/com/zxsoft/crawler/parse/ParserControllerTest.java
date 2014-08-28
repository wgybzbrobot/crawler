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

public class ParserControllerTest {

	private HttpFetcher httpFetcher;
	
	private Configuration conf;
	
	@Before
	public void setup() {
		conf = CrawlerConfiguration.create();
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
		
		NetworkInspectionParserController parserController = new NetworkInspectionParserController(conf);
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
		
		NetworkInspectionParserController parserController = new NetworkInspectionParserController(conf);
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
		
		NetworkInspectionParserController parserController = new NetworkInspectionParserController(conf);
		parserController.parse(page);
	}
	
	
	@Test
	public void testParseSinaNews() throws ParserNotFoundException, ProtocolException {
		String urlStr = "http://roll.news.sina.com.cn/s/channel.php?ch=01#col=89&spec=&type=&ch=01&k=&offset_page=0&offset_num=0&num=60&asc=&page=1";
		ProtocolOutput protocolOutput = httpFetcher.fetch(urlStr, true);
		Assert.notNull(protocolOutput);
		Document document = protocolOutput.getDocument();
		Assert.notNull(document);
		System.out.println(document.html());
		WebPage page = new WebPage("title", urlStr, System.currentTimeMillis(), document);
		page.setAjax(true);
		
		NetworkInspectionParserController parserController = new NetworkInspectionParserController(conf);
		FetchStatus status = parserController.parse(page);
		Assert.isTrue(status.getStatus() == FetchStatus.Status.SUCCESS);

	}
	
	
}
