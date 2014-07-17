package com.zxsoft.crawler.http;

import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.util.Assert;

import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.protocols.http.htmlunit.HtmlUnit;
import com.zxsoft.crawler.storage.WebPage;

public class HtmlUnitTest {

	/**
	 * this is a speclied example, wait to solve.
	 */
	@Test
	public void testSohu() {
		HttpBase httpBase = new HtmlUnit();
		Proxy proxy = new Proxy("HTTP", "", "", "192.168.1.102", 28128, "");
		WebPage page = new WebPage();
		Document document = httpBase.getProtocolOutput("http://news.sohu.com/scroll/").getDocument();
		Assert.notNull(document);
		
	}
	
	@Test
	public void testQQ() {
		HttpBase httpBase = new HtmlUnit();
		Proxy proxy = new Proxy("HTTP", "", "", "192.168.1.102", 28128, "");
		WebPage page = new WebPage();
		Document document = httpBase.getProtocolOutput("http://roll.news.qq.com/").getDocument();
		Assert.notNull(document);
	}
	
	@Test
	public void testSina() {
		HttpBase httpBase = new HtmlUnit();
		Proxy proxy = new Proxy("HTTP", "", "", "192.168.1.102", 28128, "");
		WebPage page = new WebPage();
		Document document = httpBase.getProtocolOutput("http://roll.news.sina.com.cn/s/channel.php").getDocument();
		Assert.notNull(document);
	}
}
