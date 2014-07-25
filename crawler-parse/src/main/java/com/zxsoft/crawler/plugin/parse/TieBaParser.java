package com.zxsoft.crawler.plugin.parse;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.ParseStatus;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatusCodes;
import com.zxsoft.crawler.storage.ForumDetailConf;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.Md5Signatrue;
import com.zxsoft.crawler.util.Utils;

/**
 * 百度贴吧解析器，从ForumParser中隔离出来
 */
public class TieBaParser extends Parser {

	private static Logger LOG = LoggerFactory.getLogger(TieBaParser.class);
//	private String mainUrl;
//	private String rule; // filter regular expression
//	private long prevFetchTime;
//	private boolean ajax;
//	private List<RecordInfo> recordInfos  = new LinkedList<RecordInfo>();
	
	
	private ThreadLocal<String> mainUrl = new ThreadLocal<String>();
	private ThreadLocal<String> rule = new ThreadLocal<String>() { protected String initialValue() { return "";}};
	private ThreadLocal<Long> prevFetchTime = new ThreadLocal<Long>();
	private ThreadLocal<Boolean> ajax = new ThreadLocal<Boolean>();
	private ThreadLocal<List<RecordInfo>> threadLocalRecordInfos = new ThreadLocal<List<RecordInfo>>() {
		protected List<RecordInfo> initialValue() {
			return new LinkedList<RecordInfo>();
		}
	};
	private  ForumDetailConf detailConf;
	
	public ParseStatus parse(WebPage page) throws Exception {
		Assert.notNull(page, "Page is null");
		Document document = page.getDocument();
		Assert.notNull(document, "Document is null");
//		mainUrl = page.getBaseUrl();
//		prevFetchTime = page.getPrevFetchTime();
//		ajax = page.isAjax();
//		recordInfos = new LinkedList<RecordInfo>();
		
		mainUrl.set(page.getBaseUrl());
		prevFetchTime.set(page.getPrevFetchTime());
		ajax.set(page.isAjax());
		threadLocalRecordInfos.set(new LinkedList<RecordInfo>());
		

		ForumDetailConf detailConf = confDao.getForumDetailConf(Utils.getHost(mainUrl.get()));

		// if (seed.getType() == Category.DETAIL_PAGE) { // 新的详细页 或 丢失的详细页
		// fetchContent(page, detailConf);
		// } else if (seed.getType() == Category.REPLY_PAGE) { // 丢失的回复页
		// parsePage(page, document, seed.getMainUrl(), seed.getUrl(),
		// detailConf);
		// } else {
		// LOG.error("Seed error occur.");
		// }
		fetchContent(page, detailConf);

		int num = indexWriter.write(threadLocalRecordInfos.get());
		LOG.info(mainUrl.get() + " has " + num + " records.");
		return null;
	}

	/**
	 * 解析详细页
	 * 
	 * @param page
	 *            详细页
	 * @param detailConf
	 *            详细页配置对象(Object of detail page configuration)
	 */
	private void fetchContent(WebPage page, ForumDetailConf detailConf) {
		RecordInfo info = new RecordInfo(page.getTitle(), mainUrl.get(), page.getFetchTime());

		ProtocolOutput ptemp = fetch(mainUrl.get(), false);
		if (ptemp == null || !ptemp.getStatus().isSuccess())
			return;
		Document document = ptemp.getDocument();
		page.setDocument(document);
		if (document == null)
			return;
		if (detailConf == null) {
			LOG.warn("Detail page has no configuration in database:" + mainUrl);
			return;
		}

		if (!CollectionUtils.isEmpty(document.select(detailConf.getReplyNum()))) {
			info.setComment_count(Integer.valueOf(document.select(detailConf.getReplyNum()).first()
			        .text()));
		}

		// if url + reply number is not changed, then stop fetch.
		String _md5 = Md5Signatrue.generateMd5(mainUrl.get(), String.valueOf(info.getComment_count()));
		if (duplicateInspector.md5Exist(_md5))
			return;

		if (!StringUtils.isEmpty(detailConf.getReviewNum())
		        && !CollectionUtils.isEmpty(document.select(detailConf.getReviewNum()))) {
			info.setRead_count(Integer.valueOf(document.select(detailConf.getReviewNum()).first()
			        .text()));
		}

		Elements mainEles = document.select(detailConf.getMaster());
		// 主帖页面, 取得主帖信息
		if (!CollectionUtils.isEmpty(mainEles)) {
			Element mainEle = mainEles.first();
			if (!CollectionUtils.isEmpty(mainEle.select(detailConf.getMasterAuthor()))) {
				info.setNickname(mainEle.select(detailConf.getMasterAuthor()).first().text());
			}
			Elements contentEles = mainEle.select(detailConf.getMasterContent());
			if (!CollectionUtils.isEmpty(contentEles)) {
				Element contentEle = contentEles.first();
				info.setContent(contentEle.text());
				info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, rule.get()));
				info.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
			}
			if (!CollectionUtils.isEmpty(mainEle.select("a.voice_player_inner"))) {
				String tid = extractTid(info.getUrl());
				String json = mainEle.attr("data-field");
				String pid = extractPid(json);
				info.setVoice_url("http://tieba.baidu.com/voice/index?tid=" + tid + "&pid=" + pid);
			}
			
