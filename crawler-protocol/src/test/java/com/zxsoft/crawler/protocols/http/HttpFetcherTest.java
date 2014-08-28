package com.zxsoft.crawler.protocols.http;

import org.jsoup.nodes.Document;
import org.junit.Test;
import org.thinkingcloud.framework.util.Assert;

import com.zxsoft.crawler.auth.SinaWeiboLogin;
import com.zxsoft.crawler.protocol.ProtocolOutput;

public class HttpFetcherTest {

	HttpFetcher httpFetcher;

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
	
	

	
}
