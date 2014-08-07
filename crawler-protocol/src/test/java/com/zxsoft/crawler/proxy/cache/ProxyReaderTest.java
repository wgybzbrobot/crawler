package com.zxsoft.crawler.proxy.cache;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.util.Assert;

import com.zxsoft.proxy.LocalProxyFactory;
import com.zxsoft.proxy.Proxy;
import com.zxsoft.proxy.ProxyFactory;

public class ProxyReaderTest extends TestCase  {

	
	@Test
	public void testReadProxies() throws IOException {
		ProxyFactory factory = new LocalProxyFactory();
		List<Proxy> list =  factory.getProxies("");
		Assert.notNull(list);
	}
	
	
	
}
