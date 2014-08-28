package com.zxsoft.crawler.protocols.http;

import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.thinkingcloud.framework.util.CollectionUtils;

/**
 * Store cookies.
 * <p>key:value host:cookie
 */
public class CookieStore {

	private static WeakHashMap<String, Set<String>> cookies = new WeakHashMap<String, Set<String>>();
	
	public synchronized static String get(String host) {
		Set<String> set = cookies.get(host);
		String[] arr = {};
		if (CollectionUtils.isEmpty(set)) {
			return "";
		}
		arr = set.toArray(arr);
		int rand = (int)(Math.random() * arr.length);
		return arr[rand];
	}
	
	public synchronized static void put(String host, String cookie) {
		if (CollectionUtils.isEmpty(cookies.get(host))) {
			Set<String> set = new HashSet<String>();
			set.add(cookie);
			cookies.put(host, set);
		} else {
			cookies.get(host).add(cookie);
		}
	}
}
