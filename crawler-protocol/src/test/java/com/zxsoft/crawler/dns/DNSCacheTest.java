package com.zxsoft.crawler.dns;

import java.net.URL;

import org.junit.Test;

public class DNSCacheTest {

	@Test
	public void parse() throws Exception {
		URL url = new URL("http://tieba.baidu.com");
		String ip = DNSCache.getIp(url);
		System.out.println(ip);
	}
}
