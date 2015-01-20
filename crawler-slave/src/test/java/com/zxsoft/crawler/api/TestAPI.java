package com.zxsoft.crawler.api;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.resource.ClientResource;


public class TestAPI {

	private static SlaveServer server;
	private static String baseUrl = "http://localhost:8989/slave/";

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
					Thread.sleep(20000);
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
	public void testJobAPI() throws Exception {
		ClientResource cli = new ClientResource(baseUrl + JobResource.PATH);
		JacksonRepresentation<Map<String,Object>> jr = null;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Params.CRAWL_ID, "test");
		map.put(Params.JOB_TYPE, JobType.NETWORK_INSPECT);
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(Params.URL, "http://tieba.baidu.com/f?ie=utf-8&kw=%E8%9A%8C%E5%9F%A0");
		args.put(Params.PREV_FETCH_TIME, System.currentTimeMillis() - 24 * 60 * 60 * 1000L);
		args.put(Params.COMMENT, "百度贴吧蚌埠吧");
		args.put(Params.SOURCE_ID, "aHR0cDovL3RpZWJhLmJhaWR1LmNvbS9mP2llPXV0Zi04Jmt3PSVFOCU5QSU4QyVFNSU5RiVBMA==");
		args.put(Params.COUNTRY_CODE, Params.COUNTRY_DOMESTIC);
		args.put(Params.PROVINCE_CODE, 110000);
		args.put(Params.CITY_CODE, 110100);
		args.put(Params.SERVER_ID, 6103);
		args.put(Params.SOURCE_TYPE, JobType.NETWORK_INSPECT.getValue());
		
		map.put(Params.ARGS, args);
		jr = new JacksonRepresentation<Map<String,Object>>(map);
		System.out.println(cli.put(jr).getText());
	}
}
