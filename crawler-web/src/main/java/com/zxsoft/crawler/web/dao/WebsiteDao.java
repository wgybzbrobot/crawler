package com.zxsoft.crawler.web.dao;

import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;

public interface WebsiteDao {

	ListConf getListConf(String url);
	DetailConf getDetailConf(String listUrl, String host);
}
