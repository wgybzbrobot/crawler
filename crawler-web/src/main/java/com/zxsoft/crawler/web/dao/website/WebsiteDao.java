package com.zxsoft.crawler.web.dao.website;


import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Auth;
import com.zxsoft.crawler.entity.Website;

@Repository
public interface WebsiteDao {

	/**
	 * 获取网站
	 * @param params 参数
	 */
	@Transactional
	Page<Website> getWebsites(Website website, int pageNo, int pageSize);
	Website getWebsite(String id);
	void addWebsite(Website website);
	void addWebsites(List<Website> websites);
        void deleteWebsite(Website website);
	/**
	 * @param id webiste id
	 */
	List<Auth> getAuths(String id);
	
	/**
	 * @param id auth id
	 */
	Auth getAuth(String id);
	void addAuth(Auth auth);
	void deleteAuth(String id);

}
