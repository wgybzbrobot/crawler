package com.zxsoft.crawler.cache.ehcache;

import java.io.IOException;
import java.util.List;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import com.zxsoft.crawler.cache.entry.Proxy;

public class EhcacheProxy {

	private static Ehcache cache;
	private static final String PROXY_KEY = "proxykey";

	@SuppressWarnings("unchecked")
    public static List<Proxy> get() {
		if (cache.get(PROXY_KEY) != null)
			return (List<Proxy>) cache.get(PROXY_KEY);
		
		List<Proxy> proxies = null;
		try {
			ProxyReader proxyReader = new ProxyReader();
	        proxies = proxyReader.getProxies();
	        cache.put(new Element(PROXY_KEY, proxies));
        } catch (IOException e) {
	        e.printStackTrace();
        }
		return proxies;
	}

}
