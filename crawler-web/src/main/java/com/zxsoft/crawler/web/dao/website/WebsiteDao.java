package com.zxsoft.crawler.web.dao.website;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;

@Service
public interface WebsiteDao {

	/**
	 * 获取网站
	 * @param params 参数
	 */
	@Transactional
	Page<Website> getWebsites(Website website, int pageNo, int pageSize);
	void addWebsite(Website website);
	void addWebsites(List<Website> websites);
	
	
	ListConf getListConf(String url);
	DetailConf getDetailConf(String listUrl, String host);
	Website getWebsite(String site);
}
