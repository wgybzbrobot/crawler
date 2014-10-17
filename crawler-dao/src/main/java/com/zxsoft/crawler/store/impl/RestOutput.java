package com.zxsoft.crawler.store.impl;

import java.net.MalformedURLException;
import java.net.URL;
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
import com.zxsoft.crawler.dns.DNSCache;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.store.Output;
import com.zxsoft.crawler.store.OutputException;

public class RestOutput implements Output {

	private static Logger LOG = LoggerFactory.getLogger(RestOutput.class);
	
	private Configuration conf;

	private String url ;
	
	public  RestOutput (Configuration conf) {
		this.conf = conf;
		url = conf.get("data.output.address");
	}
	
	/*static {
		Resource resource = new ClassPathResource("config.properties");
		Properties properties = new Properties();
		InputStream in;
        try {
	        in = resource.getInputStream();
	        properties.load(in);
	        url = properties.getProperty("output_url");
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}*/
	
	public void write(RecordInfo info) /*throws OutputException*/ {
		Assert.hasLength(url);
		Assert.notNull(info);
		
		if (StringUtils.isEmpty(info.getIp())) {
			try {
	            info.setIp(new DNSCache().getAsString(new URL(info.getUrl())));
            } catch (MalformedURLException e) {
	            e.printStackTrace();
            }
		}
		
		List<RecordInfo> recordInfos = new LinkedList<RecordInfo>();
		recordInfos.add(info);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("num", 1);
		map.put("records", recordInfos);
		Gson gson = new Gson();
		String json = gson.toJson(map, Map.class);
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		
		try {
			ClientResponse response = webResource.type("application/json").post(ClientResponse.class, json);
//			if (response == null || response.getStatus() != 200) {
//				throw new OutputException("Fail to post data to " + url);
//			}
		} catch (ClientHandlerException e) {
			if (e.getMessage().contains("java.net.ConnectException")) {
				LOG.error("写数据失败, " + url + " 拒绝连接.");
			}
			
		}
    }

	public int write(List<RecordInfo> recordInfos) throws OutputException {
		Assert.hasLength(url);
		
		if (CollectionUtils.isEmpty(recordInfos)) return 0;
		
//		if (2 > 1)return 0;
		
		if (StringUtils.isEmpty(recordInfos.get(0).getIp())) {
			RecordInfo info = recordInfos.get(0);
			try {
	            info.setIp(new DNSCache().getAsString(new URL(info.getUrl())));
            } catch (MalformedURLException e) {
	            e.printStackTrace();
            }
		}
		
		int realSize = recordInfos.size();
		int size = recordInfos.size();
		int outputSize = 300;
		while (size > outputSize) {
			List<RecordInfo> subList = recordInfos.subList(0, outputSize);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("num", subList.size());
			map.put("records", subList);
			Gson gson = new Gson();
			String json = gson.toJson(map, Map.class);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			ClientResponse response = null;
			try {
				response = webResource.type("application/json").post(ClientResponse.class, json);
			} catch (ClientHandlerException e) {
				throw new OutputException("不能写出数据," + e.getMessage());
			}
			if (response == null || response.getStatus() != 200) {
				throw new OutputException("Fail to post data to target address: " + url);
			}
			recordInfos = recordInfos.subList(outputSize, size);
			size = recordInfos.size();
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("num", size);
		map.put("records", recordInfos);
		Gson gson = new Gson();
		String json = gson.toJson(map, Map.class);
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = null;
		try {
			response = webResource.type("application/json").post(ClientResponse.class, json);
		} catch(ClientHandlerException e) {
			throw new OutputException("不能写出数据," + e.getMessage());
		}
		if (response == null || response.getStatus() != 200) {
			throw new OutputException("Fail to post data to target address: " + url);
		}
			 
		return realSize;
    }
	
}
