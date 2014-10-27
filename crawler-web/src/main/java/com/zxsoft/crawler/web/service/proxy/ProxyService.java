package com.zxsoft.crawler.web.service.proxy;

import org.springframework.stereotype.Service;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Proxy;
import com.zxsoft.crawler.entity.ProxyId;

@Service
public interface ProxyService {

	Proxy getProxy(ProxyId id);
	Page<Proxy> getProxies(int pageNo, int PageSize, Proxy proxy);
	void addOrUpdateProxy(Proxy proxy);
	void deleteProxy(ProxyId id);
	
}
