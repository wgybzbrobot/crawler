package com.zxsoft.crawler.dns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DNSCache {
	
	private static Logger LOG = LoggerFactory.getLogger(DNSCache.class);
	
	private static Map<String, InetAddress[]> cache = new WeakHashMap<String, InetAddress[]>();
	
	public static InetAddress[] get(String host) {
		return cache.get(host);
	}
	
	public static void put(String host, InetAddress[] addrs) {
		cache.put(host, addrs);
	}
	
	public static void remove(String host) {
		cache.remove(host);
	}
	
	public static InetAddress[] parse(String host) {
		InetAddress[] addrs = null;
		try {
			addrs = InetAddress.getAllByName(host);
        } catch (UnknownHostException e) {
	        LOG.error(host + " host unkown.", e);
        }
		return addrs;
	}
}
