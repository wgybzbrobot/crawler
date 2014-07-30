package com.zxsoft.crawler.urlbase.impl;

import java.util.LinkedList;
import java.util.List;

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
    public List<WebPage> getWebPages(int num) {
		List<WebPage> pages = new LinkedList<WebPage>();
		WebPage page = new WebPage();
		page.setBaseUrl("http://tieba.baidu.com/f?kw=%B0%F6%B2%BA");
//		page.setBaseUrl("http://bbs.tianya.cn/list-free-1.shtml");
		page.setAjax(false);
		page.setPrevFetchTime(0);
		page.setType("001");
		pages.add(page);
		
		page = new WebPage("http://bbs.tianya.cn/list-free-1.shtml", false, 0);
		page.setType("002");
		pages.add(page);
		
	    return pages;
    }

}
