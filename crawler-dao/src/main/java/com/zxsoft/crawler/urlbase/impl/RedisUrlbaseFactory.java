package com.zxsoft.crawler.urlbase.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

//import zx.soft.redis.client.standone.impl.RedisStandoneClient;

import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.urlbase.UrlbaseFactory;

@Component
public class RedisUrlbaseFactory extends UrlbaseFactory {

//	private StringRedisTemplate template;
//
//	@Autowired
//	public RedisUrlbaseFactory(StringRedisTemplate template) {
//	    this.template = template;
//    }
	
	@Override
    public List<WebPage> getWebPages(int num) {
		
//		RedisStandoneClient client = new RedisStandoneClient("", 6397, "zxsoft");
		
		
		
		List<WebPage> pages = new LinkedList<WebPage>();
		WebPage page = new WebPage();
		page.setBaseUrl("http://tieba.baidu.com/f?kw=%B0%F6%B2%BA");
		page.setAjax(false);
		page.setPrevFetchTime(0);
		page.setType("001");
		pages.add(page);
		
		page = new WebPage("http://bbs.anhuinews.com/forum-319-1.html", false, 0);
		page.setType("001");
		pages.add(page);
		
	    return pages;
    }

}
