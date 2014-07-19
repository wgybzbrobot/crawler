package com.zxsoft.crawler.parse;

import java.util.LinkedList;
import java.util.List;

import com.zxsoft.crawler.dao.ConfDao;
import com.zxsoft.crawler.duplicate.DuplicateInspector;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClientPageHelper;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.store.Output;

public abstract class Parser {

	protected HttpFetcher httpFetcher = ParseUtil.ctx.getBean(HttpFetcher.class);
	protected Output indexWriter = ParseUtil.ctx.getBean(Output.class);
	protected DuplicateInspector duplicateInspector = ParseUtil.ctx.getBean(DuplicateInspector.class);
	protected ConfDao confDao = ParseUtil.ctx.getBean(ConfDao.class);
	protected HttpClientPageHelper pageHelper = ParseUtil.ctx.getBean(HttpClientPageHelper.class);
	
	protected List<RecordInfo> recordInfos = new LinkedList<RecordInfo>();
	
	public abstract ParseStatus parse(WebPage page) throws Exception ;
	
}
