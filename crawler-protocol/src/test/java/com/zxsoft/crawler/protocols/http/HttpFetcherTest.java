package com.zxsoft.crawler.protocols.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.jsoup.nodes.Document;
import org.junit.Test;

import com.zxisl.commons.utils.Assert;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.storage.WebPage;

public class HttpFetcherTest {

	static HttpFetcher httpFetcher = new HttpFetcher();
	
	@Test
	public void test() {
		String url = "http://blog.csdn.net/gb/洛县发布/1/12/n4340210.htm洛县发布雨后海边戏水卫生警告.html";
		WebPage page = new WebPage(url, false);
		ProtocolOutput output = httpFetcher.fetch(page);
		Document document = output.getDocument();
		System.out.println(document.html());
	}

	@Test
	public void test2() {
		String url = "http://bbs.ahwang.cn/forum-156-1.html";
		WebPage page = new WebPage(url, false);
		ProtocolOutput output = httpFetcher.fetch(page);
		Document document = output.getDocument();
		Assert.notNull(document);
		System.out.println(document.html());
	}
	
	@Test
	public void test1() {
	        HttpClient client = new HttpClient();
                List<String> urls = new ArrayList<String>();
//                urls.add("http://www.qq.com");
//                urls.add("http://bbs.ahwang.cn/forum-156-1.html");
//                urls.add("http://www.sina.com.cn/");
                urls.add("http://www.ahfeixi.com/forum-137-1.html");
                

                for (String url : urls) {
                        WebPage page = new WebPage(url, false);
                        ProtocolOutput output = httpFetcher.fetch(page);
                        Document document = output.getDocument();
                        Assert.notNull(document);
                        System.out.println(document.html());
                }
	}
}
