package com.zxsoft.crawler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.api.JobManager.JobType;

public class JobResourceTest {

	private static String baseUrl = "http://localhost:8989/slave/jobs";

	@Test
	public void testCreateNetworkInspectJob() throws IOException {
		ClientResource client = new ClientResource(baseUrl);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Params.CRAWL_ID, "test");
		map.put(Params.JOB_TYPE, JobType.NETWORK_INSPECT.toString());

		Map<String, Object> args = new HashMap<String, Object>();
		args.put(Params.Interval, 100 * 200 * 60 * 1000);

		args.put(Params.URL, "http://tieba.baidu.com/f?ie=utf-8&kw=%E8%9A%8C%E5%9F%A0");
		map.put(Params.ARGS, args);
		client.put(map);
	}

	@Test
	public void testInspectNews() {
		ClientResource client = new ClientResource(baseUrl);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Params.CRAWL_ID, "test");
		map.put(Params.JOB_TYPE, JobType.NETWORK_INSPECT.toString());
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(Params.URL, "http://roll.news.sina.com.cn/s/channel.php");
		args.put(Params.Interval, 20 * 60 * 1000);
		map.put(Params.ARGS, args);
		client.put(map);
	}

	@Test
	public void testCreateNetworkSearchJob() {
		ClientResource client = new ClientResource(baseUrl);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Params.CRAWL_ID, "百度搜索吸毒");
		map.put(Params.JOB_TYPE, JobType.NETWORK_SEARCH.toString());

		Map<String, Object> args = new HashMap<String, Object>();
		args.put(Params.KEYWORD, "吸毒");
		// args.put(Params.ENGINE_URL, "sougou");
		args.put(Params.ENGINE_URL, "http://www.baidu.com/s?wd=%s");
		args.put(Params.Interval, 0L);

		map.put(Params.ARGS, args);
		Representation representation = client.put(map);
	}

	@Test
	public void testGetJob() throws IOException {
		String param = "?crawl=&job=&cmd=" + Params.JOB_CMD_GET;
		ClientResource client = new ClientResource(baseUrl + param);
		Representation representation = client.get();
		System.out.println(representation.getText());
	}

	@Test
	public void testInfoAPI() throws ResourceException, IOException {
		ClientResource client = new ClientResource(baseUrl);
		System.out.println(client.get().getText());
	}

	@Test
	public void testCreateWeiboSearchJob() {
		ClientResource client = new ClientResource(baseUrl);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Params.CRAWL_ID, "test");
		map.put(Params.JOB_TYPE, JobType.NETWORK_SEARCH.toString());

		Map<String, Object> args = new HashMap<String, Object>();
		args.put(Params.KEYWORD, "吸毒");
		args.put(Params.ENGINE_URL, "baidu");
		args.put(Params.Interval, 0L);

		map.put(Params.ARGS, args);
		Representation representation = client.put(map);
	}
}
