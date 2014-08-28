package com.zxsoft.crawler.dns;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.cache.ObjectCache;

public class DNSCache {
	
	private static Logger LOG = LoggerFactory.getLogger(DNSCache.class);
	
	public InetAddress[] get(URL url) {
		String host = url.getHost();
		ObjectCache objectCache = ObjectCache.get("DNS");
		if (objectCache.getObject(host) != null) {
			return (InetAddress[]) objectCache.getObject(host);
		} else {
			InetAddress[] addrs = null;
            try {
	            addrs = InetAddress.getAllByName(host);
	            objectCache.setObject(host, addrs);
            } catch (UnknownHostException e) {
            	LOG.warn(e.getMessage());
            }
			return addrs;
		}
	}

	public String getAsString(URL url) {
		InetAddress[] addrs = get(url);
		StringBuilder sb = new StringBuilder();
		for (InetAddress addr : addrs) {
	        sb.append(addr.getHostAddress() + ";");
        }
		return sb.toString();
	}
}
