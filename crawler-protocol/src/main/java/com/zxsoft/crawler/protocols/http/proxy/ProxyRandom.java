package com.zxsoft.crawler.protocols.http.proxy;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.zxsoft.crawler.cache.proxy.Proxy;

public class ProxyRandom {

	
	public static Proxy random() {
		List<Proxy> proxies = new ProxyFactory().getProxies();
		if (CollectionUtils.isEmpty(proxies))
			return null;
		int i = (int) (Math.random() * proxies.size());
		return proxies.get(i);
	}
}
