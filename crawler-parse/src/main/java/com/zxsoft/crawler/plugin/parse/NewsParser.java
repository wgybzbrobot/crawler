package com.zxsoft.crawler.plugin.parse;

import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.ParseStatus;
import com.zxsoft.crawler.parse.ParseStatus.Status;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.NewsDetailConf;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.store.OutputException;
import com.zxsoft.crawler.util.Md5Signatrue;
import com.zxsoft.crawler.util.Utils;

public class NewsParser extends Parser {

    private static Logger LOG = LoggerFactory.getLogger(NewsParser.class);
 
	private ThreadLocal<String> mainUrl = new ThreadLocal<String>();
	private ThreadLocal<String> rule = new ThreadLocal<String>() { protected String initialValue() { return "";}};
	private ThreadLocal<Long> prevFetchTime = new ThreadLocal<Long>();
	private ThreadLocal<Boolean> ajax = new ThreadLocal<Boolean>();
	private ThreadLocal<List<RecordInfo>> threadLocalRecordInfos = new ThreadLocal<List<RecordInfo>>() {
		protected List<RecordInfo> initialValue() {
			return new LinkedList<RecordInfo>();
		}
	};
	private  ThreadLocal<DetailConf> threadLocalDetailConf = new ThreadLocal<DetailConf>() {
		protected DetailConf initialValue() {
			return new DetailConf();
		}
	};
    
    @Override
    public ParseStatus parse(WebPage page) throws Exception {
    	Assert.notNull(page, "Page is null");
		Document document = page.getDocument();
		Assert.notNull(document, "Document is null");
		
		ParseStatus status = new ParseStatus(page.getBaseUrl());
		
		mainUrl.set(page.getBaseUrl());
		ajax.set(page.isAjax());
		String md5 = Md5Signatrue.generateMd5(mainUrl.get());
		if (duplicateInspector.md5Exist(md5)){
			status.setStatus(Status.NOT_CHANGE);
			return status;
		} else {
			duplicateInspector.addMd5(md5);
		}
		
		threadLocalDetailConf.set(confDao.getDetailConf(Utils.getHost(mainUrl.get())));
        if (threadLocalDetailConf == null || threadLocalDetailConf.get() == null) {
            LOG.error("No detail page configuration in database: " + mainUrl.get());
            status.setStatus(Status.PARSE_FAILURE);
            status.setMessage("No detail page configuration in database");
            return status;
        }
        fetchContent(page);
        int num = indexWriter.write(threadLocalRecordInfos.get());
        LOG.debug(mainUrl.get() + " has record number: " + num);
        status.setStatus(Status.SUCCESS);
		status.setCount(num);
        return status;
    }

    public void fetchContent(WebPage page) /*throws ConnectException*/ {
    	RecordInfo info = new RecordInfo(page.getTitle(), page.getBaseUrl(), page.getFetchTime());
    	
    	ProtocolOutput po = fetch(page.getBaseUrl(), ajax.get());
    	if (po == null || !po.getStatus().isSuccess()) {
    		return ;
    	}
    	
        Document document = po.getDocument();
        
        if (document == null) return ;
        
        Elements contentEles = document.select(threadLocalDetailConf.get().getContent());
        if (!CollectionUtils.isEmpty(contentEles)) {
            Element contentEle = contentEles.first();
            info.setContent(contentEle.text());
            info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, rule.get()));
            info.setVoice_url(MultimediaExtractor.extractAudioUrl(contentEle));
            info.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
        }

        String authorDom = threadLocalDetailConf.get().getAuthor();
        if (!StringUtils.isEmpty(authorDom)
                && !CollectionUtils.isEmpty(document.select(authorDom))) {
        	info.setNickname(document.select(authorDom).first().text());
        }

        String sourcesDom = threadLocalDetailConf.get().getSources();
        if (!StringUtils.isEmpty(sourcesDom)
                && !CollectionUtils.isEmpty(document.select(sourcesDom))) {
        	info.setSource_name(document.select(sourcesDom).first().text());
        }

        String replyNumDom = threadLocalDetailConf.get().getReplyNum();
        if (!StringUtils.isEmpty(replyNumDom)
                && !CollectionUtils.isEmpty(document.select(replyNumDom))) {
            String replyNum = document.select(replyNumDom).first().text();
            info.setComment_count(Integer.valueOf(replyNum));
        }
        
        String forwardNumDom = threadLocalDetailConf.get().getForwardNum();
        if (!StringUtils.isEmpty(forwardNumDom)
                && !CollectionUtils.isEmpty(document.select(forwardNumDom))) {
            String forwardNum = document.select(forwardNumDom).first().text();
            info.setRepost_count(Integer.valueOf(forwardNum));
        }
        
        String reviewNumDom = threadLocalDetailConf.get().getReviewNum();
        if (!StringUtils.isEmpty(reviewNumDom)
                && !CollectionUtils.isEmpty(document.select(reviewNumDom))) {
            String reviewNum = document.select(reviewNumDom).first().text();
            info.setRead_count(Integer.valueOf(reviewNum));
        }
        
        threadLocalRecordInfos.get().add(info);

    }

}