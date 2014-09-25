package com.zxsoft.crawler.web.dao.website;


import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.Website;

@Repository
public interface WebsiteDao {

	/**
	 * 获取网站
	 * @param params 参数
	 */
	@Transactional
	Page<Website> getWebsites(Website website, int pageNo, int pageSize);
	Website getWebsite(String site);
	void addWebsite(Website website);
	void addWebsites(List<Website> websites);
	
}
