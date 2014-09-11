package com.zxsoft.crawler.web.service;

import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.framework.utils.Page;

public interface WebsiteService {

	/**
	 * @param pageNo 
	 * @param pageSize
	 * @param params parameters of search
	 */
	Page<ListConf> getListConfs(final int pageNo, final int pageSize, ListConf param);
	Page<DetailConf> getDetailConfs(final int pageNo, final int pageSize, DetailConf param);
	
	/**
	 * Add website's list-page configuration 
	 */
	void add(ListConf listConf);
	/**
	 * add website's detail-page configuration information.
	 */
	void add(DetailConf detailConf);

	boolean listConfExist(String url);
	boolean detailConfExist(String listUrl, String host);
}
