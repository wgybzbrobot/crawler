package com.zxsoft.crawler.urlbase.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.zxsoft.crawler.urlbase.UrlbaseFactory;

@Component
public class UrlbaseRedisFactory extends UrlbaseFactory {

	private StringRedisTemplate template;

	@Autowired
	public UrlbaseRedisFactory(StringRedisTemplate template) {
	    this.template = template;
    }
	
	@Override
    public String peek() {
		
		return "http:baidu";
    }

}
