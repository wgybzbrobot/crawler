package com.zxsoft.crawler.auth;

import com.zxsoft.crawler.parse.ParseTool;

/**
 * 登录认证
 */
public class Auth extends ParseTool {

	public void auth(String url) {
		// 获取未登录的帐号，若空则返回
		confDao.getAccounts(url);
		
	}
}
