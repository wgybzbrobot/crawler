package com.zxsoft.crawler.urlbase.impl;

import java.util.LinkedList;
import java.util.List;

import zx.soft.redis.client.shard.impl.RedisShardClient;

import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.storage.WebPage.JOB_TYPE;
import com.zxsoft.crawler.urlbase.UrlbaseFactory;

public class RedisUrlbaseFactory extends UrlbaseFactory {

	@Override
    public List<WebPage> getWebPages(int num) {
		
		List<WebPage> pages = new LinkedList<WebPage>();
		WebPage page = new WebPage();
		page.setBaseUrl("http://tieba.baidu.com/f?kw=%B0%F6%B2%BA");
		page.setAjax(false);
		page.setPrevFetchTime(0);
		page.setType("001");
		page.setJobType(JOB_TYPE.NETWORK_INSPECT);
		
		pages.add(page);
		
//		RedisShardClient
		
		
		
		
	    return pages;
    }

}
