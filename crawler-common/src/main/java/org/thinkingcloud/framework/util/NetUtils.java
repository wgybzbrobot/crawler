package org.thinkingcloud.framework.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NetUtils {

	/** get url query parameters */
	public static Map<String, String> getParameters(URL u) {
		String url = u.toString();
		if (url.lastIndexOf("?") != -1) {
			url = url.split("\\?")[1];
		}

		Map<String, String> map = new HashMap<String, String>();
		
		String[] strs = url.split("&");
		for (String str : strs) {
	        if (StringUtils.isEmpty(str)) continue;
	        map.put(str.split("=")[0], str.split("=")[1]);
        }
		
		return map;
	}
	
	public static String createQueryString(Map<String, String> map) {
		if (map == null)
			return "";
		
		Set<String> set = map.keySet();
		StringBuilder sb = new StringBuilder();
		for (String str : set) {
	       sb.append("&" + str + "=" + map.get(str));
        }
		return sb.toString().substring(1);
	}
	
	/**
	 * 获取域名
	 */
	public static String getHost(URL url) {
		return url.getProtocol() + "://" + url.getHost();
	}
}
