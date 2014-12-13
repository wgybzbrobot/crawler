package com.zxsoft.crawler.protocols.http;

import org.jsoup.nodes.Document;
import org.junit.Test;
import org.thinkingcloud.framework.util.Assert;

import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.storage.WebPage;

public class HttpFetcherTest {

	static HttpFetcher httpFetcher = new HttpFetcher();
	
	@Test
	public void test() {
		String url = "http://www.hfr.cc/thread-88379-1-1.html";
		WebPage page = new WebPage(url, false);
		ProtocolOutput output = httpFetcher.fetch(page);
		Document document = output.getDocument();
		String text = document.select("div.pct div.t_fsz table tr td.t_f").first().html();
		Assert.hasText(text);
	}

	@Test
	public void test2() {
//		String url = "http://www.hfr.cc/thread-88282-1-1.html";
		String url = "http://www.hfr.cc/forum.php?mod=viewthread&tid=88379&extra=page%3D1";
		WebPage page = new WebPage(url, false);
		ProtocolOutput output = httpFetcher.fetch(page);
		Document document = output.getDocument();
		Assert.notNull(document);
		String text = document.select("div.pct div.t_fsz table tr td.t_f").first().html();
		Assert.hasText(text);
	}
	
	
}
