package com.zxsoft.crawler;

import org.junit.Test;
import org.restlet.resource.ClientResource;

public class AdminResourceTest {

	private static String baseUrl = "http://localhost:8989/crawler/";
	
	@Test
	public void test() throws Exception {
	
		ClientResource client = new ClientResource(baseUrl + AdminResource.PATH + "?cmd=status");
		
		System.out.println(client.get().getText());
	}

	@Test
	public void stop() throws Exception {
		
		ClientResource client = new ClientResource(baseUrl + AdminResource.PATH + "?cmd=stop");
		
		System.out.println(client.get().getText());
	}
}
