package com.zxsoft.crawler.protocols.http.httpclient;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;
import org.springframework.util.Assert;

import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.net.protocols.Response;

public class HttpClientTest {

	@Test
	public void testGetResponse() throws ProtocolException, IOException {
		HttpClient client = new HttpClient();
		URL url = new URL("http://tieba.baidu.com");
		Proxy proxy = new Proxy("HTTP", "", "", "192.168.31.244", 28080, "");
		boolean followRedirects = true;
		Response response = client.getResponse(url, proxy, followRedirects);
		Assert.notNull(response);
	}
}