			String dateField = mainEle.attr("data-field");
            try {
            	Date dateTemp = Utils.extractDate(dateField);
	            info.setTimestamp(dateTemp.getTime());
            } catch (ParseException e) {
            	LOG.error("Cannot parse date: " + dateField + " in page " + mainUrl.get());
            }

//			recordInfos.add(info);
			threadLocalRecordInfos.get().add(info);
			parseReply(page, detailConf);
		} else {
			LOG.warn("主帖信息配置可能有误:" + mainUrl);
			return;
		}
	}

	/**
	 * 解析回复
	 */
	private void parseReply(WebPage page, ForumDetailConf detailConf) {
		String newPageUrl = "", currentPageText = "1";
		Document doc = page.getDocument();
		String currentUrl = mainUrl.get();

		if (!detailConf.isFetchorder()) { // 从第一页
			int pageNum = 1;
			do {
				boolean isContinue = parsePage(page, doc, currentUrl, detailConf);
				if (!isContinue) {
					break;
				}
				// 获取下一页
				pageNum++;
				ProtocolOutput ptemp = fetchNextPage(pageNum, doc, ajax.get());
				if (ptemp == null || !ptemp.getStatus().isSuccess()) {
					return;
				}
				doc = ptemp.getDocument();
				currentUrl = newPageUrl;
			} while (!StringUtils.isEmpty(newPageUrl));

		} else { // fetch from last page
			// jump to last page
			ProtocolOutput ptemp = fetchLastPage(doc, ajax.get());
			Document lastDoc = null;
			if (ptemp == null || (lastDoc = ptemp.getDocument()) == null) {
				parsePage(page, doc, currentUrl, detailConf);
				return;
			}

			doc = lastDoc;
			currentUrl = doc.location();
			while (true) {
				if (doc == null || StringUtils.isEmpty(currentUrl))
					break;
				boolean isContinue = parsePage(page, doc, currentUrl, detailConf);
				if (!isContinue) {
					break;
				}
				// 获取上一页
//				LOG.info(doc.location());
				ProtocolOutput potemp = fetchPrevPage(-1, doc, ajax.get());
				if (potemp == null) {
					break;
				}
				doc = potemp.getDocument();
				currentUrl = doc.location();
			}
		}
	}

	/**
	 * 解析<code>currentUrl</code>.
	 * 
	 * @param doc
	 *            The document of <code>currentUrl</code>
	 * @param mainUrl
	 *            <code>currentUrl</code> 的主帖页面URL
	 * @param currentUrl
	 *            当前页URL
	 * @param detailConf
	 *            详细页配置对象(Object of detail page configuration)
	 */
	private boolean parsePage(WebPage page, Document doc, String currentUrl,
	        ForumDetailConf detailConf) {
		Elements replyEles = doc.select(detailConf.getReply()); // 所有回复
		String tid = extractTid(mainUrl.get());
		Collections.reverse(replyEles);
		for (Element element : replyEles) {
			RecordInfo info = new RecordInfo();
			info.setOriginal_url(mainUrl.get());
			info.setUrl(currentUrl);

			String json = element.attr("data-field");
			String pid = extractPid(json);
			info = save(info, element, detailConf, tid, pid, page); // 保存回复

			if (info.getTimestamp() != 0 && info.getTimestamp() < prevFetchTime.get())
				return false;
			/*
			 * 解析子回复
			 */
			if (getSubCommentCount(json) < 1) {
				continue;
			}
			String surl = "http://tieba.baidu.com/p/comment?tid=" + tid + "&pid=" + pid
			        + "&pn=1&t=" + System.currentTimeMillis();

			RecordInfo subReply = new RecordInfo();
			subReply.setOriginal_url(mainUrl.get());
			subReply.setUrl(currentUrl);
			subReply.setOriginal_id(info.getId());
			saveSub(subReply, surl, detailConf, tid);
		}
		return true;
	}

	/**
	 * 保存回复
	 */
	private RecordInfo save(RecordInfo info, Element element, ForumDetailConf detailConf,
	        String tid, String pid, WebPage page) {

		String dateField = element.attr("data-field");
        try {
        	Date dateTemp = Utils.extractDate(dateField);
            info.setTimestamp(dateTemp.getTime());
        } catch (ParseException e) {
        	LOG.error("Cannot parse date: " + dateField + " in page " + info.getUrl());
        }
        
		String id = UUID.randomUUID().toString();
		info.setId(id);
		if (!CollectionUtils.isEmpty(element.select(detailConf.getReplyAuthor()))) {
			info.setNickname(element.select(detailConf.getReplyAuthor()).first().text());
		}

		Elements contentEles = element.select(detailConf.getReplyContent());
		if (!CollectionUtils.isEmpty(contentEles)) {
			Element contentEle = contentEles.first();
			info.setContent(contentEle.text());
			info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, rule.get()));
			info.setVideo_url("");
		}
		if (!CollectionUtils.isEmpty(element.select("a.voice_player_inner"))) {
			info.setVoice_url("http://tieba.baidu.com/voice/index?tid=" + tid + "&pid=" + pid);
		}

