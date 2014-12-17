package com.zxsoft.crawler.store.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.Assert;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.store.Output;
import com.zxsoft.crawler.store.OutputException;

public class RestOutput implements Output {
	private static Logger LOG = LoggerFactory.getLogger(RestOutput.class);
	private static final String url;

	static {
		Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream("output.properties");
		try {
			prop.load(stream);
		} catch (IOException e1) {
		        LOG.error("Load output.properties file failed.");
		}
		url = prop.getProperty("data.output.address");
		if (StringUtils.isEmpty(url)) {
			throw new NullPointerException("data.output.address not set");
		}
		LOG.info("data.output.address: " + url);
	}

	public void write(RecordInfo info) throws OutputException {
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
			String msg = response.getEntity(String.class);
			OutputReturn ret = gson.fromJson(msg, OutputReturn.class);
			if (ret.errorCode != 0) {
				LOG.error("Output Failure: " + ret.errorMessage);
			}
		} catch (ClientHandlerException e) {
			throw new OutputException(e.getMessage());
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.destroy();
			}
		}
	}

	final class OutputReturn {
		int errorCode = -1;
		String errorMessage;
	}

	public int write(List<RecordInfo> recordInfos) throws OutputException {
		// if (2 > 1) return recordInfos.size();
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		if (CollectionUtils.isEmpty(recordInfos))
			return 0;
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
				String json = gson.toJson(map, Map.class);
				WebResource webResource = client.resource(url);
				ClientResponse response = null;
				try {
					response = webResource.type("application/json").post(ClientResponse.class, json);
					String msg = response.getEntity(String.class);
					OutputReturn ret = gson.fromJson(msg, OutputReturn.class);
					if (ret.errorCode != 0) {
						LOG.error("Output Failure: " + ret.errorMessage);
					}
				} catch (ClientHandlerException e) {
					LOG.error(e.getMessage());
					throw new OutputException(e.getMessage());
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
			String json = gson.toJson(map, Map.class);
			WebResource webResource = client.resource(url);
			ClientResponse response = null;
			try {
				response = webResource.type("application/json").post(ClientResponse.class, json);
				String msg = response.getEntity(String.class);
			} catch (ClientHandlerException e) {
				throw new OutputException(e.getMessage());
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (Exception e) {
			throw new OutputException(e.getMessage());
		} finally {
			client.destroy();
		}
		return realSize;
	}

}
