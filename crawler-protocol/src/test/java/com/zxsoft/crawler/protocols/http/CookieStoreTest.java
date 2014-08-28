package com.zxsoft.crawler.protocols.http;

import org.junit.Test;

public class CookieStoreTest {

	@Test
	public void test() {
		CookieStore.put("http://sina.com", "123456");
		CookieStore.put("http://sina.com", "123457");
		CookieStore.put("http://sina.com", "123458");
		CookieStore.put("http://sina.com", "123459");
		System.out.println(CookieStore.get("http://sina.com"));
	}
}
