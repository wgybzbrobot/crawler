package com.zxsoft.crawler.util.parse;

import java.util.concurrent.Callable;

import com.zxsoft.crawler.storage.WebPageMy;


public class ParseCallable implements Callable<ParseStatus>{

	private Parser parser;
	private WebPageMy page;
	
	
	public ParseCallable(Parser parser, WebPageMy page) {
		super();
		this.parser = parser;
		this.page = page;
	}


	public ParseStatus call() throws Exception {
		return parser.parse(page);
	}

}
