package com.zxsoft.crawler.web.service.website.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.web.dao.website.ConfigDao;
import com.zxsoft.crawler.web.service.website.ConfigService;

@Service
public class ConfigServiceImpl implements ConfigService {

	@Autowired
	private ConfigDao configDaoImpl;
	
	@Override
	public Map<String, Object> getConfig(String url) {
		ConfList confList = configDaoImpl.getConfList(url);
		List<ConfDetail> confDetails = configDaoImpl.getConfDetails(url);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("confList", confList);
		map.put("confDetails", confDetails);
		
		
		
		return map;
	}

}
