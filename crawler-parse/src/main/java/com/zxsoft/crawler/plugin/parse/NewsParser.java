package com.zxsoft.crawler.plugin.parse;

import java.net.ConnectException;
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
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.storage.NewsDetailConf;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.store.OutputException;
import com.zxsoft.crawler.util.Md5Signatrue;
import com.zxsoft.crawler.util.Utils;

public class NewsParser extends Parser {

    private static Logger LOG = LoggerFactory.getLogger(NewsParser.class);
 
//	private String rule; // filter regular expression
//    private String mainUrl;
//    private boolean ajax = false;
    
	private ThreadLocal<String> mainUrl = new ThreadLocal<String>();
	private ThreadLocal<String> rule = new ThreadLocal<String>() { protected String initialValue() { return "";}};
	private ThreadLocal<Long> prevFetchTime = new ThreadLocal<Long>();
	private ThreadLocal<Boolean> ajax = new ThreadLocal<Boolean>();
	private ThreadLocal<List<RecordInfo>> recordInfos = new ThreadLocal<List<RecordInfo>>() {
		protected List<RecordInfo> initialValue() {
			return new LinkedList<RecordInfo>();
		}
	};
    
    
    
    public ParseStatus parse(WebPage page) throws Exception {
    	Assert.notNull(page, "Page is null");
		Document document = page.getDocument();
		Assert.notNull(document, "Document is null");
		
		mainUrl.set(page.getBaseUrl());
		ajax.set(page.isAjax());
		String md5 = Md5Signatrue.generateMd5(mainUrl.get());
		if (duplicateInspector.md5Exist(md5)) return null;
		
		
        NewsDetailConf detailConf = confDao.getNewsDetailConf(Utils.getHost(mainUrl.get()));
        if (detailConf == null) {
            LOG.error("No detail page configuration in database: " + mainUrl);
            return null;
        }
        fetchContent(page, detailConf);
        return null;
    }

    private boolean fetchContent(WebPage page, NewsDetailConf detailConf) throws ConnectException {
    	RecordInfo info = new RecordInfo(page.getTitle(), page.getBaseUrl(), page.getFetchTime());
    	
    	ProtocolOutput po = fetch(page.getBaseUrl(), ajax.get());
    	if (po == null || !po.getStatus().isSuccess()) {
    		return false;
    	}
    	
        Document document = po.getDocument();
        
        if (document == null) return false;
        
        Elements contentEles = document.select(detailConf.getContent());
        if (!CollectionUtils.isEmpty(contentEles)) {
            Element contentEle = contentEles.first();
            info.setContent(contentEle.text());
            info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, rule.get()));
            info.setVoice_url(MultimediaExtractor.extractAudioUrl(contentEle));
            info.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
        }

        if (!StringUtils.isEmpty(detailConf.getAuthor())
                && !CollectionUtils.isEmpty(document.select(detailConf.getAuthor()))) {
        	info.setNickname(document.select(detailConf.getAuthor()).first().text());
        }

        if (!StringUtils.isEmpty(detailConf.getSources())
                && !CollectionUtils.isEmpty(document.select(detailConf.getSources()))) {
        	info.setSource_name(document.select(detailConf.getSources()).first().text());
        }

        if (!StringUtils.isEmpty(detailConf.getReplyNum())
                && !CollectionUtils.isEmpty(document.select(detailConf.getReplyNum()))) {
            String replyNum = document.select(detailConf.getReplyNum()).first().text();
            info.setComment_count(Integer.valueOf(replyNum));
        }
        if (!StringUtils.isEmpty(detailConf.getForwardNum())
                && !CollectionUtils.isEmpty(document.select(detailConf.getForwardNum()))) {
            String forwardNum = document.select(detailConf.getForwardNum()).first().text();
            info.setRepost_count(Integer.valueOf(forwardNum));
        }
        if (!StringUtils.isEmpty(detailConf.getReviewNum())
                && !CollectionUtils.isEmpty(document.select(detailConf.getReviewNum()))) {
            String reviewNum = document.select(detailConf.getReviewNum()).first().text();
            info.setRead_count(Integer.valueOf(reviewNum));
        }

        try {
	        indexWriter.write(info);
        } catch (OutputException e) {
	        e.printStackTrace();
        }
        
        return true;
    }

}