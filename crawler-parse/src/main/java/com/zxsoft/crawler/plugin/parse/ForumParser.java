package com.zxsoft.crawler.plugin.parse;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.gemstone.gemfire.internal.tools.gfsh.app.commands.get;
import com.zxsoft.crawler.dao.ConfDao;
import com.zxsoft.crawler.duplicate.DuplicateInspector;
import com.zxsoft.crawler.parse.Category;
import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.ParseStatus;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatusCodes;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.store.Output;
import com.zxsoft.crawler.store.OutputException;
import com.zxsoft.crawler.util.Md5Signatrue;
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
	
	public ParseStatus parse(WebPage page) throws Exception {
		Assert.notNull(page, "Page is null");
		Document document = page.getDocument();
		Assert.notNull(document, "Document is null");
		
		mainUrl.set(page.getBaseUrl());
		prevFetchTime.set(page.getPrevFetchTime());
		ajax.set(page.isAjax());
		threadLocalRecordInfos.set(new LinkedList<RecordInfo>());
		threadLocalDetailConf.set(confDao.getDetailConf(Utils.getHost(mainUrl.get())));
		
		ParseStatus status = new ParseStatus(mainUrl.get());
		status.setStatus(ParseStatus.Status.PARSING);
		
		String md5 = Md5Signatrue.generateMd5(mainUrl.get());
		if (duplicateInspector.md5Exist(md5)) {
			status.setStatus(ParseStatus.Status.NOT_CHANGE);
			status.setMessage("This url was fetched and no changed");
			return status;
		}
		
		if (threadLocalDetailConf == null || threadLocalDetailConf.get() == null) {
			LOG.warn("Detail page has no configuration in database." + mainUrl);
			status.setStatus(ParseStatus.Status.PARSE_FAILURE);
			status.setMessage("No detail page configuration found in database");
			return status;
		}

		/*if (seed.getType() == Category.DETAIL_PAGE) { // 新的详细页 或 丢失的详细页
			fetchContent(page, detailConf);
		} else if (seed.getType() == Category.REPLY_PAGE) { // 丢失的回复页
			parsePage(page, document, seed.getMainUrl(), seed.getUrl(), detailConf);
		} else {
			LOG.error("Seed error occur.");
		}*/
		
		fetchContent(page);
		
		int num = 0;
        try {
	        num = indexWriter.write(threadLocalRecordInfos.get());
        } catch (OutputException e) {
        	status.setStatus(ParseStatus.Status.OUTPUT_FAILURE);
        	status.setMessage(e.getMessage());
        }
		LOG.debug(mainUrl.get() + " has " + num + " records.");
		status.setStatus(ParseStatus.Status.SUCCESS);
		status.setMessage("Fetch " + num + " records from " + mainUrl.get());
		status.setCount(num);
		return status;
	}

	public void fetchContent(WebPage page) /*throws ParseException, ConnectException*/ {
		
		RecordInfo info = new RecordInfo(page.getTitle(), mainUrl.get(), page.getFetchTime());
		
		Document document = page.getDocument();
		// Elements titleEle = document.select(detailConf.getTitle()); //
		String replyNumDom = threadLocalDetailConf.get().getReplyNum();
		if (!StringUtils.isEmpty(replyNumDom) && !CollectionUtils.isEmpty(document.select(replyNumDom))) {
			info.setComment_count(Utils.extractNum(document.select(replyNumDom).first().text()));
		}
		String reviewNumDom = threadLocalDetailConf.get().getReviewNum();
		if (!StringUtils.isEmpty(reviewNumDom)
		        && !CollectionUtils.isEmpty(document.select(reviewNumDom))) {
			info.setRead_count(Integer.valueOf(Utils.extractNum(document.select(reviewNumDom).first().text())));
		}
		
		Elements mainEles = document.select(threadLocalDetailConf.get().getMaster());
		// 主帖页面, 取得主帖信息
		if (!CollectionUtils.isEmpty(mainEles)) {
			Element mainEle = mainEles.first();
			String authorDom = threadLocalDetailConf.get().getAuthor();
			if (!StringUtils.isEmpty(authorDom)
			        && !CollectionUtils.isEmpty(mainEle.select(authorDom)))
				info.setNickname(mainEle.select(authorDom).first().text());

			Elements contentEles = mainEle.select(threadLocalDetailConf.get().getContent());
			if (!CollectionUtils.isEmpty(contentEles)) {
				Element contentEle = contentEles.first();
				info.setContent(contentEle.text());
				info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, rule.get()));
				info.setVoice_url(MultimediaExtractor.extractAudioUrl(contentEle));
				info.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
			}

			if (info.getTimestamp() == 0) {
				String dateDom = threadLocalDetailConf.get().getDate();
				if (!StringUtils.isEmpty(dateDom)
				        && !CollectionUtils.isEmpty(mainEle.select(dateDom))) {
					String dateField = mainEle.select(dateDom).first().text();
					if (!StringUtils.isEmpty(dateField)) {
						try {
	                        info.setTimestamp(Utils.formatDate(dateField).getTime());
                        } catch (java.text.ParseException e) {
                        	LOG.error("Cannot parse date: " + dateField + " in page " + mainUrl.get());
                        }
					}
				}
			}

			threadLocalRecordInfos.get().add(info);

			parseReply(page);
		} else {
			LOG.warn("主帖信息配置有误:" + mainUrl);
			return;
		}
	}

	/**
	 * 解析回复
	 */
	private void parseReply(WebPage page) {
		Document doc = page.getDocument();
		String currentUrl = mainUrl.get();
		String newPageUrl = "", currentPageText = "";
		ProtocolOutput ptemp = null;
		if (!threadLocalDetailConf.get().isFetchorder()) { // 从第一页
			int pageNum = 1;
			do {
				parsePage(page, doc, mainUrl.get(), currentUrl);
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
				parsePage(page, doc, mainUrl.get(), currentUrl);
			} else {
				doc = lastDoc;
				while (true) {
					if (doc == null || StringUtils.isEmpty(currentUrl = doc.location()))
						break;
					LOG.debug(currentUrl);
					parsePage(page, doc, mainUrl.get(), currentUrl);
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
	private void parsePage(WebPage page, Document doc, String mainUrl, String currentUrl) {
		Elements replyEles = doc.select(threadLocalDetailConf.get().getReply());
		for (Element element : replyEles) {
			RecordInfo reply = new RecordInfo();
			reply.setOriginal_url(mainUrl);
			reply.setUrl(currentUrl);
			String id = save(page, reply, element, null); // 保存回复

			if (id == null) continue;
			// 解析子回复
			String subReplyDom = threadLocalDetailConf.get().getSubReply();
			if (StringUtils.isEmpty(subReplyDom))
				continue;
			Elements subReplyEles = element.select(subReplyDom);

			String parentId = id;
			if (!CollectionUtils.isEmpty(subReplyEles)) {
				for (Element ele : subReplyEles) {
					RecordInfo subReply = new RecordInfo();
					subReply.setOriginal_url(mainUrl);
					subReply.setUrl(currentUrl);
					saveSub(subReply, ele, parentId);
				}
			}
		}
	}

	/**
	 * 保存回复
	 */
	private String save(WebPage page, RecordInfo reply, Element element, String parentId) {
		String id = UUID.randomUUID().toString();
		reply.setId(id);
		String replyAuthorDom = threadLocalDetailConf.get().getReplyAuthor();
		if (!StringUtils.isEmpty(replyAuthorDom) && !CollectionUtils.isEmpty(element.select(replyAuthorDom))) {
			reply.setNickname(element.select(replyAuthorDom).first().text());
		}
		
		Elements contentEles = element.select(threadLocalDetailConf.get().getReplyContent());
		if (!CollectionUtils.isEmpty(contentEles)) {
			Element contentEle = contentEles.first();
			reply.setContent(contentEle.text());
			reply.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, rule.get()));
			reply.setVoice_url(MultimediaExtractor.extractAudioUrl(contentEle));
			reply.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
		} else {
			return null;
		}

		String replyDateDom = threadLocalDetailConf.get().getReplyDate();
		if (!StringUtils.isEmpty(replyDateDom) && !CollectionUtils.isEmpty(element.select(replyDateDom))) {
			String dateField = element.select(replyDateDom).first().text();
			try {
	            reply.setTimestamp(Utils.formatDate(dateField).getTime());
            } catch (java.text.ParseException e) {
            	LOG.error("Cannot parse date: " + dateField + " in page " + reply.getUrl());
            }
		}

		reply.setOriginal_id(parentId);
		threadLocalRecordInfos.get().add(reply);
		
		return id;
	}

	/**
	 * 保存子回复
	 */
	private String saveSub(RecordInfo reply, Element element, String parentId) {
		String id = UUID.randomUUID().toString();
		reply.setId(id);
		String subReplyAuthorDom = threadLocalDetailConf.get().getSubReplyAuthor();
		if (!StringUtils.isEmpty(subReplyAuthorDom) && !CollectionUtils.isEmpty(element.select(subReplyAuthorDom))) {
			reply.setNickname(element.select(subReplyAuthorDom).first().text());
		}
		String subReplyContentDom = threadLocalDetailConf.get().getSubReplyContent();
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
		String subReplyDate = threadLocalDetailConf.get().getSubReplyDate();
		if (!CollectionUtils.isEmpty(element.select(subReplyDate))) {
			String dateField = element.select(subReplyDate).first().text();
			try {
	            reply.setTimestamp(Utils.formatDate(dateField).getTime());
            } catch (java.text.ParseException e) {
            	LOG.error("Cannot parse date: " + dateField + " in page " + reply.getUrl());
            }
		}

		reply.setOriginal_id(parentId);
		threadLocalRecordInfos.get().add(reply);
		
		return id;
	}

}
