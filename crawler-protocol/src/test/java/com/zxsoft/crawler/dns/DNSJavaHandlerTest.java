package com.zxsoft.crawler.dns;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;
import org.springframework.util.Assert;

import com.zxsoft.crawler.util.SProperties;

public class DNSJavaHandlerTest {

	@Test
	public void testSetup() {
		SProperties config = new SProperties();
		config.put("dnscachetime", "20");
		DNSJavaHandler handler = new DNSJavaHandler();
		handler.setup(config);
	}
	
	@Test
	public void testGetInetAddress() {
		String url = "http://www.tieba.baidu.com";
		DNSJavaHandler handler = new DNSJavaHandler();
		try {
	        InetAddress addr = handler.getInetAddress(url);
	        Assert.isTrue(addr.getHostAddress().matches("(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)"));
        } catch (UnknownHostException e) {
	        e.printStackTrace();
        }
	}
}
