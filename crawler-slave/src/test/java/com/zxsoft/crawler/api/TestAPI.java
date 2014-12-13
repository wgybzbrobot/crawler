package com.zxsoft.crawler.api;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.resource.ClientResource;

import com.zxsoft.crawler.api.JobManager.JobType;

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
	public void testJobAPI() throws Exception {
		ClientResource cli = new ClientResource(baseUrl + JobResource.PATH);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Params.CRAWL_ID, "test");
		map.put(Params.JOB_TYPE, JobType.NETWORK_INSPECT);
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(Params.URL, "http://bbs.tianya.cn/list-free-1.shtml");
		args.put(Params.COMMENT, "天涯杂谈");
		map.put(Params.ARGS, args);
	    JacksonRepresentation<Map<String,Object>> jr =
	    	      new JacksonRepresentation<Map<String,Object>>(map);
	    System.out.println(cli.put(jr).getText());
	    	    
		args.put(Params.URL, "http://roll.news.sina.com.cn/s/channel.php");
		args.put(Params.COMMENT, "新浪新闻滚动");
		map.put(Params.ARGS, args);
		jr = new JacksonRepresentation<Map<String,Object>>(map);
		System.out.println(cli.put(jr).getText());
		
		args.put(Params.URL, "http://boxun.eggpain.tk/rolling.shtml");
		args.put(Params.COMMENT, "博讯滚动新闻");
		map.put(Params.ARGS, args);
		jr = new JacksonRepresentation<Map<String,Object>>(map);
		System.out.println(cli.put(jr).getText());
	}
}
