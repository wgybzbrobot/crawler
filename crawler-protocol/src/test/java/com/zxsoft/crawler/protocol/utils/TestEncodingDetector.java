//package com.zxsoft.crawler.protocol.utils;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpException;
//import org.apache.commons.httpclient.HttpMethod;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.junit.Test;
//
//import com.zxsoft.crawler.util.EncodingDetector;
//
//public class TestEncodingDetector {
//
//	@Test
//	public void testGetEncode() throws HttpException, IOException {
//		HttpClient client = new HttpClient();
//		List<String> urls = new ArrayList<String>();
//		urls.add("http://news.mydrivers.com");
//		urls.add("http://baidu.com");
//		urls.add("http://www.sina.com");
//		urls.add("http://www.tencent.com");
//		
//		for (String url : urls) {
//			
//			HttpMethod get = new GetMethod(url);
//			client.executeMethod(get);
//			byte[] btyes = get.getResponseBody();
//			get.releaseConnection();
//			String encode = EncodingDetector.getEncode(btyes);
//			System.out.println(url + ":" + encode);
//        }
//		
//	}
//}
