package com.zxsoft.crawler.plugin.parse;

import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.Assert;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.FetchStatus.Status;
import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.parse.Platform;
import com.zxsoft.crawler.plugin.parse.ext.DateExtractor;
import com.zxsoft.crawler.plugin.parse.ext.ExtExtractor;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.util.Md5Signatrue;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.store.OutputException;
import com.zxsoft.crawler.util.Utils;

public class NewsParser extends Parser {

	private static Logger LOG = LoggerFactory.getLogger(NewsParser.class);

	private ThreadLocal<Boolean> ajax = new ThreadLocal<Boolean>();
	private ThreadLocal<List<RecordInfo>> threadLocalRecordInfos = new ThreadLocal<List<RecordInfo>>() {
		protected List<RecordInfo> initialValue() {
			return new LinkedList<RecordInfo>();
		}
	};

	@Override
	public FetchStatus parse(WebPage page) throws Exception {
		Assert.notNull(page, "Page is null");
		String mainUrl = page.getBaseUrl();

		DetailConf detailConf = confDao.getDetailConf(page.getListUrl(), Utils.getHost(mainUrl));
		 if (detailConf == null) {
                        return new FetchStatus(mainUrl, 41, Status.CONF_ERROR);
                }
		
		page.setAjax(detailConf.isAjax());
		ProtocolOutput _output = fetch(page);
		if (!_output.getStatus().isSuccess()) {
		        return new FetchStatus(mainUrl, 61, Status.PROTOCOL_FAILURE);
		}
		
		Document document = _output.getDocument();
		page.setDocument(document);

		ajax.set(page.isAjax());

		if (detailConf == null) {
		        LOG.error("DetailConf not found: " + mainUrl);
			return new FetchStatus(mainUrl, 41, Status.CONF_ERROR);
		}
		
		RecordInfo info = new RecordInfo(mainUrl, comment,Platform.PLATFORM_NEWS, ip, country_code, province_code, city_code, location_code, location, source_id, server_id, source_type);
		info.setTitle(page.getTitle());
		Elements contentEles = null;
		if (!StringUtils.isEmpty(detailConf.getContent()) && !CollectionUtils.isEmpty(contentEles = document.select(detailConf.getContent()))) {
			Element contentEle = contentEles.first();
			info.setContent(contentEle.text());
			info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, ""));
			info.setVoice_url(MultimediaExtractor.extractAudioUrl(contentEle));
			info.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
		}
		String authorDom = detailConf.getAuthor();
		if (!StringUtils.isEmpty(authorDom) && !CollectionUtils.isEmpty(document.select(authorDom))) {
		        String text = document.select(authorDom).first().text();
			info.setNickname(ExtExtractor.extractAuthor(text));
		}
		String sourcesDom = detailConf.getSources();
		if (!StringUtils.isEmpty(sourcesDom) && !CollectionUtils.isEmpty(document.select(sourcesDom)))  {
		        String text = document.select(sourcesDom).first().text();
	                info.setSource_name(ExtExtractor.extractSource(text));
		}
		String replyNumDom = detailConf.getReplyNum();
		if (!StringUtils.isEmpty(replyNumDom) && !CollectionUtils.isEmpty(document.select(replyNumDom)))
//			info.setComment_count(Integer.valueOf(document.select(replyNumDom).first().text()));
			info.setComment_count(ExtExtractor.extractReplyNum(document.select(replyNumDom).first().text()));
		        
		String forwardNumDom = detailConf.getForwardNum();
		if (!StringUtils.isEmpty(forwardNumDom) && !CollectionUtils.isEmpty(document.select(forwardNumDom))) {
			String forwardNum = document.select(forwardNumDom).first().text();
			if (!StringUtils.isEmpty(forwardNum))
			info.setRepost_count(Integer.valueOf(forwardNum));
		}
		String reviewNumDom = detailConf.getReviewNum();
		if (!StringUtils.isEmpty(reviewNumDom) && !CollectionUtils.isEmpty(document.select(reviewNumDom))) {
			String reviewNum = document.select(reviewNumDom).first().text();
			if (!StringUtils.isEmpty(reviewNum))
			        info.setRead_count(ExtExtractor.extractReadNum(reviewNum));
		}
		String dateDom = detailConf.getDate();
		if (!StringUtils.isEmpty(dateDom) && !CollectionUtils.isEmpty(document.select(dateDom))) {
		        String str = document.select(dateDom).first().html();
	                info.setTimestamp(DateExtractor.extractInMilliSecs(str));
		}
		info.setId(Md5Signatrue.generateMd5(info.getUrl()));
		threadLocalRecordInfos.get().add(info);

		int count = threadLocalRecordInfos.get().size();
		try {
		        
			indexWriter.write(threadLocalRecordInfos.get());
		} catch (OutputException e) {
			throw new OutputException(mainUrl + " output data failed.");
		}

		return new FetchStatus(mainUrl, 21, Status.SUCCESS, count);
	}

}