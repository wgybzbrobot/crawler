package com.zxsoft.crawler.parse;

import java.util.concurrent.Callable;

import com.zxsoft.crawler.storage.WebPage;



public class ParseCallable implements Callable<ParseStatus>{

	private Parser parser;
	private WebPage page;
	
	public ParseCallable(Parser parser, WebPage page) {
		super();
		this.parser = parser;
		this.page = page;
	}

	public ParseStatus call() {
		ParseStatus status = null;
		try {
			status = parser.parse(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

}
