package com.zxsoft.crawler.protocols.http;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.thinkingcloud.framework.util.Assert;

public class TestAuthHelper {

	@Test
	public void testIsAuth() throws MalformedURLException {
//		Assert.isTrue(AuthHelper.isAuth(new URL("http://tieba.baidu.com/p/3351807309")));
		Assert.isTrue(!AuthHelper.isAuth(new URL("http://sports.sina.com.cn/nba/2014-10-15/10417371123.shtml")));
	}
}
