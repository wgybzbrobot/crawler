package com.zxsoft.crawler.plugin.parse;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector.SelectorParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.StringUtils;

import com.zxsoft.crawler.dns.DNSCache;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.FetchStatus.Status;
import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
import com.zxsoft.crawler.protocol.util.Md5Signatrue;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.store.OutputException;
import com.zxsoft.crawler.util.Utils;

/**
 * <ol>
 * <li>1. 解析主帖</li>
 * <li>2. 若从最后一页抓取，则调转到最后一页</li>
 * <li>3. 解析当前页</li>
 * <li>4. 翻页</li>
 * </ol>
 */
public class ForumParser extends Parser {

	private static Logger LOG = LoggerFactory.getLogger(ForumParser.class);

	private List<RecordInfo> recordInfos = new LinkedList<RecordInfo>();

	public List<RecordInfo> getRecordInfos() {
		return recordInfos;
	}
	private String ip;

	/**
	 * give a thread page url, get the page html code, parse the main thread,
	 * parse reply thread, parse subre
	 */
	public FetchStatus parse(WebPage page) throws Exception {
		Assert.notNull(page, "Page is null");
		String mainUrl = page.getBaseUrl();
		long prevFetchTime = page.getPrevFetchTime();
		DetailConf detailConf = confDao.getDetailConf(page.getListUrl(), Utils.getHost(mainUrl));
		if (detailConf == null)
			return new FetchStatus(mainUrl, 41, Status.CONF_ERROR);

		ProtocolOutput _output = fetch(page);
		if (!_output.getStatus().isSuccess()) {
			return new FetchStatus(mainUrl, 51, Status.PROTOCOL_FAILURE);
		}
		
		Document mainDoc = _output.getDocument();
		page.setDocument(mainDoc);
		ip = DNSCache.getIp(new URL(mainUrl));
		
		/*
		 * Parse Main-Thread
		 */
		RecordInfo info = new RecordInfo(page.getTitle(), mainUrl, System.currentTimeMillis());
		info.setIp(ip);
		String replyNumDom = detailConf.getReplyNum();
		if (!StringUtils.isEmpty(replyNumDom) && !CollectionUtils.isEmpty(mainDoc.select(replyNumDom)))
			info.setComment_count(Utils.extractNum(mainDoc.select(replyNumDom).first().text()));
		String reviewNumDom = detailConf.getReviewNum();
		if (!StringUtils.isEmpty(reviewNumDom) && !CollectionUtils.isEmpty(mainDoc.select(reviewNumDom)))
			info.setRead_count(Integer.valueOf(Utils.extractNum(mainDoc.select(reviewNumDom).first().text())));

		Elements masterEles = mainDoc.select(detailConf.getMaster());
		if (!CollectionUtils.isEmpty(masterEles)) {
			Element masterEle = masterEles.first();
			String authorDom = detailConf.getAuthor();
			if (!StringUtils.isEmpty(authorDom) && !CollectionUtils.isEmpty(masterEle.select(authorDom)))
				info.setNickname(masterEle.select(authorDom).first().text());
			Elements contentEles = masterEle.select(detailConf.getContent());
			if (!CollectionUtils.isEmpty(contentEles)) {
				Element contentEle = contentEles.first();
				info.setContent(contentEle.text());
				info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, ""));
				info.setVoice_url(MultimediaExtractor.extractAudioUrl(contentEle));
				info.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
			}
			if (info.getTimestamp() == 0) {
				String dateDom = detailConf.getDate();
				if (!StringUtils.isEmpty(dateDom) && !CollectionUtils.isEmpty(masterEle.select(dateDom))) {
					String dateField = masterEle.select(dateDom).first().text();
					if (!StringUtils.isEmpty(dateField)) {
						try {
							info.setTimestamp(Utils.formatDate(dateField).getTime());
						} catch (java.text.ParseException | NullPointerException e) {
							LOG.error("Cannot parse date: " + dateField);
						}
					}
				}
			}
			info.setId(Md5Signatrue.generateMd5(info.getNickname(), info.getContent(), info.getPic_url(), info.getVoice_url(),
			        info.getVideo_url()));
			getRecordInfos().add(info);
		} else {
			return new FetchStatus(mainUrl, 42, Status.CONF_ERROR);
		}
		
		/*
		 * Parse reply-thread
		 */
		Document _doc = mainDoc;
		String currentUrl = mainUrl, newPageUrl = ""/* , currentPageText = "" */;
		_output = null;
		if (!detailConf.isFetchorder()) { // 从第一页
			int pageNum = 1;
			do {
				boolean isContinue = parsePage(prevFetchTime, _doc, mainUrl, currentUrl, detailConf);
				if (!isContinue) {
					break;
				}
				pageNum++;
				WebPage np = page.clone();
				np.setDocument(_doc);
				np.setBaseUrl(_doc.location());
				_output = fetchNextPage(pageNum, np);
				if (_output == null || _output.getStatus().getCode() != STATUS_CODE.SUCCESS)
					break;
				_doc = _output.getDocument();
				currentUrl = newPageUrl;
			} while (!StringUtils.isEmpty(newPageUrl));

		} else { // parse from last page
			WebPage np = page.clone(); // jump to last page
			page.setDocument(_doc);
			page.setBaseUrl(_doc.location());
			_output = fetchLastPage(np);
			Document lastDoc = null;
			if (_output == null || _output.getStatus().getCode() != STATUS_CODE.SUCCESS || (lastDoc = _output.getDocument()) == null) {
				parsePage(prevFetchTime, _doc, mainUrl, currentUrl, detailConf);
			} else {
				_doc = lastDoc;
				while (true) {
					if (_doc == null || StringUtils.isEmpty(currentUrl = _doc.location()))
						break;
					boolean isContinue = parsePage(prevFetchTime, _doc, mainUrl, currentUrl, detailConf);
					if (!isContinue) {
						break;
					}
					// 获取上一页
					np.setDocument(_doc);
					np.setBaseUrl(_doc.location());
					_output = fetchPrevPage(-1, np);
					if (_output == null || _output.getStatus().getCode() != STATUS_CODE.SUCCESS)
						break;
					_doc = _output.getDocument();
				}
			}
		}

		int count = 0;
		try {
			count = indexWriter.write(getRecordInfos());
		} catch (OutputException e) {
			throw new OutputException(mainUrl + "data output failure");
//			return new FetchStatus(mainUrl, 61, Status.OUTPUT_FAILURE);
		}
		return new FetchStatus(mainUrl, 21, Status.SUCCESS, count);
	}

	/**
	 * 解析一页
	 */
	private boolean parsePage(long prevFetchTime, Document doc, String mainUrl, String currentUrl, DetailConf detailConf)
	        throws SelectorParseException {
		Elements replyEles = doc.select(detailConf.getReply());
		for (Element element : replyEles) {
			RecordInfo reply = new RecordInfo();
			reply.setIp(ip);
			reply.setOriginal_url(mainUrl);
			reply.setUrl(currentUrl);
			String id = save(reply, element, null, detailConf); // 保存回复
			if (reply.getTimestamp() != 0 && reply.getTimestamp() < prevFetchTime)
				return false;
			if (id == null)
				continue;
			// 解析子回复
			String subReplyDom = detailConf.getSubReply();
			if (StringUtils.isEmpty(subReplyDom))
				continue;
			Elements subReplyEles = element.select(subReplyDom);
			String parentId = id;
			if (!CollectionUtils.isEmpty(subReplyEles)) {
				for (Element ele : subReplyEles) {
					RecordInfo subReply = new RecordInfo();
					subReply.setIp(ip);
					subReply.setOriginal_url(mainUrl);
					subReply.setUrl(currentUrl);
					saveSub(subReply, ele, parentId, detailConf);
				}
			}
		}
		return true;
	}

	/**
	 * 保存回复
	 */
	private String save(RecordInfo reply, Element element, String parentId, DetailConf detailConf)
	        throws SelectorParseException {
		String replyAuthorDom = detailConf.getReplyAuthor();
		if (!StringUtils.isEmpty(replyAuthorDom) && !CollectionUtils.isEmpty(element.select(replyAuthorDom))) {
			reply.setNickname(element.select(replyAuthorDom).first().text());
		}
		Elements contentEles = element.select(detailConf.getReplyContent());
		if (!CollectionUtils.isEmpty(contentEles)) {
			Element contentEle = contentEles.first();
			reply.setContent(contentEle.text());
			reply.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, ""));
			reply.setVoice_url(MultimediaExtractor.extractAudioUrl(contentEle));
			reply.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
		} else {
			return null;
		}
		String replyDateDom = detailConf.getReplyDate();
		if (!StringUtils.isEmpty(replyDateDom) && !CollectionUtils.isEmpty(element.select(replyDateDom))) {
			String dateField = element.select(replyDateDom).first().text();
			try {
				reply.setTimestamp(Utils.formatDate(dateField).getTime());
			} catch (java.text.ParseException | NullPointerException e) {
				LOG.error("Cannot parse date: " + dateField + " in page " + reply.getUrl());
			}
		}
		reply.setOriginal_id(parentId);
		String id = Md5Signatrue.generateMd5(reply.getNickname(), reply.getContent(), reply.getPic_url(), reply.getVoice_url(),
		        reply.getVideo_url());
		reply.setId(id);
		getRecordInfos().add(reply);
		return id;
	}

	/**
	 * 保存子回复
	 */
	private String saveSub(RecordInfo reply, Element element, String parentId, DetailConf detailConf) throws SelectorParseException {
		String subReplyAuthorDom = detailConf.getSubReplyAuthor();
		if (!StringUtils.isEmpty(subReplyAuthorDom) && !CollectionUtils.isEmpty(element.select(subReplyAuthorDom))) {
			reply.setNickname(element.select(subReplyAuthorDom).first().text());
		}
		String subReplyContentDom = detailConf.getSubReplyContent();
		if (!StringUtils.isEmpty(subReplyContentDom) && !CollectionUtils.isEmpty(element.select(subReplyContentDom))) {
			reply.setContent(element.select(subReplyContentDom).first().text());
			Elements imgs = element.select(subReplyContentDom).select("img");
			StringBuilder imgUrlSb = new StringBuilder();
			for (Element img : imgs) {
				imgUrlSb.append(img.attr("abs:src"));// 多个url用空格隔开
			}
			reply.setPic_url(imgUrlSb.toString());
		}
		reply.setVoice_url("");
		reply.setVideo_url("");
		String subReplyDate = detailConf.getSubReplyDate();
		if (!CollectionUtils.isEmpty(element.select(subReplyDate))) {
			String dateField = element.select(subReplyDate).first().text();
			try {
				reply.setTimestamp(Utils.formatDate(dateField).getTime());
			} catch (java.text.ParseException e) {
				LOG.error("Cannot parse date: " + dateField + " in page " + reply.getUrl());
			}
		}
		reply.setOriginal_id(parentId);
		String id = Md5Signatrue.generateMd5(reply.getNickname(), reply.getContent(), reply.getPic_url(), reply.getVoice_url(),
		        reply.getVideo_url());
		reply.setId(id);
		getRecordInfos().add(reply);
		return id;
	}

}
