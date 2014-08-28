package com.zxsoft.proxy;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.Assert;


public class LocalProxyFacotryTest {

	private static Logger LOG = LoggerFactory.getLogger(LocalProxyFacotryTest.class);
	
	@Test
	public void test() {
		ProxyFactory localProxyFactory = new LocalProxyFactory();
		
		List<Proxy> list = localProxyFactory.getProxies("");
		
		LOG.error("test");
		Assert.notEmpty(list);
	}
}
