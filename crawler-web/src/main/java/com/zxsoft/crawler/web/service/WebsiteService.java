package com.zxsoft.crawler.web.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.framework.utils.Page;

@Service
public interface WebsiteService {

	/**
	 * @param pageNo 
	 * @param pageSize
	 * @param params parameters of search
	 */
	Page<ListConf> getListConfs(final int pageNo, final int pageSize, ListConf param);
	
	/**
	 * Non-Instant Search
	 * Add website's list-page configuration and detail-page configuration information.
	 */
	void add(ListConf listConf, DetailConf detailConf);
	
	
}
