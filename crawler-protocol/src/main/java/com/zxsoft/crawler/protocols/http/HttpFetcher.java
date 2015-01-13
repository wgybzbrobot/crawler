package com.zxsoft.crawler.protocols.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static HttpBase htmlUnit = new HtmlUnit();
	private static HttpBase httpClient = new HttpClient();
	
	public ProtocolOutput fetch(String url, NameValuePair[] data) throws IOException {
		return httpClient.post(url, data);
	}
	
	public ProtocolOutput fetch(WebPage page) {
		
		ProtocolOutput protocolOutput = new ProtocolOutput();
		String url = page.getBaseUrl();
		
		try {
                        URL u= new URL(url);
                        try {
                                url = URIUtil.encodePathQuery(url, "UTF-8");
                                page.setBaseUrl(url);
                        } catch (URIException e) {
                        }
                } catch (MalformedURLException e1) {
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
			String msg = "Fetch " + url + " failed with protocol exception: " + e.getMessage();
			protocolOutput.setStatus(new ProtocolStatus(url, STATUS_CODE.FAILED, msg));
		} catch (IOException e) {
			String msg = "Fetch " + url + " failed with io exception: " + e.getMessage();
			protocolOutput.setStatus(new ProtocolStatus(url, STATUS_CODE.FAILED, msg));
			LOG.error(msg);
		} catch (Exception e) {
		        String msg = "Fetch " + url + " failed with the following exception:" + e.getMessage();
                        protocolOutput.setStatus(new ProtocolStatus(url, STATUS_CODE.FAILED, msg));
		        LOG.error(msg, e);
                }finally {
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
}
