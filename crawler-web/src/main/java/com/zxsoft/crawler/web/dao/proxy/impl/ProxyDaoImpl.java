package com.zxsoft.crawler.web.dao.proxy.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.thinkingcloud.framework.web.utils.HibernateCallbackUtil;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Proxy;
import com.zxsoft.crawler.entity.ProxyId;
import com.zxsoft.crawler.web.dao.proxy.ProxyDao;

@Repository
public class ProxyDaoImpl implements ProxyDao {

	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	@Override
	public Proxy getProxy(ProxyId id) {
		return hibernateTemplate.get(Proxy.class, id);
	}

	@Override
	public Page<Proxy> getProxies(int pageNo, int pageSize, Proxy proxy) {
		
		Map<String, String> params = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer(" from Proxy a where 1=1 ");
		if (proxy != null) {
			if (proxy.getId() != null) {
				String ip = proxy.getId().getIp();
				params.put("id.ip", "%" + ip.trim() + "%");
			}
			if (proxy.getSiteType() != null) {
				String comment  = proxy.getSiteType().getComment();
				params.put("siteType.comment", comment);
			}
		}
		
		
		HibernateCallback<Page<Proxy>> action = HibernateCallbackUtil.getCallbackWithPage(sb, params, null, pageNo, pageSize);
		
		Page<Proxy> page = hibernateTemplate.execute(action);
		return page;
	}

	@Override
	public void addOrUpdateProxy(Proxy proxy) {
		hibernateTemplate.saveOrUpdate(proxy);
	}

	@Override
	public void deleteProxy(Proxy proxy) {
		hibernateTemplate.delete(proxy);
	}

}
