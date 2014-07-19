package com.zxsoft.crawler.protocol.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;

import org.junit.Test;
import org.springframework.util.Assert;

public class SocksProxyTest {

	@Test
	public void testSocksProxy()  {
		SocketAddress addr = new InetSocketAddress("219.147.172.2", 12345);
		Proxy proxy = new Proxy(Proxy.Type.SOCKS, addr);
		
//		SocketAddress addr = new InetSocketAddress("59.108.116.179", 3128);
//		Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
		
		URL url;
		Object content = null;
        try {
	        url = new URL("http://tieba.baidu.com");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
			conn.setConnectTimeout(5000);
			conn.connect();
			content = conn.getContent();
        } catch (MalformedURLException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
		
        Assert.notNull(content);
	}
}
