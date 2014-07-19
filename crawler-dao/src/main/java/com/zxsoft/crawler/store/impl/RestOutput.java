package com.zxsoft.crawler.store.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.store.Output;

@Component
//@ConfigurationProperties(prefix="datasource.index")
public class RestOutput implements Output {

	private Logger LOG = LoggerFactory.getLogger(RestOutput.class);
	
//	@Autowired
	private RestTemplate restTemplate;

	private static String url;
	
	public void write(RecordInfo info) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<RecordInfo> request = new HttpEntity<RecordInfo>(info, headers);
				
		LOG.info(info.getUrl());
//		restTemplate.postForObject(url, request, RecordInfo.class);
    }

	public void write(List<RecordInfo> recordInfos) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<List<RecordInfo>> request = new HttpEntity<List<RecordInfo>>(recordInfos, headers);
				
		LOG.info("recordinfo size: " + recordInfos.size());
//		restTemplate.postForObject(url, request, List.class);
	    
    }
	
	
}
