package com.zxsoft.crawler.plugin.parse;

import java.net.ConnectException;
import java.util.Date;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.zxsoft.crawler.dao.ConfDao;
import com.zxsoft.crawler.duplicate.DuplicateInspector;
import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.ParseStatus;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.storage.News;
import com.zxsoft.crawler.storage.NewsDetailConf;
import com.zxsoft.crawler.storage.Seed;
import com.zxsoft.crawler.storage.WebPageMy;
import com.zxsoft.crawler.util.Md5Signatrue;

public class NewsParser extends Parser {

    private static Logger LOG = LoggerFactory.getLogger(NewsParser.class);
 
	private DuplicateInspector duplicateInspector;
	private ConfDao confDao;
    
    public ParseStatus parse(WebPageMy page) throws Exception {
    	Assert.notNull(page, "Page is null");
		Document document = page.getDocument();
		Assert.notNull(document, "Document is null");
		Seed seed = page.getSeed();
		Assert.notNull(seed, "Seed is null");
		
		String md5 = Md5Signatrue.generateMd5(seed.getUrl());
		if (duplicateInspector.md5Exist(md5)) return null;
		else tools.getRedisSerivice().addJudgeMd5(md5);
		
        ListConf listConf = page.getListConf();
        page.setPage(listConf.getPage());
        NewsDetailConf detailConf = tools.getDomService().getNewsDetailConf(page.getHost());
        if (detailConf == null) {
            LOG.error("No detail page configuration in database: " + page.getHost());
            return null;
        }
        fetchContent(page, detailConf);
        return null;
    }

    private boolean fetchContent(WebPageMy page, NewsDetailConf detailConf) throws ConnectException {
    	Seed seed = page.getSeed();
    	News news = new News(seed.getTitle(), seed.getUrl(), seed.getReleasedate(), new Date());
    	
    	JsoupLoader loader = new JsoupLoader();
    	loader.setTools(tools);
        Document document = loader.load(seed);
        
        if (document == null) return false;
        
        Elements contentEles = document.select(detailConf.getContent());
        if (!CollectionUtils.isEmpty(contentEles)) {
            Element contentEle = contentEles.first();
            news.setContent(contentEle.text());
            news.setImg(MultimediaExtractor.extractImgUrl(contentEle, page.getListConf().getFilterurl()));
            news.setAudio(MultimediaExtractor.extractAudioUrl(contentEle));
            news.setVideo(MultimediaExtractor.extractVideoUrl(contentEle));
        }

        if (!StringUtils.isEmpty(detailConf.getAuthor())
                && !CollectionUtils.isEmpty(document.select(detailConf.getAuthor()))) {
            news.setAuthor(document.select(detailConf.getAuthor()).first().text());
        }

        if (!StringUtils.isEmpty(detailConf.getSources())
                && !CollectionUtils.isEmpty(document.select(detailConf.getSources()))) {
            news.setSources(document.select(detailConf.getSources()).first().text());
        }

        if (!StringUtils.isEmpty(detailConf.getReplyNum())
                && !CollectionUtils.isEmpty(document.select(detailConf.getReplyNum()))) {
            String replyNum = document.select(detailConf.getReplyNum()).first().text();
            news.setReplyNum(Integer.valueOf(replyNum));
        }
        if (!StringUtils.isEmpty(detailConf.getForwardNum())
                && !CollectionUtils.isEmpty(document.select(detailConf.getForwardNum()))) {
            String forwardNum = document.select(detailConf.getForwardNum()).first().text();
            news.setForwardNum(Integer.valueOf(forwardNum));
        }
        if (!StringUtils.isEmpty(detailConf.getReviewNum())
                && !CollectionUtils.isEmpty(document.select(detailConf.getReviewNum()))) {
            String reviewNum = document.select(detailConf.getReviewNum()).first().text();
            news.setReviewNum(Integer.valueOf(reviewNum));
        }

        return tools.getInfoService().addNews(news);
    }

}