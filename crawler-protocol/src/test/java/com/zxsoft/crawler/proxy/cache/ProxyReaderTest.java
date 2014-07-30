package com.zxsoft.crawler.proxy.cache;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.util.Assert;

import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.protocols.http.proxy.LocalProxyFactory;
import com.zxsoft.crawler.protocols.http.proxy.ProxyFactory;

public class ProxyReaderTest extends TestCase  {

	
	@Test
	public void testReadProxies() throws IOException {
		ProxyFactory factory = new LocalProxyFactory();
		List<Proxy> list =  factory.getProxies("");
		Assert.notNull(list);
	}
	
	
	
}
