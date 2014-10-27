package com.zxsoft.crawler.web.service.crawler.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import com.zxsoft.crawler.master.MasterPath;
import com.zxsoft.crawler.web.service.crawler.SlaveService;

public class SlaveServiceImpl extends SimpleCrawlerServiceImpl implements SlaveService {

	public SlaveServiceImpl() {
		super();
	}

	@Override
	public List<Map<String, Object>> slaves() throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create();
		WebResource webResource = client.resource(CRAWLER_MASTER
		        + MasterPath.SLAVE_RESOURCE_PATH);
		String text = webResource.get(String.class);
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		Map<String, Object> map = gson.fromJson(text, Map.class);
		result = (List<Map<String, Object>>) map.get("slavestatus");
		return result;
	}

}
