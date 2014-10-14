package com.zxsoft.crawler.parse;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.dao.ConfDao;
import com.zxsoft.crawler.duplicate.DuplicateInspector;
import com.zxsoft.crawler.duplicate.impl.RedisDuplicateInspector;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.store.Output;
import com.zxsoft.crawler.store.impl.RestOutput;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

public abstract class ParseTool {
	private Logger LOG = LoggerFactory.getLogger(ParseTool.class);
	
	private Configuration conf;
	
	private static HttpFetcher httpFetcher ;
	
	protected static Output indexWriter ;
	protected static DuplicateInspector duplicateInspector = new RedisDuplicateInspector();
	protected  ConfDao confDao = new ConfDao();

	public void setConf(Configuration conf) {
		this.conf = conf;
		httpFetcher = new HttpFetcher(conf);
		indexWriter = new RestOutput(conf);
	}
	
	protected ProtocolOutput fetch(String url, boolean ajax) {
	    return httpFetcher.fetch(url, ajax);
    }
	
	/**
	 * 获取上一页
	 */
	protected ProtocolOutput fetchPrevPage(int pageNum, Document currentDoc, boolean ajax, boolean needAuth) {
		try {
			return httpFetcher.fetchPrevPage(pageNum, currentDoc, ajax, needAuth);
		} catch (PrevPageNotFoundException e) {
			LOG.debug("Cannot get preview page of " + currentDoc.location()
			        + ", may be it has no preview page.");
		} catch (PageBarNotFoundException e) {
			LOG.debug("Cannot get page bar of " + currentDoc.location()
			        + ", may be it has no page bar.");
		}
		ProtocolStatus status = new ProtocolStatus();
		status.setCode(STATUS_CODE.FAILED);
		status.setMessage("Cannot get preview page of " + currentDoc.location() + ", may be it has no preview page.");
		return new ProtocolOutput(null, status);
	}

	/**
	 * 获取下一页
	 */
	protected ProtocolOutput fetchNextPage(int pageNum, Document currentDoc, boolean ajax, boolean needAuth) {
		try {
			return httpFetcher.fetchNextPage(pageNum, currentDoc, ajax, needAuth);
		} catch (PageBarNotFoundException e) {
			LOG.debug("Cannot get Next page of " + currentDoc.location() + ", may be it has no next page.");
		}
		ProtocolStatus status = new ProtocolStatus();
		status.setCode(STATUS_CODE.FAILED);
		status.setMessage("Cannot get Next page of " + currentDoc.location() + ", may be it has no next page.");
		return new ProtocolOutput(null, status);
	}

	/**
	 * 获取最后页
	 */
	protected ProtocolOutput fetchLastPage(Document currentDoc, boolean ajax, boolean needAuth) {
		try {
			return httpFetcher.fetchLastPage(currentDoc, ajax, needAuth);
		} catch (PageBarNotFoundException e) {
			LOG.debug("Cannot get last page of " + currentDoc.location() + ", may be it has no last page.");
		}
		ProtocolStatus status = new ProtocolStatus();
		status.setCode(STATUS_CODE.FAILED);
		status.setMessage("Cannot get last page of " + currentDoc.location() + ", may be it has no last page.");
		return new ProtocolOutput(null, status);
	}

}
