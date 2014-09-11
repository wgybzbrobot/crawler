package com.zxsoft.crawler.web.service.crawler.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.web.service.crawler.JobService;

public class TestJobServiceImpl {

	private JobService jobService = new JobServiceImpl();
	
	@Test
	public void testAddInspectJob() {
		String url = "http://bbs.tianya.cn/list-free-1.shtml";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(Params.URL, url);
		
		jobService.addInsecptJob(args);
	}
	
	@Test
	public void testAddSearchJob() {
		String keyword = "吸毒";
		List<String> engineIds = new LinkedList<String>();
		engineIds.add("baidu");
		engineIds.add("sougou");
		
		for (String engineId : engineIds) {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put(Params.KEYWORD, keyword);
			args.put(Params.ENGINE_ID, engineId);
			jobService.addSearchJob(args);
		}
		
	}
}
