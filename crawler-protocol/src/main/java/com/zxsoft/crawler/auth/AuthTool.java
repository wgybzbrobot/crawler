package com.zxsoft.crawler.auth;

import java.util.HashMap;
import java.util.Map;

public class AuthTool {

	
	private static Map<String, Class<? extends Login>> hostToClass = new HashMap<String, Class<? extends Login>>();
	
	static {
		hostToClass.put("http://s.weibo.com", SinaWeiboLogin.class);
	}
	
	public static Login getLogin(String host) throws InstantiationException, IllegalAccessException {
		return hostToClass.get(host).newInstance();
	}
}
