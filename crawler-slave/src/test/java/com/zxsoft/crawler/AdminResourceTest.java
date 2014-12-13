package com.zxsoft.crawler;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.resource.ClientResource;

import com.zxsoft.crawler.api.AdminResource;
import com.zxsoft.crawler.api.SlaveServer;

public class AdminResourceTest {

	private static String baseUrl = "http://localhost:8989/slave/";
	private static SlaveServer server;

	@BeforeClass
	public static void before() throws Exception {
		server = new SlaveServer(8989);
		server.start();
	}
	
	@AfterClass
	public static void after() throws Exception {
		if (!server.stop(false)) {
			for (int i = 1; i < 15; i++) {
				System.err.println("Waiting for jobs to complete - " + i + "s");
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				};
				server.stop(false);
				if (!server.isRunning()) {
					break;
				}
			}
		}
		if (server.isRunning()) {
			System.err.println("Forcibly stopping server...");
			server.stop(true);
		}
	}
	
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
