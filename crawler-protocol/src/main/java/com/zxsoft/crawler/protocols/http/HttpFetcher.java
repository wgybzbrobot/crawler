package com.zxsoft.crawler.protocols.http;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

@Component
@Scope("prototype")
public class HttpFetcher {
	
	private static Logger LOG = LoggerFactory.getLogger(HttpFetcher.class);
	private static org.apache.log4j.Logger errorLogger = LogManager.getLogger("ProtocolErrorLog"); 
	
	@Autowired
	private HttpBase htmlUnit;

	@Autowired
	private HttpBase httpClient;

	public ProtocolOutput fetch(String url, boolean ajax) {
		if (StringUtils.isEmpty(url) || !url.matches("^(https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
			LOG.error("Error: not match url regular expression: " + url);
			return null;
		}
		
		ProtocolOutput protocolOutput = null;
		try {
		if (!ajax) {
			protocolOutput = httpClient.getProtocolOutput(url);
		} else {
			protocolOutput = htmlUnit.getProtocolOutput(url);
		}
		} catch (ProtocolException e) {
			errorLogger.error("Fetch " + url + " failed with the following error:", e);
		} catch (IOException e) {
			errorLogger.error("Fetch " + url + " failed with the following error:", e);
		} finally {
			return protocolOutput;
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
		return fetch(url, false);
	}
}
