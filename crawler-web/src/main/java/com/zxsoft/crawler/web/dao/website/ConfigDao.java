package com.zxsoft.crawler.web.dao.website;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.entity.ConfList;


@Repository
public interface ConfigDao {

	/**
	 * @param url 版块url
	 */
	ConfList getConfList(String url);
	
	/**
	 * @param url 版块url
	 */
	List<ConfDetail> getConfDetails(String url);
}
