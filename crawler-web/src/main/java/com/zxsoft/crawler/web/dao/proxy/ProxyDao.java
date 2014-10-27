package com.zxsoft.crawler.web.dao.proxy;

import org.springframework.stereotype.Repository;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Proxy;
import com.zxsoft.crawler.entity.ProxyId;

@Repository
public interface ProxyDao {

	Proxy getProxy(ProxyId id);
	Page<Proxy> getProxies(int pageNo, int PageSize, Proxy proxy);
	void addOrUpdateProxy(Proxy proxy);
	void deleteProxy(Proxy proxy);
}
