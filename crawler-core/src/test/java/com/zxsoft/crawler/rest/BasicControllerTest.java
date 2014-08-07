package com.zxsoft.crawler.rest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.zxsoft.crawler.urlbase.UrlbaseFactory;

public class BasicControllerTest {

	private static Logger LOG  = LoggerFactory.getLogger(BasicControllerTest.class);
	
	RestTemplate template = new TestRestTemplate();
	
	@Test
	public void testCrawlerStatus() {
		ResponseEntity<String> response = template.getForEntity("http://localhost:9999/status", String.class);
		String body = response.getBody();
		LOG.debug(body);
	}
	
	@Test
	public void testStopCrawler() {
		ResponseEntity<String> response = template.getForEntity("http://localhost:9999/stop", String.class);
		String body = response.getBody();
		LOG.debug(body);
		Assert.isTrue("false".equals(body));
	}
	
	@Test
	public void testStartCrawler() {
		ResponseEntity<String> response = template.getForEntity("http://localhost:9999/start", String.class);
		String body = response.getBody();
		LOG.debug(body);
		Assert.isTrue("true".equals(body));
	}
	@Test
	public void testRestartCrawler() {
		ResponseEntity<String> response = template.getForEntity("http://localhost:9999/restart", String.class);
		String body = response.getBody();
		LOG.debug(body);
		Assert.isTrue("true".equals(body));
	}
	
}
