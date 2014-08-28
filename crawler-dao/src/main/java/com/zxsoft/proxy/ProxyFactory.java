package com.zxsoft.proxy;

import java.util.List;

public interface ProxyFactory {
	
	List<Proxy> getProxies(String type);
	
}
