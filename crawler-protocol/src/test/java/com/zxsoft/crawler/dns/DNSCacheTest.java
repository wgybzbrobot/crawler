package com.zxsoft.crawler.dns;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;
import org.springframework.util.Assert;

public class DNSCacheTest {

	@Test
	public void parse() throws UnknownHostException {
		InetAddress[] addrs = DNSCache.parse("tieba.baidu.com");
		DNSCache.put("http://tieba.baidu.com", addrs);
		System.out.println(addrs[0].toString().trim());
		Assert.isTrue(addrs[0].getHostAddress().matches("180.97.33.2[3 | 4]"));
	}
}
