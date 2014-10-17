package com.zxsoft.crawler.protocols.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.LogManager;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.StringUtils;

import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
import com.zxsoft.crawler.protocols.http.htmlunit.HtmlUnit;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClient;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

public class HttpFetcher {
	
	private static Logger LOG = LoggerFactory.getLogger(HttpFetcher.class);
	private static org.apache.log4j.Logger errorLogger = LogManager.getLogger("ProtocolErrorLog"); 
	
	private Configuration conf;

	private HttpBase htmlUnit;
	
	private HttpBase httpClient;
	
	public HttpFetcher(Configuration conf) {
		this.conf = conf;
		htmlUnit = new HtmlUnit(conf);
		httpClient = new HttpClient(conf);
	}
	

	public ProtocolOutput fetch(String url, NameValuePair[] data) throws IOException {
		return httpClient.post(url, data);
	}
	
	public ProtocolOutput fetch(WebPage page) {
		
		ProtocolOutput protocolOutput = new ProtocolOutput();
		String url = page.getBaseUrl();
		if (StringUtils.isEmpty(url) || !url.matches("^(https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
			LOG.error("Error: not match url regular expression: " + url);
			ProtocolStatus status = new ProtocolStatus();
			status.setMessage(url + "不是网络地址.");
			status.setCode(STATUS_CODE.INVALID_URL);
			protocolOutput = new ProtocolOutput(null, status);
			return protocolOutput;
		}
		
		try {
		if (page.isAjax()) {
			protocolOutput = htmlUnit.getProtocolOutput(page);
		} else {
			protocolOutput = httpClient.getProtocolOutput(page);
		}
		} catch (ProtocolException e) {
			String msg = "Fetch " + url + " failed with the following error:" + e.getMessage();
			protocolOutput.setStatus(new ProtocolStatus(url, STATUS_CODE.FAILED, msg));
		} catch (IOException e) {
			String msg = "Fetch " + url + " failed with the following error:" + e.getMessage();
			protocolOutput.setStatus(new ProtocolStatus(url, STATUS_CODE.FAILED, msg));
			LOG.error(msg);
		} finally {
			return protocolOutput;
		}
	}
	
	public ProtocolOutput fetchNextPage(int pageNum, WebPage page) throws PageBarNotFoundException {
		if (!page.isAjax()) {
			return httpClient.getProtocolOutputOfNextPage(pageNum, page);
		} else {
			return htmlUnit.getProtocolOutputOfNextPage(pageNum, page);
		}
	}
	public ProtocolOutput fetchPrevPage(int pageNum, WebPage page) throws PrevPageNotFoundException, PageBarNotFoundException {
		if (!page.isAjax()) {
			return httpClient.getProtocolOutputOfPrevPage(pageNum, page);
		} else {
			return htmlUnit.getProtocolOutputOfPrevPage(pageNum, page);
		}
	}
	public ProtocolOutput fetchLastPage(WebPage page) throws PageBarNotFoundException {
		if (!page.isAjax()) {
			return httpClient.getProtocolOutputOfLastPage(page);
		} else {
			return htmlUnit.getProtocolOutputOfLastPage(page);
		}
	}
	
	
//	public ProtocolOutput fetch(String url) {
//		return fetch(url, false);
//	}
}
