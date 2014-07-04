package com.zxsoft.crawler.util.parse;

import com.zxsoft.crawler.storage.WebPageMy;
import com.zxsoft.crawler.tools.Tool;


public abstract class Parser implements Tool {

	public WebPageMy page;
	
	public abstract ParseStatus parse(WebPageMy page) throws Exception ;
	
}
