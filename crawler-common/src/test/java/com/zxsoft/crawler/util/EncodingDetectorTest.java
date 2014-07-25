package com.zxsoft.crawler.util;

import org.junit.Test;
import org.springframework.util.Assert;

public class EncodingDetectorTest {

	@Test
	public void test() {
		String encode = EncodingDetector.parseCharacterEncoding("text/html; charset=GBK");
		Assert.isTrue("GBK".equals(encode));
	}
}
