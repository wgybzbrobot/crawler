package com.zxsoft.crawler.protocols.http;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

@Component
@Scope("prototype")
public class HttpFetcher {
	
	private static Logger LOG = LoggerFactory.getLogger(HttpFetcher.class);

	@Autowired
	private HttpBase htmlUnit;

	@Autowired
	private HttpBase httpClient;

	public ProtocolOutput fetch(String url, boolean ajax) {
		if (!url.matches("^(https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
			LOG.error("Error: not match url regular expression: " + url);
			return null;
		}
		
		if (!ajax) {
			return httpClient.getProtocolOutput(url);
		} else {
			return htmlUnit.getProtocolOutput(url);
		}
	}
	
	public ProtocolOutput fetchNextPage(int pageNum, Document currentDoc, boolean ajax) throws PageBarNotFoundException {
		if (!ajax) {
			return httpClient.getProtocolOutputOfNextPage(pageNum, currentDoc);
		} else {
			return htmlUnit.getProtocolOutputOfNextPage(pageNum, currentDoc);
		}
	}
	public ProtocolOutput fetchPrevPage(int pageNum, Document currentDoc, boolean ajax) throws PrevPageNotFoundException, PageBarNotFoundException {
		if (!ajax) {
			return httpClient.getProtocolOutputOfPrevPage(pageNum, currentDoc);
		} else {
			return htmlUnit.getProtocolOutputOfPrevPage(pageNum, currentDoc);
		}
	}
	public ProtocolOutput fetchLastPage(Document currentDoc, boolean ajax) throws PageBarNotFoundException {
		if (!ajax) {
			return httpClient.getProtocolOutputOfLastPage(currentDoc);
		} else {
			return htmlUnit.getProtocolOutputOfLastPage(currentDoc);
		}
	}
	
	
	
	
	
	
	
	

	public ProtocolOutput fetch(String url) {
		return httpClient.getProtocolOutput(url);
	}
}
