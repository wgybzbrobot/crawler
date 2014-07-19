package com.zxsoft.crawler.urlbase;

import org.springframework.stereotype.Component;

import com.zxsoft.crawler.storage.WebPage;

@Component
public abstract class UrlbaseFactory {
	
	/**
	 * @return url
	 */
	public abstract WebPage peek();

}
