package com.zxsoft.crawler.protocols.http;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.tika.io.IOUtils;
import org.jsoup.nodes.Document;
import org.junit.Test;

import com.zxisl.commons.utils.Assert;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.util.EncodingDetector;
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
