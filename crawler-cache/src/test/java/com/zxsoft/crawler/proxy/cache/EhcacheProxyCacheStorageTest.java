package com.zxsoft.crawler.proxy.cache;

import java.io.IOException;

import org.junit.Test;
import org.springframework.util.Assert;

import com.zxsoft.crawler.cache.ehcache.EhcacheManager;
import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.cache.proxy.ehcache.EhcacheProxyCacheStorage;

import net.sf.ehcache.Ehcache;
import junit.framework.TestCase;

public class EhcacheProxyCacheStorageTest extends TestCase  {

	private EhcacheProxyCacheStorage storage;
	
	@Override
	public void setUp() {
		Ehcache cache = EhcacheManager.getProxyEhcache();
		storage = new EhcacheProxyCacheStorage(cache);
	}
	
	@Test
	public void testPutEntry() throws IOException {
		Proxy proxy = new Proxy("", "", "127.0.0.1", 80);
		storage.putEntry(proxy.getHost() + ":" + proxy.getPort(), proxy);
	}
	
	@Test
	public void testGetEntry() throws IOException {
		Proxy proxy = storage.getEntry("127.0.0.1:80");
		Assert.isTrue("127.0.0.1".equals(proxy.getHost()));
	}

	@Test
	public void testRemoveEntry() throws IOException {
		storage.removeEntry("127.0.0.1:80");
		Proxy proxy = storage.getEntry("127.0.0.1:80");
		Assert.isNull(proxy, "proxy is removed");
	}
	
	
	
}
