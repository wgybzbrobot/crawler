package com.zxsoft.crawler.parse;

import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.zxsoft.crawler.dao.ConfDao;
import com.zxsoft.crawler.duplicate.DuplicateInspector;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.store.Output;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

public abstract class ParseTool {
	private Logger LOG = LoggerFactory.getLogger(ParseTool.class);
//	private static Configuration conf;
//	public void setConf(Configuration configuration) {
//		conf = configuration;
//	}
	
	public static void init(ApplicationContext ctx) {
		httpFetcher = ctx.getBean(HttpFetcher.class);
		indexWriter = ctx.getBean(Output.class);
		duplicateInspector = ctx.getBean(DuplicateInspector.class);
		confDao = ctx.getBean(ConfDao.class);
//		parserFactory = ctx.getBean(ParserFactory.class);
//		parserFactory.setConf(conf);
	}

	private static HttpFetcher httpFetcher;
	protected static Output indexWriter;
	protected static DuplicateInspector duplicateInspector;
	protected static ConfDao confDao;
//	private static ParserFactory parserFactory;
	

	protected ProtocolOutput fetch(String url, boolean ajax) {
	    return httpFetcher.fetch(url, ajax);
    }
	
//	protected Parser getParserByCategory(String category) throws ParserNotFoundException {
//		return parserFactory.getParserByCategory(category);
//	}
	
	/**
	 * 获取上一页
	 */
	protected ProtocolOutput fetchPrevPage(int pageNum, Document currentDoc, boolean ajax) {
		try {
			return httpFetcher.fetchPrevPage(pageNum, currentDoc, ajax);
		} catch (PrevPageNotFoundException e) {
			LOG.warn("Cannot get preview page of " + currentDoc.location()
			        + ", may be it has no preview page.");
		} catch (PageBarNotFoundException e) {
			LOG.warn("Cannot get page bar of " + currentDoc.location()
			        + ", may be it has no page bar.");
		}
		return null;
	}

	/**
	 * 获取下一页
	 */
	protected ProtocolOutput fetchNextPage(int pageNum, Document currentDoc, boolean ajax) {
		try {
			return httpFetcher.fetchNextPage(pageNum, currentDoc, ajax);
		} catch (PageBarNotFoundException e) {
			LOG.warn("Cannot get Next page of " + currentDoc.location() + ", may be it has no next page.");
		}
		return null;
	}

	/**
	 * 获取最后页
	 */
	protected ProtocolOutput fetchLastPage(Document currentDoc, boolean ajax) {
		try {
			return httpFetcher.fetchLastPage(currentDoc, ajax);
		} catch (PageBarNotFoundException e) {
			LOG.warn("Cannot get last page of " + currentDoc.location() + ", may be it has no last page.");
		}
		return null;
	}

}
