package com.zxsoft.crawler;

import java.io.IOException;

import org.junit.Test;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class APIInfoResourceTest {

	private static String baseUrl = "http://localhost:8989/crawler/";
	
	@Test
	public void test() throws ResourceException, IOException {
		ClientResource client = new ClientResource(baseUrl);
		System.out.println(client.get().getText());
	}
}
