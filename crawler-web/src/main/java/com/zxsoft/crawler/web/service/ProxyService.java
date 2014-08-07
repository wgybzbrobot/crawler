package com.zxsoft.crawler.web.service;

import java.util.List;

import com.zxsoft.proxy.Proxy;

public interface ProxyService {

	List<Proxy> find(final int pageNo, final int pageSize, Proxy param);
	
	void add(Proxy proxy);
}
