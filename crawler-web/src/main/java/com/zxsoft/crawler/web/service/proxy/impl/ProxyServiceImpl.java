package com.zxsoft.crawler.web.service.proxy.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Proxy;
import com.zxsoft.crawler.entity.ProxyId;
import com.zxsoft.crawler.web.dao.proxy.ProxyDao;
import com.zxsoft.crawler.web.service.proxy.ProxyService;

@Service
public class ProxyServiceImpl implements ProxyService {

	@Autowired
	private ProxyDao proxyDao;
	
	@Override
    public Proxy getProxy(ProxyId id) {
	    return proxyDao.getProxy(id);
    }

	@Override
    public Page<Proxy> getProxies(int pageNo, int PageSize, Proxy proxy) {
	   	return proxyDao.getProxies(pageNo, PageSize, proxy);
    }

	@Override
    public void addOrUpdateProxy(Proxy proxy) {
	    proxyDao.addOrUpdateProxy(proxy);
    }

	@Override
    public void deleteProxy(ProxyId id) {
		Proxy proxy = proxyDao.getProxy(id);
	    proxyDao.deleteProxy(proxy);
	    
    }

}
