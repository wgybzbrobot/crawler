package com.zxsoft.crawler.store.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.util.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.store.Output;
import com.zxsoft.crawler.store.OutputException;

public class RestOutputTest {

	private Output output = new RestOutput();
	
	@Test
	public void testWrite() throws OutputException /*throws OutputException*/ {
		RecordInfo info = new RecordInfo("Test", "http://test.org", System.currentTimeMillis());
		output.write(info);
	}
	
	@Test
	public void testJerseyClient() {
		Client client = Client.create();
		WebResource webResource = client.resource("http://192.168.32.11:8900/sentiment/index");
		
		RecordInfo info = new RecordInfo("Test", "http://test.org", System.currentTimeMillis());
		info.setIdentify_md5("xiayun");
		List<RecordInfo> recordInfos = new ArrayList<RecordInfo>();
		recordInfos.add(info);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("num", 1);
		map.put("records", recordInfos);
		String json = new GsonBuilder().disableHtmlEscaping().create().toJson(map,Map.class);
		ClientResponse response = webResource.type("application/json").post(ClientResponse.class, json);
		Assert.isTrue(response.getStatus() == 200);
		String res = response.getEntity(String.class);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		Map r = gson.fromJson(res, Map.class);
		System.out.println(res);
	}

}
