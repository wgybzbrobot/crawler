package com.zxsoft.crawler.parse;

import java.util.Calendar;
import java.util.Date;
import org.junit.Test;
import org.thinkingcloud.framework.util.Assert;
import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.storage.WebPage;

public class TestNetworkInspectParserController {

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
	
	@Test
	public void testParseZhongAn() throws ParserNotFoundException {
		String urlStr = "http://bbs.aqnews.com.cn/forumdisplay.php?fid=46";
		long now = System.currentTimeMillis();
		long interval = 20 * 24 * 60 * 60 * 1000L;
		System.out.println(interval);
		long prev = now - interval;
		System.out.println("prev:" + new Date(prev));
		WebPage page = new WebPage("title", urlStr, System.currentTimeMillis(), null);
		page.setAjax(false);
		page.setPrevFetchTime(prev);
		NetworkInspectParserController parserController = new NetworkInspectParserController();
		FetchStatus fetchStatus = parserController.parse(page);
		System.out.println(fetchStatus.toString());
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
