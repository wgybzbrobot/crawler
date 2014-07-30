package com.zxsoft.crawler.protocols.http.proxy;

import java.net.MalformedURLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.util.Utils;

@Component
public class ProxyRandom {

	@Resource
	private ProxyFactory localProxyFactory;
	
	public Proxy random(String url) {
		
		try {
	        String host = Utils.getHost(url);
        } catch (MalformedURLException e) {
	        e.printStackTrace();
        }
		
		String type = "";
		
		List<Proxy> proxies = localProxyFactory.getProxies(type);
		
		if (CollectionUtils.isEmpty(proxies))
			return null;
		
		int i = (int) (Math.random() * proxies.size());
		return proxies.get(i);
	}
}
