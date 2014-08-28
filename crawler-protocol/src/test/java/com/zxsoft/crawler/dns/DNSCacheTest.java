package com.zxsoft.crawler.dns;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import org.junit.Test;
import org.thinkingcloud.framework.util.Assert;

public class DNSCacheTest {

	@Test
	public void parse() throws Exception {
		URL url = new URL("http://tieba.baidu.com");
		InetAddress[] addrs = new DNSCache().get(url);
		System.out.println(addrs[0].toString().trim());
		Assert.isTrue(addrs[0].getHostAddress().matches("180.97.33.2[3 | 4]"));
		
		System.out.println(new DNSCache().getAsString(url));
	}
}
