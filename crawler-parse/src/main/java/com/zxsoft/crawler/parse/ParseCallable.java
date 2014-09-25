package com.zxsoft.crawler.parse;

import java.util.concurrent.Callable;

import org.jsoup.select.Selector.SelectorParseException;

import com.zxsoft.crawler.parse.FetchStatus.Status;
import com.zxsoft.crawler.storage.WebPage;



public class ParseCallable implements Callable<FetchStatus>{

	private Parser parser;
	private WebPage page;
	
	public ParseCallable(Parser parser, WebPage page) {
		super();
		this.parser = parser;
		this.page = page;
	}

	public FetchStatus call() {
		FetchStatus status = null;
		try {
			status = parser.parse(page);
		} catch (SelectorParseException e) { 
			e.printStackTrace();
			status = new FetchStatus();
			status.setStatus(Status.CONF_ERROR);
			status.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			status = new FetchStatus();
			status.setMessage(e.getMessage());
		} finally {
			return status;
		}
	}

}
