package com.zxsoft.crawler.plugin.parse;

import java.net.URL;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.Assert;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zxsoft.crawler.dns.DNSCache;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.FetchStatus.Status;
import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.plugin.parse.ext.DateExtractor;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
import com.zxsoft.crawler.protocol.util.Md5Signatrue;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.store.OutputException;
import com.zxsoft.crawler.util.Utils;

/**
 * 百度贴吧解析器，从ForumParser中隔离出来
 */
public class TieBaParser extends Parser {

	private static Logger LOG = LoggerFactory.getLogger(TieBaParser.class);
	private long prevFetchTime;
	private List<RecordInfo> recordInfos = new LinkedList<RecordInfo>();

	private String mainUrl;
	private DetailConf detailConf;
	private String ip;
	private long monitorTime = new Date().getTime();
	public List<RecordInfo> getRecordInfos() {
		return recordInfos;
	}

	public FetchStatus parse(WebPage page) throws Exception {
		Assert.notNull(page, "Page is null");
		ProtocolOutput _output = fetch(page);
		mainUrl = page.getBaseUrl();
		prevFetchTime =page.getPrevFetchTime();
		// ajax.set(page.isAjax());
		if (!_output.getStatus().isSuccess())
			return new FetchStatus(mainUrl, 51, Status.PROTOCOL_FAILURE);

		Document mainDoc = _output.getDocument();
		page.setDocument(mainDoc);

		detailConf = confDao.getDetailConf(page.getListUrl(), Utils.getHost(mainUrl));
		if (detailConf == null) {
			return new FetchStatus(mainUrl, 41, Status.CONF_ERROR);
		}

		ip = DNSCache.getIp(new URL(mainUrl));
		
		/*
		 * Parse main thread
		 */
		RecordInfo info = new RecordInfo(page.getTitle(), page.getBaseUrl(), page.getFetchTime() / 1000L);
		info.setIp(ip);
		Document document = page.getDocument();
		String replyNumDom = detailConf.getReplyNum();
		if (StringUtils.hasLength(replyNumDom) && !CollectionUtils.isEmpty(document.select(replyNumDom))) {
			info.setComment_count(Integer.valueOf(document.select(detailConf.getReplyNum()).first().text()));
		}
		String reviewNumDom = detailConf.getReviewNum();
		if (!StringUtils.isEmpty(reviewNumDom) && !CollectionUtils.isEmpty(document.select(reviewNumDom))) {
			info.setRead_count(Integer.valueOf(document.select(reviewNumDom).first().text()));
		}
		Elements mainEles = document.select(detailConf.getMaster());
		// 主帖页面, 取得主帖信息
		if (!CollectionUtils.isEmpty(mainEles)) {
			Element mainEle = mainEles.first();
			String masterAuthorDom = detailConf.getAuthor();
			if (!StringUtils.isEmpty(masterAuthorDom) && !CollectionUtils.isEmpty(mainEle.select(masterAuthorDom))) {
				info.setNickname(mainEle.select(masterAuthorDom).first().text());
			}
			Elements contentEles = mainEle.select(detailConf.getContent());
			if (!CollectionUtils.isEmpty(contentEles)) {
				Element contentEle = contentEles.first();
				info.setContent(contentEle.text());
				info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, ""));
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
				LOG.error("Cannot parse date: " + dateField + " in page " + mainUrl);
			}
			info.setId(Md5Signatrue.generateMd5(info.getNickname(), info.getContent(), info.getPic_url(), info.getVoice_url(),
			        info.getVideo_url()));
			getRecordInfos().add(info);
		} else {
			return new FetchStatus(mainUrl, 42, Status.CONF_ERROR);
		}

		/*
		 * Parse Reply-thread
		 */
		String newPageUrl = ""/* , currentPageText = "1" */;
		Document _doc = mainDoc;
		String currentUrl = mainUrl; // 首页
		if (!detailConf.isFetchorder()) { // 从第一页
			int pageNum = 1;
			do {
				boolean isContinue = parsePage(page, _doc, currentUrl);
				if (!isContinue) {
					break;
				}
				// 获取下一页
				pageNum++;
				WebPage np = page.clone();
				np.setDocument(_doc);
				_output = fetchNextPage(pageNum, np);
				if (!_output.getStatus().isSuccess()) {
					break;
				}
				_doc = _output.getDocument();
				currentUrl = newPageUrl;
			} while (!StringUtils.isEmpty(newPageUrl));
		} else { // fetch from last page
			WebPage np = page.clone(); // jump to last page
			np.setDocument(_doc);
			_output = fetchLastPage(np);
			if (!_output.getStatus().isSuccess()) {
				parsePage(np, _doc, currentUrl);
			} else {
				_doc = _output.getDocument();
				currentUrl = _doc.location();
				while (true) {
					if (_doc == null || StringUtils.isEmpty(currentUrl))
						break;
					boolean isContinue = parsePage(np, _doc, currentUrl);
					if (!isContinue) {
						break;
					}
					// 获取上一页
					np.setDocument(_doc);
					np.setBaseUrl(currentUrl);
					ProtocolOutput potemp = fetchPrevPage(-1, np);
					if (potemp == null || !potemp.getStatus().isSuccess()) {
						break;
					}
					_doc = potemp.getDocument();
					currentUrl = _doc.location();
				}
			}
		}

		int count = getRecordInfos().size();
		try {
			indexWriter.write(getRecordInfos());
		} catch (OutputException e) {
			throw new OutputException(mainUrl + "data output failure");
//			return new FetchStatus(mainUrl, 61, Status.OUTPUT_FAILURE, count);
		}

		return new FetchStatus(mainUrl, 21, Status.SUCCESS, count);
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
	 * @param threadLocalDetailConf
	 *            详细页配置对象(Object of detail page configuration)
	 */
	private boolean parsePage(WebPage page, Document doc, String currentUrl) {
		Elements replyEles = doc.select(detailConf.getReply()); // 所有回复
		String tid = extractTid(mainUrl);
		Collections.reverse(replyEles);
		for (Element element : replyEles) {
			RecordInfo info = new RecordInfo();
			info.setOriginal_url(mainUrl);
			info.setUrl(currentUrl);
			info.setIp(ip);

			String json = element.attr("data-field");
			String pid = extractPid(json);
			info = save(info, element, tid, pid, page); // 保存回复

			if (info.getTimestamp() != 0 && info.getTimestamp() < prevFetchTime)
				return false;
			/*
			 * 解析子回复
			 */
			if (getSubCommentCount(json) < 1) {
				continue;
			}
			String surl = "http://tieba.baidu.com/p/comment?tid=" + tid + "&pid=" + pid + "&pn=1&t=" + System.currentTimeMillis();

			RecordInfo subReply = new RecordInfo();
			subReply.setIp(ip);
			subReply.setOriginal_url(mainUrl);
			subReply.setUrl(currentUrl);
			subReply.setOriginal_id(info.getId());
			saveSub(subReply, surl, tid);
		}
		return true;
	}

	/**
	 * 保存回复
	 */
	private RecordInfo save(RecordInfo info, Element element, String tid, String pid, WebPage page) {

		String dateField = element.attr("data-field");
		try {
			Date dateTemp = Utils.extractDate(dateField);
			info.setTimestamp(dateTemp.getTime());
		} catch (ParseException e) {
			LOG.error("Cannot parse date: " + dateField + " in page " + info.getUrl());
		}

		if (!CollectionUtils.isEmpty(element.select(detailConf.getReplyAuthor()))) {
			info.setNickname(element.select(detailConf.getReplyAuthor()).first().text());
		}

		Elements contentEles = element.select(detailConf.getReplyContent());
		if (!CollectionUtils.isEmpty(contentEles)) {
			Element contentEle = contentEles.first();
			info.setContent(contentEle.text());
			info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, ""));
			info.setVideo_url("");
		}
		if (!CollectionUtils.isEmpty(element.select("a.voice_player_inner"))) {
			info.setVoice_url("http://tieba.baidu.com/voice/index?tid=" + tid + "&pid=" + pid);
		}

		info.setId(Md5Signatrue.generateMd5(info.getNickname(), info.getContent(), info.getPic_url(), info.getVoice_url(),
		        info.getVideo_url()));
		info.setLasttime(monitorTime);
		getRecordInfos().add(info);
		return info;
	}

	/**
	 * 解析子回复
	 */
	private void saveSub(RecordInfo reply, String surl, String tid) {
		WebPage np = new WebPage(surl, false);
		ProtocolOutput ptemp = fetch(np);
		if (ptemp.getStatus().getCode() != STATUS_CODE.SUCCESS) {
			LOG.debug("No Sub reply infomation.");
			return;
		}
		Document doc = ptemp.getDocument();
		if (doc == null) {
			LOG.debug("No Sub reply infomation.");
			return;
		}

		Elements pagebar = doc.select("p.j_pager.l_pager.pager_theme_2");
		String u = "", newPageUrl = "";
		if (!CollectionUtils.isEmpty(pagebar)) {
			try {
				u = getSubReplyLastPage(pagebar.first(), surl);
			} catch (Exception e) {

			}
		}

		if (StringUtils.isEmpty(u)) { // 无分页
			parseSubPage(reply, doc, tid);
		} else {
			// jump to last page
			np.setBaseUrl(u);
			ptemp = fetch(np);
			while (true) {
				if (ptemp == null || !ptemp.getStatus().isSuccess())
					break;
				doc = ptemp.getDocument();
				parseSubPage(reply, doc, tid);
				if (CollectionUtils.isEmpty(doc.select("p.j_pager.l_pager.pager_theme_2"))) {
					break;
				}
				newPageUrl = getSubReplyPrePage(doc.select("p.j_pager.l_pager.pager_theme_2").first(), surl);
				if (newPageUrl == null) {
					break;
				}
				np.setBaseUrl(newPageUrl);
				ptemp = fetch(np);
			}
		}
	}

	/**
	 * 保存子回复
	 */
	private void parseSubPage(RecordInfo _reply, Document doc, String tid) {

		Elements elements = doc.select("li.lzl_single_post.j_lzl_s_p");
		if (CollectionUtils.isEmpty(elements))
			return;
		for (Element element : elements) {
			RecordInfo reply = _reply.clone();

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

			if (!CollectionUtils.isEmpty(element.select(detailConf.getSubReplyDate()))) {
				String dateField = element.select(detailConf.getSubReplyDate()).first().html();
				try {
					Date dateTemp = DateExtractor.extract(dateField);
					reply.setTimestamp(dateTemp.getTime());
				} catch (Exception e) {
					e.printStackTrace();
					LOG.error("Cannot parse date: " + dateField + " in page " + _reply.getUrl());
				}
			}

			reply.setId(Md5Signatrue.generateMd5(reply.getNickname(), reply.getContent(), reply.getPic_url(), reply.getVoice_url(),
			        reply.getVideo_url()));
			reply.setLasttime(monitorTime);
			getRecordInfos().add(reply);
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
