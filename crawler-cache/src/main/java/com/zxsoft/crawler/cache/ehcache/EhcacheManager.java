package com.zxsoft.crawler.cache.ehcache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

public class EhcacheManager {

	public static CacheManager cacheManager = CacheManager.create();
	
	// 代理缓存
	public static final String PROXY_CACHE = "proxy_cache";
	
	static {
		cacheManager.addCache(PROXY_CACHE);
		
	}
	
	public static Ehcache getProxyEhcache () {
		return cacheManager.getEhcache(PROXY_CACHE);
	}
}
