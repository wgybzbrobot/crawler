package com.zxsoft.crawler.parse;

import com.zxsoft.crawler.storage.WebPageMy;


public abstract class Parser {

	public WebPageMy page;
	
	public abstract ParseStatus parse(WebPageMy page) throws Exception ;
	
}
