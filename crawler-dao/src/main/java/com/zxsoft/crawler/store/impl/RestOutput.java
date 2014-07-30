package com.zxsoft.crawler.store.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.store.Output;
import com.zxsoft.crawler.store.OutputException;

@Component
@Scope("prototype")
public class RestOutput implements Output {

	@Value("${output.address}")
	private String url ;
	
	public void write(RecordInfo info) throws OutputException {
		List<RecordInfo> recordInfos = new LinkedList<RecordInfo>();
		recordInfos.add(info);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("num", 1);
		map.put("records", recordInfos);
		Gson gson = new Gson();
		String json = gson.toJson(map, Map.class);
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.type("application/json").post(ClientResponse.class, json);
		if (response == null || response.getStatus() != 200) {
			throw new OutputException("Fail to post data to " + url);
		}
    }

	public int write(List<RecordInfo> recordInfos) throws OutputException {
		Assert.notEmpty(recordInfos);
		
		int size = recordInfos.size();
		int outputSize = 300;
		while (size > outputSize) {
			List<RecordInfo> subList = recordInfos.subList(0, outputSize);
			for (RecordInfo recordInfo : subList) {
	            System.out.println(recordInfo.getTitle());
            }
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("num", subList.size());
			map.put("records", subList);
			Gson gson = new Gson();
			String json = gson.toJson(map, Map.class);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			ClientResponse response = webResource.type("application/json").post(ClientResponse.class, json);
			if (response == null || response.getStatus() != 200) {
				throw new OutputException("Fail to post data to target address: " + url);
			}
			recordInfos = recordInfos.subList(outputSize, size);
			size = recordInfos.size();
		}
		
		for (RecordInfo recordInfo : recordInfos) {
            System.out.println(recordInfo.getTitle());
        }
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("num", size);
		map.put("records", recordInfos);
		Gson gson = new Gson();
		String json = gson.toJson(map, Map.class);
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.type("application/json").post(ClientResponse.class, json);
		if (response == null || response.getStatus() != 200) {
			throw new OutputException("Fail to post data to target address: " + url);
		}
			 
		return size;
    }
	
}
