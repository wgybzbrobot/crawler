package com.zxsoft.crawler.web.dao.website;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.entity.ConfDetailId;
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
	
	ConfList getListConf(String url);
	void addListConf(ConfList listConf);
	
	ConfDetail getDetailConf(String listUrl, String host);

	void deleteConfDetail(ConfDetailId id);

	void addDetailConf(ConfDetail detailConf);
	void addDetailConfs(List<ConfDetail> detailConfs);

	List<ConfList> getInspectConfLists(ConfList confList);
}
