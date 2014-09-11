package com.zxsoft.crawler.protocols.http;

import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.thinkingcloud.framework.util.Assert;

import com.zxsoft.crawler.auth.SinaWeiboLogin;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.util.CrawlerConfiguration;

public class HttpFetcherTest {

	static HttpFetcher httpFetcher;
	
	static {
		Configuration conf = CrawlerConfiguration.create();
		httpFetcher = new HttpFetcher(conf);
	}

	SinaWeiboLogin login;
	
	@Test
	public void testWeibo() throws Exception {
	
		login.login("hefeiqingdou@sina.cn", "hefei123");
		String url = "http://s.weibo.com/wb/%25E6%2588%25BF%25E7%25A5%2596%25E5%2590%258D&xsort=time&Refer=weibo_wb";
		ProtocolOutput output = httpFetcher.fetch(url, false);
		Assert.notNull(output);
		Document document = output.getDocument();
		Assert.notNull(document);
		System.out.println(document.html());
	}
	
	@Test
	public void test() {
		String url = "http://www.hfr.cc/thread-88379-1-1.html";
		ProtocolOutput output = httpFetcher.fetch(url, false);
		Assert.notNull(output);
		Document document = output.getDocument();
		System.out.println(document.html());
		String text = document.select("div.pct div.t_fsz table tr td.t_f").first().html();
		System.out.println(text);
	}

	@Test
	public void test2() {
//		String url = "http://www.hfr.cc/thread-88282-1-1.html";
		String url = "http://www.hfr.cc/forum.php?mod=viewthread&tid=88379&extra=page%3D1";
		ProtocolOutput output = httpFetcher.fetch(url, false);
		Assert.notNull(output);
		Document document = output.getDocument();
		System.out.println(document.html());
		String text = document.select("div.pct div.t_fsz table tr td.t_f").first().html();
		System.out.println(text);
	}
	
	
	@Test
	public void testlocal() {
		String url = "http://localhost:8080/crawler-web/?comment=#";
		ProtocolOutput output = httpFetcher.fetch(url, false);
		Assert.notNull(output);
		Document document = output.getDocument();
		System.out.println(document.html());
		String text = document.select("div#testnull").first().html();
		System.out.println(text);
	}
	
	
	
	
}
