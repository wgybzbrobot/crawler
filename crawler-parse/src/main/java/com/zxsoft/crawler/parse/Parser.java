package com.zxsoft.crawler.parse;

import com.zxsoft.crawler.storage.WebPage;

public abstract class Parser extends ParseTool implements Cloneable {

	
	public abstract ParseStatus parse(WebPage page) throws Exception ;
	
	@Override
	public Parser clone() throws CloneNotSupportedException {
		return (Parser) super.clone();
	}
	
}
