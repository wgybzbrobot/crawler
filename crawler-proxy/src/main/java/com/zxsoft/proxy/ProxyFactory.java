package com.zxsoft.proxy;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public abstract class ProxyFactory {
	
	public abstract List<Proxy> getProxies(String type);
	
	@CacheEvict(value={"proxyCache"}, allEntries=true)
	public void removeCache() {
		
	}
}
