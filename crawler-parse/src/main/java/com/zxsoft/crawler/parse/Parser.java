package com.zxsoft.crawler.parse;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.zxsoft.crawler.dao.ConfDao;
import com.zxsoft.crawler.duplicate.DuplicateInspector;
import com.zxsoft.crawler.indexer.IndexWriter;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.protocols.http.PageHelper;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;

//@Component
//@Scope("prototype")
public abstract class Parser {

//	@Autowired
	protected HttpFetcher httpFetcher = ParseUtil.ctx.getBean(HttpFetcher.class);
//	@Autowired
	protected IndexWriter indexWriter = ParseUtil.ctx.getBean(IndexWriter.class);
//	@Autowired
	protected DuplicateInspector duplicateInspector = ParseUtil.ctx.getBean(DuplicateInspector.class);
//	@Autowired
	protected ConfDao confDao = ParseUtil.ctx.getBean(ConfDao.class);
//	@Autowired
	protected PageHelper pageHelper = ParseUtil.ctx.getBean(PageHelper.class);
	
	protected List<RecordInfo> recordInfos = new LinkedList<RecordInfo>();
	
	public abstract ParseStatus parse(WebPage page) throws Exception ;
	
}
