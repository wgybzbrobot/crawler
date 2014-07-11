package com.zxsoft.crawler.protocols.http.proxy;

import java.util.List;


import com.zxsoft.crawler.cache.proxy.Proxy;

public interface ProxyFactory {

	public List<Proxy> getProxies();
}
