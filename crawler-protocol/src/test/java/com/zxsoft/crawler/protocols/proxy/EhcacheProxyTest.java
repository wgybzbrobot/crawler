package com.zxsoft.crawler.protocols.proxy;

import java.util.List;

import org.junit.Test;

import com.zxsoft.crawler.proxy.cache.Proxy;
import com.zxsoft.crawler.proxy.cache.ehcache.EhcacheProxyCacheStorage;

public class EhcacheProxyTest {

	public void testGet() {
		List<Proxy> proxies = EhcacheProxyCacheStorage.get();
	}
}
