package com.zxsoft.crawler.parse;

import com.zxsoft.crawler.storage.WebPage;

public abstract class Parser extends ParseTool  {
	
	public abstract ParseStatus parse(WebPage page) throws Exception;
	
}
