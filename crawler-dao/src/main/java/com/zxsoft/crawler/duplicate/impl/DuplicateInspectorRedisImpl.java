package com.zxsoft.crawler.duplicate.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.zxsoft.crawler.duplicate.DuplicateInspector;

@Service
@PropertySource("classpath:application.properties")
public class DuplicateInspectorRedisImpl implements DuplicateInspector {

	private RestTemplate restTemplate;
	
	@Autowired
	public DuplicateInspectorRedisImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public boolean md5Exist(String md5) {
		
		return false;
	}

}
