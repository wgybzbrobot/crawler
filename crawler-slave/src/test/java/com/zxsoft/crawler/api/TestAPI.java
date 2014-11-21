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
	ClientResource cli;

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
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Params.CRAWL_ID, "test");
		map.put(Params.JOB_TYPE, JobType.NETWORK_INSPECT);
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(Params.URL, "http://bbs.tianya.cn/list-free-1.shtml");
		args.put(Params.PROXY_TYPE, "001");
		args.put(Params.COMMENT, "天涯杂谈");
		map.put(Params.ARGS, args);
	    JacksonRepresentation<Map<String,Object>> jr =
	    	      new JacksonRepresentation<Map<String,Object>>(map);
	    System.out.println(cli.put(jr).getText());
	    	    
	    args.put(Params.URL, "http://bbs.hefei.cc/forum-143-1.html");
		args.put(Params.PROXY_TYPE, "001");
		args.put(Params.COMMENT, "法制在线");
		map.put(Params.ARGS, args);
		jr = new JacksonRepresentation<Map<String,Object>>(map);
		System.out.println(cli.put(jr).getText());
		
		
		args.put(Params.URL, "http://bbs.xiaomi.cn");
		args.put(Params.PROXY_TYPE, "001");
		args.put(Params.COMMENT, "小米论坛");
		map.put(Params.ARGS, args);
		jr = new JacksonRepresentation<Map<String,Object>>(map);
		System.out.println(cli.put(jr).getText());
		
		
		args.put(Params.URL, "http://bbs.anhuinews.com/forum-510-1.html");
		args.put(Params.PROXY_TYPE, "001");
		args.put(Params.COMMENT, "网上问政");
		map.put(Params.ARGS, args);
		jr = new JacksonRepresentation<Map<String,Object>>(map);
		System.out.println(cli.put(jr).getText());
		
		args.put(Params.URL, "http://roll.news.sina.com.cn/s/channel.php");
		args.put(Params.PROXY_TYPE, "001");
		args.put(Params.COMMENT, "新浪新闻滚动");
		map.put(Params.ARGS, args);
		jr = new JacksonRepresentation<Map<String,Object>>(map);
		System.out.println(cli.put(jr).getText());
		
		args.put(Params.URL, "http://boxun.eggpain.tk/rolling.shtml");
		args.put(Params.PROXY_TYPE, "001");
		args.put(Params.COMMENT, "博讯滚动新闻");
		map.put(Params.ARGS, args);
		jr = new JacksonRepresentation<Map<String,Object>>(map);
		System.out.println(cli.put(jr).getText());
		
		args.put(Params.URL, "http://dzh.mop.com/guihua");
		args.put(Params.PROXY_TYPE, "001");
		args.put(Params.COMMENT, "鬼话");
		map.put(Params.ARGS, args);
		jr = new JacksonRepresentation<Map<String,Object>>(map);
		System.out.println(cli.put(jr).getText());
		
		args.put(Params.URL, "http://boxun.eggpain.tk/tops.shtml");
		args.put(Params.PROXY_TYPE, "001");
		args.put(Params.COMMENT, "近期最受欢迎文章");
		map.put(Params.ARGS, args);
		jr = new JacksonRepresentation<Map<String,Object>>(map);
		System.out.println(cli.put(jr).getText());
		
		args.put(Params.URL, "http://tieba.baidu.com/f?ie=utf-8&kw=%E5%90%88%E8%82%A5");
		args.put(Params.PROXY_TYPE, "001");
		args.put(Params.COMMENT, "百度贴吧合肥吧");
		map.put(Params.ARGS, args);
		jr = new JacksonRepresentation<Map<String,Object>>(map);
		System.out.println(cli.put(jr).getText());
		
		args.put(Params.URL, "http://dzh.mop.com/yuanchuang");
		args.put(Params.PROXY_TYPE, "001");
		args.put(Params.COMMENT, "原创区-猫扑大杂烩");
		map.put(Params.ARGS, args);
		jr = new JacksonRepresentation<Map<String,Object>>(map);
		System.out.println(cli.put(jr).getText());
		
		args.put(Params.URL, "http://tieba.baidu.com/f?ie=utf-8&kw=%E5%B7%A2%E6%B9%96");
		args.put(Params.PROXY_TYPE, "001");
		args.put(Params.COMMENT, "百度贴吧巢湖吧");
		map.put(Params.ARGS, args);
		jr = new JacksonRepresentation<Map<String,Object>>(map);
		System.out.println(cli.put(jr).getText());
		
	}
}
