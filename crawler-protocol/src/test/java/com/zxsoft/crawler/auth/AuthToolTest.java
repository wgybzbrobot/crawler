package com.zxsoft.crawler.auth;

import org.junit.Test;

public class AuthToolTest {

	@Test
	public void testGetLogin() throws Exception {
		Login login = AuthTool.getLogin("http://s.weibo.com");
		String cookie = login.login("hefeiqingdou@sina.cn", "hefei123");
		System.out.println(cookie);
	}
}
