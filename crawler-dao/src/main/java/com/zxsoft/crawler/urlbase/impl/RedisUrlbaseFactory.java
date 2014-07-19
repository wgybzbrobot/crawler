package com.zxsoft.crawler.urlbase.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.urlbase.UrlbaseFactory;

@Component
public class RedisUrlbaseFactory extends UrlbaseFactory {

	private StringRedisTemplate template;

	@Autowired
	public RedisUrlbaseFactory(StringRedisTemplate template) {
	    this.template = template;
    }
	
	@Override
    public WebPage peek() {
		WebPage page = new WebPage();
		page.setBaseUrl("http://tieba.baidu.com/f?kw=%B0%F6%B2%BA");
		page.setAjax(false);
		page.setPrevFetchTime(0);
		
		return page;
    }

}