//		recordInfos.add(info);
		threadLocalRecordInfos.get().add(info);
		return info;
	}

	/**
	 * 解析子回复
	 */
	private void saveSub(RecordInfo reply, String surl, ForumDetailConf detailConf, String tid) {
		ProtocolOutput ptemp = fetch(surl, ajax.get());
		if (ptemp.getStatus().getCode() != ProtocolStatusCodes.SUCCESS){
			LOG.info("No Sub reply infomation.");
			return;
		}
		Document doc = ptemp.getDocument();
		if (doc == null) {
			LOG.info("No Sub reply infomation.");
			return;
		}

		Elements pagebar = doc.select("p.j_pager.l_pager.pager_theme_2");
		String u = "", newPageUrl = "";
		if (!CollectionUtils.isEmpty(pagebar)) {
			u = getSubReplyLastPage(pagebar.first(), surl);
		}

		if (StringUtils.isEmpty(u)) { // 无分页
			parseSubPage(reply, doc, detailConf, tid);
		} else {
			// jump to last page
			ptemp = fetch(u, ajax.get());
			while (true) {
				if (ptemp == null || !ptemp.getStatus().isSuccess())
					break;
				doc = ptemp.getDocument();
				parseSubPage(reply, doc, detailConf, tid);
				if (CollectionUtils.isEmpty(doc.select("p.j_pager.l_pager.pager_theme_2"))) {
					break;
				}
				newPageUrl = getSubReplyPrePage(doc.select("p.j_pager.l_pager.pager_theme_2")
				        .first(), surl);
				if (newPageUrl == null) {
					break;
				}
				ptemp = fetch(newPageUrl, false);
			}
		}
	}

	/**
	 * 保存子回复
	 */
	private void parseSubPage(RecordInfo _reply, Document doc, ForumDetailConf detailConf, String tid) {

		Elements elements = doc.select("li.lzl_single_post.j_lzl_s_p");
		if (CollectionUtils.isEmpty(elements))
			return;
		for (Element element : elements) {
			RecordInfo reply = _reply.clone();
			String id = UUID.randomUUID().toString();
			reply.setId(id);

			String json = element.attr("data-field");
			String spid = extractSpid(json);

			if (!CollectionUtils.isEmpty(element.select(detailConf.getSubReplyAuthor()))) {
				reply.setNickname(element.select(detailConf.getSubReplyAuthor()).first().text());
			}
			if (!CollectionUtils.isEmpty(element.select(detailConf.getSubReplyContent()))) {
				reply.setContent(element.select(detailConf.getSubReplyContent()).first().text());
			}
			Elements imgs = element.select(detailConf.getSubReplyContent()).select("img");
			StringBuilder imgUrlSb = new StringBuilder();
			for (Element img : imgs) {
				imgUrlSb.append(img.attr("abs:src"));
			}
			reply.setPic_url(imgUrlSb.toString());

			if (!CollectionUtils.isEmpty(element.select("a.voice_player_inner"))
			        && !StringUtils.isEmpty(spid)) {
				reply.setVoice_url("http://tieba.baidu.com/voice/index?tid=" + tid + "&pid=" + spid);
			}
			reply.setVideo_url("");

			
			if (!CollectionUtils.isEmpty(element.select(detailConf.getSubReplyDate()))) {
				String dateField = element.select(detailConf.getSubReplyDate()).first().text();
		        try {
		        	Date dateTemp = Utils.extractDate(dateField);
		            reply.setTimestamp(dateTemp.getTime());
		        } catch (ParseException e) {
		        	LOG.error("Cannot parse date: " + dateField + " in page " + _reply.getUrl());
		        }
			}

//			recordInfos.add(reply);
			threadLocalRecordInfos.get().add(reply);
		}
	}

	/**
	 * get tid
	 */
	private String extractTid(String str) {
		Pattern pattern = Pattern.compile("\\d{5,12}");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			return matcher.group(0);
		}
		LOG.warn("Cannot extract tid ");
		return null;
	}

	private String extractPid(String json) {
		JsonParser jsonParser = new JsonParser();
		JsonObject content = jsonParser.parse(json).getAsJsonObject().getAsJsonObject("content");
		if (content == null) {
			LOG.error("回复pid抽取失败");
			return null;
		}
		return content.get("post_id").getAsString();
	}
	
	private String extractSpid(String json) {
		JsonParser jsonParser = new JsonParser();
		JsonObject content = jsonParser.parse(json).getAsJsonObject();
		if (content == null) {
			LOG.error("子回复spid抽取失败");
			return null;
		}
		return content.get("spid").getAsString();
	}
	
	private int getSubCommentCount(String json) {
		JsonParser jsonParser = new JsonParser();
		JsonObject content = jsonParser.parse(json).getAsJsonObject().getAsJsonObject("content");
		if (content == null) {
			LOG.error("子回复数量抽取失败");
			return 0;
		}
		return content.get("comment_num").getAsInt();
	}

	/**
	 * @param pagebar
	 *            子回复分页栏
	 * @param surl
	 *            子回复url
	 */
	private String getSubReplyLastPage(Element pagebar, String surl) {
		// 1. get all links from page bar
		Elements links = pagebar.getElementsByTag("a");
		if (CollectionUtils.isEmpty(links)) {
			return null;
		}

		// 2. get max num or contains something in all links, that is last page
		int i = 1;
		Element el = null;
		Pattern pattern1 = Pattern.compile("\\d+");
		Pattern pattern2 = Pattern.compile("尾页");
		for (Element ele : links) {
			String v = ele.text();

			Matcher matcher = pattern2.matcher(v);
			if (matcher.find()) {
				el = ele;
				break;
			}

			matcher = pattern1.matcher(v);
			if (matcher.find()) {
				v = matcher.group(0);
			}
			if (v.matches("\\d+") && Integer.valueOf(v) > i) { // get max num
				i = Integer.valueOf(v);
				el = ele;
			}
		}

		String num = el.attr("href").substring(1);
		return surl.replaceAll("pn=\\d+", "pn=" + num);
		// 3. return last page url
		// return el.absUrl("href");

	}

	private String getSubReplyPrePage(Element pagebar, String surl) {
		// 1. get all links from page bar
		Elements links = pagebar.getElementsByTag("a");
		if (CollectionUtils.isEmpty(links)) {
			return null;
		}

		// 2. extract link contains something in all links, that is last page
		Element el = null;
		Pattern pattern2 = Pattern.compile("上一页");
		for (Element ele : links) {
			String v = ele.text();

			Matcher matcher = pattern2.matcher(v);
			if (matcher.find()) {
				el = ele;
				break;
			}
		}

		// 3. return last page url
		if (el == null) {
			return null;
		}

		String num = el.attr("href").substring(1);
		return surl.replaceFirst("pn=\\d+", "pn=" + num);

		// return el.absUrl("href");
	}

	// }

}
