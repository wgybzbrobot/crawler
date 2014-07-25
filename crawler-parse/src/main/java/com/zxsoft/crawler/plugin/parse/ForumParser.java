package com.zxsoft.crawler.plugin.parse;

import java.net.ConnectException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.zxsoft.crawler.dao.ConfDao;
import com.zxsoft.crawler.duplicate.DuplicateInspector;
import com.zxsoft.crawler.parse.Category;
import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.ParseException;
import com.zxsoft.crawler.parse.ParseStatus;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatusCodes;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClientPageHelper;
import com.zxsoft.crawler.storage.Forum;
import com.zxsoft.crawler.storage.ForumDetailConf;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.Reply;
import com.zxsoft.crawler.storage.Seed;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.store.Output;
import com.zxsoft.crawler.util.Md5Signatrue;
import com.zxsoft.crawler.util.Utils;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

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
//	private String rule; // filter regular expression
//	private String mainUrl;
//	private boolean ajax;
//	private List<RecordInfo> recordInfos = new LinkedList<RecordInfo>();
	
	private ThreadLocal<String> mainUrl = new ThreadLocal<String>();
	private ThreadLocal<String> rule = new ThreadLocal<String>() { protected String initialValue() { return "";}};
	private ThreadLocal<Long> prevFetchTime = new ThreadLocal<Long>();
	private ThreadLocal<Boolean> ajax = new ThreadLocal<Boolean>();
	private ThreadLocal<List<RecordInfo>> recordInfos = new ThreadLocal<List<RecordInfo>>() {
		protected List<RecordInfo> initialValue() {
			return new LinkedList<RecordInfo>();
		}
	};
	
	@Override
	public ParseStatus parse(WebPage page) throws Exception {
		Assert.notNull(page, "Page is null");
		Document document = page.getDocument();
		Assert.notNull(document, "Document is null");

//		ajax = page.isAjax();
//		mainUrl = page.getBaseUrl();
		
		mainUrl.set(page.getBaseUrl());
		prevFetchTime.set(page.getPrevFetchTime());
		ajax.set(page.isAjax());
		recordInfos.set(new LinkedList<RecordInfo>());
		
		String md5 = Md5Signatrue.generateMd5(mainUrl.get());
		if (duplicateInspector.md5Exist(md5)) return null;
		
		
		ParseStatus status = new ParseStatus(mainUrl.get());
		ForumDetailConf detailConf = confDao.getForumDetailConf(Utils.getHost(mainUrl.get()));

		if (detailConf == null) {
			LOG.warn("Detail page has no configuration in database." + mainUrl);
			status.setMessage("detail page has no configuration in database.");
			return status;
		}

		/*if (seed.getType() == Category.DETAIL_PAGE) { // 新的详细页 或 丢失的详细页
			fetchContent(page, detailConf);
		} else if (seed.getType() == Category.REPLY_PAGE) { // 丢失的回复页
			parsePage(page, document, seed.getMainUrl(), seed.getUrl(), detailConf);
		} else {
			LOG.error("Seed error occur.");
		}*/
		fetchContent(page, detailConf);
		
		int num = indexWriter.write(recordInfos.get());
		LOG.info(mainUrl.get() + " has " + num + " records.");
		return status;
	}

	private void fetchContent(WebPage page, ForumDetailConf detailConf) throws ParseException, ConnectException {
		
		RecordInfo info = new RecordInfo(page.getTitle(), mainUrl.get(), page.getFetchTime());
		
		Document document = page.getDocument();
		// Elements titleEle = document.select(detailConf.getTitle()); //
		if (!CollectionUtils.isEmpty(document.select(detailConf.getReplyNum()))) {
			info.setComment_count(Utils.extractNum(document.select(detailConf.getReplyNum()).first().text()));
		}
		if (!StringUtils.isEmpty(detailConf.getReviewNum())
		        && !CollectionUtils.isEmpty(document.select(detailConf.getReviewNum()))) {
			info.setRead_count(Integer.valueOf(Utils.extractNum(document.select(detailConf.getReviewNum()).first().text())));
		}
		Elements mainEles = document.select(detailConf.getMaster());
		// 主帖页面, 取得主帖信息
		if (!CollectionUtils.isEmpty(mainEles)) {
			Element mainEle = mainEles.first();
			if (!StringUtils.isEmpty(detailConf.getMasterAuthor())
			        && !CollectionUtils.isEmpty(mainEle.select(detailConf.getMasterAuthor())))
				info.setNickname(mainEle.select(detailConf.getMasterAuthor()).first().text());

			Elements contentEles = mainEle.select(detailConf.getMasterContent());
			if (!CollectionUtils.isEmpty(contentEles)) {
				Element contentEle = contentEles.first();
				info.setContent(contentEle.text());
				info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, rule.get()));
				info.setVoice_url(MultimediaExtractor.extractAudioUrl(contentEle));
				info.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
			}

			if (info.getTimestamp() == 0) {
				if (!StringUtils.isEmpty(detailConf.getMasterDate())
				        && !CollectionUtils.isEmpty(mainEle.select(detailConf.getMasterDate()))) {
					String dateField = mainEle.select(detailConf.getMasterDate()).first().text();
					if (!StringUtils.isEmpty(dateField)) {
						try {
	                        info.setTimestamp(Utils.formatDate(dateField).getTime());
                        } catch (java.text.ParseException e) {
                        	LOG.error("Cannot parse date: " + dateField + " in page " + mainUrl.get());
                        }
					}
				}
			}

			recordInfos.get().add(info);

			parseReply(page, detailConf);
		} else {
			LOG.warn("主帖信息配置有误:" + mainUrl);
			return;
		}
	}

	/**
	 * 解析回复
	 */
	private void parseReply(WebPage page, ForumDetailConf detailConf) {
		Document doc = page.getDocument();
		String currentUrl = mainUrl.get();
		String newPageUrl = "", currentPageText = "";
		ProtocolOutput ptemp = null;
		if (!detailConf.isFetchorder()) { // 从第一页
			int pageNum = 1;
			do {
				parsePage(page, doc, mainUrl.get(), currentUrl, detailConf);
				pageNum++;
				ptemp = fetchNextPage(pageNum, doc, ajax.get());
				if (ptemp == null || ptemp.getStatus().getCode() != ProtocolStatusCodes.SUCCESS)
					break;
				doc = ptemp.getDocument();
				currentUrl = newPageUrl;
			} while (!StringUtils.isEmpty(newPageUrl));

		} else { // fetch from last page
			// jump to last page
			ptemp = fetchLastPage(doc, ajax.get());
			Document lastDoc = null;
			if (ptemp == null || ptemp.getStatus().getCode() != ProtocolStatusCodes.SUCCESS || (lastDoc = ptemp.getDocument()) == null ) {
				parsePage(page, doc, mainUrl.get(), currentUrl, detailConf);
			} else {
				doc = lastDoc;
				while (true) {
					if (doc == null || StringUtils.isEmpty(currentUrl = doc.location()))
						break;
//					LOG.info(currentUrl);
					parsePage(page, doc, mainUrl.get(), currentUrl, detailConf);
					// 获取上一页
					ptemp = fetchPrevPage(-1, doc, ajax.get());
					if (ptemp == null || ptemp.getStatus().getCode() != ProtocolStatusCodes.SUCCESS)
						break;
					doc = ptemp.getDocument();
				}
			}
		}
	}

	/**
	 * 解析一页
	 */
	private void parsePage(WebPage page, Document doc, String mainUrl, String currentUrl, ForumDetailConf detailConf) {
		Elements replyEles = doc.select(detailConf.getReply());
		for (Element element : replyEles) {
			RecordInfo reply = new RecordInfo();
			reply.setOriginal_url(mainUrl);
			reply.setUrl(currentUrl);
			String id = save(page, reply, element, detailConf, null); // 保存回复

			if (id == null) continue;
			// 解析子回复
			String subReplyDom = detailConf.getSubReply();
			if (StringUtils.isEmpty(subReplyDom))
				continue;
			Elements subReplyEles = element.select(subReplyDom);

			String parentId = id;
			if (!CollectionUtils.isEmpty(subReplyEles)) {
				for (Element ele : subReplyEles) {
					RecordInfo subReply = new RecordInfo();
					subReply.setOriginal_url(mainUrl);
					subReply.setUrl(currentUrl);
					saveSub(subReply, ele, detailConf, parentId);
				}
			}
		}
	}

	/**
	 * 保存回复
	 */
	private String save(WebPage page, RecordInfo reply, Element element, ForumDetailConf detailConf, String parentId) {
		String id = UUID.randomUUID().toString();
		reply.setId(id);
		if (!CollectionUtils.isEmpty(element.select(detailConf.getReplyAuthor()))) {
			reply.setNickname(element.select(detailConf.getReplyAuthor()).first().text());
		}

		Elements contentEles = element.select(detailConf.getReplyContent());
		if (!CollectionUtils.isEmpty(contentEles)) {
			Element contentEle = contentEles.first();
			reply.setContent(contentEle.text());
			reply.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, rule.get()));
			reply.setVoice_url(MultimediaExtractor.extractAudioUrl(contentEle));
			reply.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
		} else {
			return null;
		}

		if (!CollectionUtils.isEmpty(element.select(detailConf.getReplyDate()))) {
			String dateField = element.select(detailConf.getReplyDate()).first().text();
			try {
	            reply.setTimestamp(Utils.formatDate(dateField).getTime());
            } catch (java.text.ParseException e) {
            	LOG.error("Cannot parse date: " + dateField + " in page " + reply.getUrl());
            }
		}

		reply.setOriginal_id(parentId);
		recordInfos.get().add(reply);
		
		return id;
	}

	/**
	 * 保存子回复
	 */
	private String saveSub(RecordInfo reply, Element element, ForumDetailConf detailConf, String parentId) {
		String id = UUID.randomUUID().toString();
		reply.setId(id);
		if (!CollectionUtils.isEmpty(element.select(detailConf.getSubReplyAuthor()))) {
			reply.setNickname(element.select(detailConf.getSubReplyAuthor()).first().text());
		}
		if (!CollectionUtils.isEmpty(element.select(detailConf.getSubReplyContent()))) {
			reply.setContent(element.select(detailConf.getSubReplyContent()).first().text());
		}
		Elements imgs = element.select(detailConf.getSubReplyContent()).select("img");
		StringBuilder imgUrlSb = new StringBuilder();
		for (Element img : imgs) {
			imgUrlSb.append(img.attr("abs:src")).append(" ");// 多个url用空格隔开
		}
		reply.setPic_url(imgUrlSb.toString());
		reply.setVoice_url("");
		reply.setVideo_url("");

		if (!CollectionUtils.isEmpty(element.select(detailConf.getSubReplyDate()))) {
			String dateField = element.select(detailConf.getSubReplyDate()).first().text();
			try {
	            reply.setTimestamp(Utils.formatDate(dateField).getTime());
            } catch (java.text.ParseException e) {
            	LOG.error("Cannot parse date: " + dateField + " in page " + reply.getUrl());
            }
		}

		reply.setOriginal_id(parentId);
		recordInfos.get().add(reply);
		
		return id;
	}

}
