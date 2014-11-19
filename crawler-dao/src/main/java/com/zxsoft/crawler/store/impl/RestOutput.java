package com.zxsoft.crawler.store.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.Assert;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.StringUtils;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.store.Output;
import com.zxsoft.crawler.store.OutputException;
import com.zxsoft.crawler.util.CrawlerConfiguration;

public class RestOutput implements Output {
	private static Logger LOG = LoggerFactory.getLogger(RestOutput.class);
	private static final String url ;

	static {
		Configuration conf = CrawlerConfiguration.create();
		url = conf.get("data.output.address");
		if (StringUtils.isEmpty(url)) {
			throw new NullPointerException("data.output.address not set");
		}
	}
	
	public void write(RecordInfo info) /*throws OutputException*/ {
		Assert.notNull(info);
		List<RecordInfo> recordInfos = new LinkedList<RecordInfo>();
		recordInfos.add(info);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("num", 1);
		map.put("records", recordInfos);
		Gson gson = new Gson();
		String json = gson.toJson(map, Map.class);
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = null;
		try {
			response = webResource.type("application/json").post(ClientResponse.class, json);
		} catch (ClientHandlerException e) {
			if (e.getMessage().contains("java.net.ConnectException")) {
				LOG.error("写数据失败, " + url + " 拒绝连接.");
			}
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.destroy();
			}
		}
    }

	public int write(List<RecordInfo> recordInfos) throws OutputException {
		if (2 > 1) return 0;
		if (CollectionUtils.isEmpty(recordInfos)) return 0;
		int realSize = recordInfos.size();
		int size = recordInfos.size();
		int outputSize = 50;
		Client client = Client.create();
		try {
			while (size > outputSize) {
				List<RecordInfo> subList = recordInfos.subList(0, outputSize);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("num", subList.size());
				map.put("records", subList);
				Gson gson = new Gson();
				String json = gson.toJson(map, Map.class);
				
				WebResource webResource = client.resource(url);
				ClientResponse response = null;
				try {
					response = webResource.type("application/json").post(ClientResponse.class, json);
					String msg = response.getEntity(String.class);
				} catch (ClientHandlerException e) {
					throw new OutputException("不能写出数据," + e.getMessage());
				} finally {
					if (response != null) {
						response.close();
					}
				}
				recordInfos = recordInfos.subList(outputSize, size);
				size = recordInfos.size();
			}
		
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("num", size);
			map.put("records", recordInfos);
			Gson gson = new Gson();
			String json = gson.toJson(map, Map.class);
			WebResource webResource = client.resource(url);
			ClientResponse response = null;
			try {
				response = webResource.type("application/json").post(ClientResponse.class, json);
				String msg = response.getEntity(String.class);
			} catch(ClientHandlerException e) {
				throw new OutputException("不能写出数据," + e.getMessage());
			} finally {
				if (response != null) {
					response.close();
				}
			}
		}catch (Exception e) {
		} finally {
			client.destroy();
		}
		return realSize;
    }
	
}
