package com.zxsoft.crawler.parse;

import java.util.Calendar;
import java.util.Date;

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
	
	/**
	 * 测试蚌埠吧
	 */
	@Test
	public void testParseTieBa() throws ParserNotFoundException {
		String urlStr = "http://tieba.baidu.com/f?ie=utf-8&kw=%E8%9A%8C%E5%9F%A0";
		WebPage page = new WebPage(urlStr, false);
		page = new WebPage("test-title", urlStr, System.currentTimeMillis(), null);
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, 9, 14);
		page.setPrevFetchTime(calendar.getTimeInMillis());
		
		NetworkInspectParserController parserController = new NetworkInspectParserController();
		parserController.parse(page);
	}

	/**
	 * 测试糯米网
	 */
	@Test
	public void testParseNuomi() throws ParserNotFoundException {
		String urlStr = "http://www.nuomi.com/?cid=002540";
		WebPage page  = new WebPage("title", urlStr, System.currentTimeMillis(), null);
		page.setAjax(false);
		
		NetworkInspectParserController parserController = new NetworkInspectParserController();
		parserController.parse(page);
	}
	
	@Test
	public void testParseTianYa() throws ParserNotFoundException {
		String urlStr = "http://bbs.tianya.cn/list-free-1.shtml";
		WebPage page = new WebPage();
		page.setTitle("天涯杂谈");
		page.setBaseUrl(urlStr);
		page.setAjax(false);
		NetworkInspectParserController parserController = new NetworkInspectParserController();
		parserController.parse(page);
		
		WebPage _page = new WebPage();
		page.setTitle("小米论坛");
		page.setBaseUrl("http://bbs.xiaomi.cn");
		page.setAjax(false);
		parserController.parse(_page);
	}
	
	@Test
	public void testParseZhongAn() throws ParserNotFoundException {
		String urlStr = "http://bbs.anhuinews.com/forum.php?mod=forumdisplay&fid=319&filter=lastpost&orderby=lastpost";
		long now = System.currentTimeMillis();
		long interval = 20 * 24 * 60 * 60 * 1000L;
		System.out.println(interval);
		long prev = now - interval;
		System.out.println("prev:" + new Date(prev));
		WebPage page = new WebPage("title", urlStr, System.currentTimeMillis(), null);
		page.setAjax(false);
		page.setPrevFetchTime(prev);
		NetworkInspectParserController parserController = new NetworkInspectParserController();
		parserController.parse(page);
		
		
	}
	
	@Test
	public void testParseMop() throws ParserNotFoundException {
		String urlStr = "http://dzh.mop.com/yuanchuang";
		WebPage page = new WebPage(urlStr, false);
		page.setPrevFetchTime(System.currentTimeMillis() - 4000 * 60 * 1000);
		ProtocolOutput protocolOutput = httpFetcher.fetch(page);
		Assert.notNull(protocolOutput);
		
		Document document = protocolOutput.getDocument();
		Assert.notNull(document);
		page = new WebPage("title", urlStr, System.currentTimeMillis(), document);
		page.setAjax(false);
		page.setPrevFetchTime(System.currentTimeMillis() - 4000 * 60 * 1000);
		
		NetworkInspectParserController parserController = new NetworkInspectParserController();
		parserController.parse(page);
	}
	
	
	/**
	 * 新浪新闻
	 */
	@Test
	public void testParseSinaNews() throws ParserNotFoundException, ProtocolException {
		String urlStr = "http://roll.news.sina.com.cn/s/channel.php";
		WebPage page = new WebPage(urlStr, true);
		page = new WebPage("title", urlStr, System.currentTimeMillis(), null);
		page.setAjax(true);
		
		NetworkInspectParserController parserController = new NetworkInspectParserController();
		FetchStatus status = parserController.parse(page);
		Assert.isTrue(status.getStatus() == FetchStatus.Status.SUCCESS);

	}
	
}
