package com.zxsoft.crawler.web.service.website;

import org.springframework.stereotype.Service;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;

@Service
public interface WebsiteService {

	Page<Website> getWebsite(final Website website, int pageNo, int pageSize);
	void addWebsite(Website website);
	
	
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
	void save(Website website);
	Website getWebsite(String site);
}
