//package com.zxsoft.crawler.protocols.http;
//
//import org.jsoup.nodes.Document;
//import org.junit.Test;
//
//import com.zxsoft.crawler.protocol.ProtocolOutput;
//import com.zxsoft.crawler.storage.WebPage;
//
//public class HttpFetcherTest {
//
//	static HttpFetcher httpFetcher = new HttpFetcher();
//	
//	@Test
//	public void test() {
//		String url = "http://www.canyu.org/n99305c6.aspx";
//		WebPage page = new WebPage(url, false);
//		ProtocolOutput output = httpFetcher.fetch(page);
//		Document document = output.getDocument();
//		System.out.println(document.html());
//	}
//
//}
