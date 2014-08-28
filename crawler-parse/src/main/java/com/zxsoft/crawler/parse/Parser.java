package com.zxsoft.crawler.parse;

import com.zxsoft.crawler.storage.WebPage;

public abstract class Parser extends ParseTool  {
	
	public abstract FetchStatus parse(WebPage page) throws Exception;
	
}
