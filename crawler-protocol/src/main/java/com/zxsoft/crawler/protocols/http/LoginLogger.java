package com.zxsoft.crawler.protocols.http;

import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.thinkingcloud.framework.util.CollectionUtils;

/**
 * 网站帐号登录记录器
 */
public class LoginLogger {

	private static WeakHashMap<String, Set<String>> logger = new WeakHashMap<String, Set<String>>();
	
	public synchronized static String get(String host) {
		Set<String> set = logger.get(host);
		String[] arr = {};
		if (CollectionUtils.isEmpty(set)) {
			return "";
		}
		arr = set.toArray(arr);
		int rand = (int)(Math.random() * arr.length);
		return arr[rand];
	}
	
	public synchronized static void put(String host, String cookie) {
		if (CollectionUtils.isEmpty(logger.get(host))) {
			Set<String> set = new HashSet<String>();
			set.add(cookie);
			logger.put(host, set);
		} else {
			logger.get(host).add(cookie);
		}
	}
}
