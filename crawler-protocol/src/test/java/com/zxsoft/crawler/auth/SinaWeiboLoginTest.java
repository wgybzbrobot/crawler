package com.zxsoft.crawler.auth;

import org.junit.Test;

public class SinaWeiboLoginTest {
	
	@Test
	public void testLogin() throws Exception {
		SinaWeiboLogin login = new SinaWeiboLogin();
		login.login("hefeiqingdou@sina.cn", "hefei123");
	}
}
