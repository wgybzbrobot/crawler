package com.zxsoft.crawler.plugin.parse;

import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.DetailRule;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.parse.ExtInfo;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.FetchStatus.Status;
import com.zxsoft.crawler.parse.ext.ExtExtractor;
import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.parse.Platform;
import com.zxsoft.crawler.plugin.parse.ext.DateExtractor;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.util.Md5Signatrue;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;

public class NewsParser extends Parser {


    private static Logger LOG = LoggerFactory.getLogger(NewsParser.class);

    public NewsParser(RecordInfo recordInfo, DetailRule detailRule,
                    Long prevFetchTime, ExtInfo extInfo) {
        super(recordInfo, detailRule, prevFetchTime, extInfo);
    }

	 private List<RecordInfo> recordInfos = new LinkedList<RecordInfo>();

	@Override
	public FetchStatus parse() throws CrawlerException  {

		 if (detailRule == null)
	            throw new CrawlerException(ErrorCode.CONF_ERROR,
	                            "Detail page rule is null.");
		
	        String mainUrl = recordInfo.getOriginal_url();
	        WebPage page = new WebPage(mainUrl, detailRule.getAjax(), null);
	        
		ProtocolOutput _output = fetch(page);
		if (!_output.getStatus().isSuccess()) {
		        return new FetchStatus(mainUrl, 61, Status.PROTOCOL_FAILURE);
		}
		
		Document document = _output.getDocument();
		page.setDocument(document);

		
		RecordInfo info = recordInfo.clone();
		info.setUrl(mainUrl);
		info.setPlatform(Platform.PLATFORM_NEWS);

		Elements contentEles = null;
		if (!StringUtils.isEmpty(detailRule.getContent()) && !CollectionUtils.isEmpty(contentEles = document.select(detailRule.getContent()))) {
			Element contentEle = contentEles.first();
			info.setContent(contentEle.text());
			info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, ""));
			info.setVoice_url(MultimediaExtractor.extractAudioUrl(contentEle));
			info.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
		}
		String authorDom = detailRule.getAuthor();
		if (!StringUtils.isEmpty(authorDom) && !CollectionUtils.isEmpty(document.select(authorDom))) {
		        String text = document.select(authorDom).first().text();
			info.setNickname(ExtExtractor.extractAuthor(text));
		}
		String sourcesDom = detailRule.getSources();
		if (!StringUtils.isEmpty(sourcesDom) && !CollectionUtils.isEmpty(document.select(sourcesDom)))  {
		        String text = document.select(sourcesDom).first().text();
	                info.setSource_name(ExtExtractor.extractSource(text));
		}
		String replyNumDom = detailRule.getReplyNum();
		if (!StringUtils.isEmpty(replyNumDom) && !CollectionUtils.isEmpty(document.select(replyNumDom)))
//			info.setComment_count(Integer.valueOf(document.select(replyNumDom).first().text()));
			info.setComment_count(ExtExtractor.extractReplyNum(document.select(replyNumDom).first().text()));
		        
		String forwardNumDom = detailRule.getForwardNum();
		if (!StringUtils.isEmpty(forwardNumDom) && !CollectionUtils.isEmpty(document.select(forwardNumDom))) {
			String forwardNum = document.select(forwardNumDom).first().text();
			if (!StringUtils.isEmpty(forwardNum))
			info.setRepost_count(Integer.valueOf(forwardNum));
		}
		String reviewNumDom = detailRule.getReviewNum();
		if (!StringUtils.isEmpty(reviewNumDom) && !CollectionUtils.isEmpty(document.select(reviewNumDom))) {
			String reviewNum = document.select(reviewNumDom).first().text();
			if (!StringUtils.isEmpty(reviewNum))
			        info.setRead_count(ExtExtractor.extractReadNum(reviewNum));
		}
		String dateDom = detailRule.getDate();
		if (!StringUtils.isEmpty(dateDom) && !CollectionUtils.isEmpty(document.select(dateDom))) {
		        String str = document.select(dateDom).first().html();
	                info.setTimestamp(DateExtractor.extractInMilliSecs(str));
		}
		info.setId(Md5Signatrue.generateMd5(info.getUrl()));
		recordInfos.add(info);

		int count = recordInfos.size();
		        
		indexWriter.write(recordInfos);

		return new FetchStatus(mainUrl, 21, Status.SUCCESS, count);
	}

}