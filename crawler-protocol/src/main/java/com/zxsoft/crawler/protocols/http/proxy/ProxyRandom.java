package com.zxsoft.crawler.protocols.http.proxy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.zxsoft.crawler.cache.proxy.Proxy;

@Component
public class ProxyRandom {

	@Autowired
	private ProxyFactory factory;
	
	public Proxy random() {
		
		List<Proxy> proxies = factory.getProxies();
		
		if (CollectionUtils.isEmpty(proxies))
			return null;
		
		int i = (int) (Math.random() * proxies.size());
		return proxies.get(i);
	}
}
