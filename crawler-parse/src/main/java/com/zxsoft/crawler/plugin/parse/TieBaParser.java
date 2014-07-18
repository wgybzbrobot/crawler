package com.zxsoft.crawler.plugin.parse;

import java.util.Collections;
import java.util.Date;
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
import com.zxsoft.crawler.dao.ConfDao;
import com.zxsoft.crawler.duplicate.DuplicateInspector;
import com.zxsoft.crawler.parse.Category;
import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.ParseStatus;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClientPageHelper;
import com.zxsoft.crawler.storage.Forum;
import com.zxsoft.crawler.storage.ForumDetailConf;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.Reply;
import com.zxsoft.crawler.storage.Seed;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.Md5Signatrue;
import com.zxsoft.crawler.util.Utils;

/**
 * 百度贴吧解析器，从ForumParser中隔离出来
 */
public class TieBaParser extends Parser {

	private static Logger LOG = LoggerFactory.getLogger(TieBaParser.class);
	private String mainUrl;
	private String rule; // filter regular expression
	private long fetchTime;
	private long prevFetchTime;
	private boolean ajax;

	public ParseStatus parse(WebPage page) throws Exception {
		Assert.notNull(page, "Page is null");
		Document document = page.getDocument();
		Assert.notNull(document, "Document is null");
		ajax = page.isAjax();
		mainUrl = page.getBaseUrl();
		fetchTime = page.getFetchTime();
		prevFetchTime = page.getPrevFetchTime();
		
		ForumDetailConf detailConf = confDao.getForumDetailConf(Utils.getHost(mainUrl));

//		if (seed.getType() == Category.DETAIL_PAGE) { // 新的详细页 或 丢失的详细页
//			fetchContent(page, detailConf);
//		} else if (seed.getType() == Category.REPLY_PAGE) { // 丢失的回复页
//			parsePage(page, document, seed.getMainUrl(), seed.getUrl(), detailConf);
//		} else {
//			LOG.error("Seed error occur.");
//		}
		fetchContent(page, detailConf);

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
		RecordInfo info = new RecordInfo(page.getTitle(), mainUrl, page.getFetchTime());

		Document document = httpFetcher.fetch(mainUrl).getDocument();
		page.setDocument(document);
		if (document == null)
			return;
		if (detailConf == null) {
			LOG.warn("Detail page has no configuration in database:" + mainUrl);
			return;
		}

		if (!CollectionUtils.isEmpty(document.select(detailConf.getReplyNum()))) {
			info.setComment_count(Integer.valueOf(document.select(detailConf.getReplyNum()).first().text()));
		}
		
		// if url + reply number is not changed, then stop fetch.
		String _md5 = Md5Signatrue.generateMd5(mainUrl, String.valueOf(info.getComment_count()));
		if (duplicateInspector.md5Exist(_md5))
			return;
		
		if (!StringUtils.isEmpty(detailConf.getReviewNum())
		        && !CollectionUtils.isEmpty(document.select(detailConf.getReviewNum()))) {
			info.setRead_count(Integer.valueOf(document.select(detailConf.getReviewNum()).first().text()));
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
				info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, rule));
				info.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
			}
			if (!CollectionUtils.isEmpty(mainEle.select("a.voice_player_inner"))) {
				String tid = extractTid(info.getUrl());
				String json = mainEle.attr("data-field");
				String pid = extractPid(json);
				info.setVoice_url("http://tieba.baidu.com/voice/index?tid=" + tid + "&pid=" + pid);
			}
			info.setTimestamp(Utils.extractDate(mainEle.attr("data-field")).getTime());
			
			recordInfos.add(info);
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
		String newPageUrl = "", currentPageText = "1";
		Document doc = page.getDocument();
		String currentUrl = mainUrl;

		if (!detailConf.isFetchorder()) { // 从第一页
			do {
				boolean isContinue = parsePage(page, doc, currentUrl, detailConf);
				if (!isContinue) {
					break;
				}
				// 获取下一页
//				Element pagebar = PageHelper.getPageBar(doc);
//				if (pagebar != null) {
//					Element pg = pageHelper.loadNextPage(pagebar, currentPageText);
//					if (pg != null) {
//						currentPageText = pg.text();
//						newPageUrl = pg.absUrl("href");
//					}
//				}
//				doc = httpFetcher.fetch(newPageUrl).getDocument();
				doc = pageHelper.loadNextPage(doc, ajax).getDocument();
				currentUrl = newPageUrl;
			} while (!StringUtils.isEmpty(newPageUrl));

		} else { // fetch from last page
			// jump to last page
//			String u = "";
//			Element pg = null;
//			Element pagebar = PageHelper.getPageBar(doc);
//			if (pagebar != null) {
//				pg = PageHelper.getLastPage(pagebar);
//				if (pg != null) {
//					currentPageText = pg.text();
//					u = pg.absUrl("href");
//				}
//			}
			ProtocolOutput ptemp = pageHelper.loadLastPage(doc, ajax); 
			if (ptemp == null) {
				LOG.info("++++++++++++++++++++");
				return;
			}
				
			Document lastDoc = ptemp.getDocument();
			if (lastDoc == null) { // 无分页
				parsePage(page, doc, currentUrl, detailConf);
			} else {
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
//					pagebar = PageHelper.getPageBar(doc);
//					pg = PageHelper.getPrePage(pagebar, currentPageText);
//					if (pg == null)
//						break;
//					currentPageText = pg.text();
//					newPageUrl = pg.absUrl("href");
//					doc = httpFetcher.fetch(newPageUrl).getDocument();
					ProtocolOutput potemp = pageHelper.loadPrevPage(doc, ajax); 
					if (potemp == null) {
						LOG.error("Error: parse reply " + currentUrl);
						break;
					}
					doc = potemp.getDocument();
					currentUrl = doc.location();
					LOG.info(currentUrl);
				}
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
	private boolean parsePage(WebPage page, Document doc, String currentUrl, ForumDetailConf detailConf) {
		Elements replyEles = doc.select(detailConf.getReply()); // 所有回复
		String tid = extractTid(mainUrl);
		Collections.reverse(replyEles);
		for (Element element : replyEles) {
			RecordInfo info = new RecordInfo();
			info.setOriginal_url(mainUrl);
			info.setUrl(currentUrl);
			
			String json = element.attr("data-field");
			String pid = extractPid(json);
			info = save(info, element, detailConf, tid, pid, page); // 保存回复

			if (info.getTimestamp() != 0 && info.getTimestamp() < prevFetchTime)
				return false;
			/*
			 * 解析子回复
			 */
			String surl = "http://tieba.baidu.com/p/comment?tid=" + tid + "&pid=" + pid + "&pn=1&t="
			        + System.currentTimeMillis();
			
			RecordInfo subReply = new RecordInfo();
			subReply.setOriginal_url(mainUrl);
			subReply.setUrl(currentUrl);
			subReply.setOriginal_id(info.getId());
			saveSub(subReply, surl, detailConf, tid);
		}
		return true;
	}

	/**
	 * 保存回复
	 */
	private RecordInfo save(RecordInfo info, Element element, ForumDetailConf detailConf, String tid, String pid, WebPage page) {
		
		info.setTimestamp(Utils.extractDate(element.attr("data-field")).getTime());
		String id = UUID.randomUUID().toString();
		info.setId(id);
		if (!CollectionUtils.isEmpty(element.select(detailConf.getReplyAuthor()))) {
			info.setNickname(element.select(detailConf.getReplyAuthor()).first().text());
		}

		Elements contentEles = element.select(detailConf.getReplyContent());
		if (!CollectionUtils.isEmpty(contentEles)) {
			Element contentEle = contentEles.first();
			info.setContent(contentEle.text());
			info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, rule));
			info.setVideo_url("");
		}
		if (!CollectionUtils.isEmpty(element.select("a.voice_player_inner"))) {
			info.setVoice_url("http://tieba.baidu.com/voice/index?tid=" + tid + "&pid=" + pid);
		}

		recordInfos.add(info);
		return info;
	}

	/**
	 * 解析子回复
	 */
	private void saveSub(RecordInfo reply, String surl, ForumDetailConf detailConf, String tid) {
		ProtocolOutput ptemp = httpFetcher.fetch(surl);
		if (ptemp.getStatus().getCode() != 200)
			return;
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
			doc = httpFetcher.fetch(u).getDocument();
			while (true) {
				if (doc == null)
					break;
				parseSubPage(reply, doc, detailConf, tid);
				if (CollectionUtils.isEmpty(doc.select("p.j_pager.l_pager.pager_theme_2"))) {
					LOG.error("ERROR ACCUR");
					break;
				}
				newPageUrl = getSubReplyPrePage(doc.select("p.j_pager.l_pager.pager_theme_2").first(), surl);
				if (newPageUrl == null) {
					break;
				}
				doc = httpFetcher.fetch(newPageUrl).getDocument();
			}
		}
	}

	/**
	 * 保存子回复
	 */
	private void parseSubPage(RecordInfo reply, Document doc, ForumDetailConf detailConf, String tid) {

		Elements elements = doc.select("li.lzl_single_post.j_lzl_s_p");
		if (CollectionUtils.isEmpty(elements))
			return;
		for (Element element : elements) {
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

			if (!CollectionUtils.isEmpty(element.select("a.voice_player_inner")) && !StringUtils.isEmpty(spid)) {
				reply.setVoice_url("http://tieba.baidu.com/voice/index?tid=" + tid + "&pid=" + spid);
			}
			reply.setVideo_url("");

			String dateStr = "";
			if (!CollectionUtils.isEmpty(element.select(detailConf.getSubReplyDate()))) {
				dateStr = element.select(detailConf.getSubReplyDate()).first().text();
				reply.setTimestamp(Utils.formatDate(dateStr).getTime());
			}

			recordInfos.add(reply);
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
