package com.zxsoft.crawler.plugin.parse;

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
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.FetchStatus.Status;
import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.store.OutputException;
import com.zxsoft.crawler.util.Md5Signatrue;
import com.zxsoft.crawler.util.Utils;

/**
 * 百度贴吧解析器，从ForumParser中隔离出来
 */
public class TieBaParser extends Parser {

	private static Logger LOG = LoggerFactory.getLogger(TieBaParser.class);
	
	private ThreadLocal<String> mainUrl = new ThreadLocal<String>();
	private ThreadLocal<String> rule = new ThreadLocal<String>() { protected String initialValue() { return "";}};
	private ThreadLocal<Long> prevFetchTime = new ThreadLocal<Long>();
	private ThreadLocal<Boolean> ajax = new ThreadLocal<Boolean>();
	private ThreadLocal<Boolean> auth = new ThreadLocal<Boolean>();
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
	
	public FetchStatus parse(WebPage page) throws Exception {
		Assert.notNull(page, "Page is null");
		ProtocolOutput outputTemp = fetch(page); 
		Document document = null;
		mainUrl.set(page.getBaseUrl());
		prevFetchTime.set(page.getPrevFetchTime());
		ajax.set(page.isAjax());
		auth.set(page.isAuth());
		FetchStatus status = new FetchStatus(mainUrl.get(), "");
		if (outputTemp == null || (document = outputTemp.getDocument()) == null) {
			LOG.error("Http protocol get page error ..." + page.getBaseUrl());
			status.setStatus(Status.PROTOCOL_FAILURE);
			status.setMessage("Http protocol get page error.");
			return status;
		}
		page.setDocument(document);
		
		threadLocalRecordInfos.set(new LinkedList<RecordInfo>());
		threadLocalDetailConf.set(confDao.getDetailConf(page.getListUrl(), Utils.getHost(mainUrl.get())));

		status.setStatus(FetchStatus.Status.PARSING);
		
		if (threadLocalDetailConf == null || threadLocalDetailConf.get() == null) {
			LOG.warn("Detail page has no configuration in database." + mainUrl);
			status.setStatus(FetchStatus.Status.PARSE_FAILURE);
			status.setMessage("No detail page configuration found in database");
			return status;
		}

		fetchContent(page);
		int num = threadLocalRecordInfos.get().size();
		try {
			indexWriter.write(threadLocalRecordInfos.get());
			LOG.info("size:" + num);
			for (RecordInfo info : threadLocalRecordInfos.get()) {
	            LOG.info(info.getNickname() + ": " + info.getContent());
            }
		} catch (OutputException e) {
			status.setStatus(FetchStatus.Status.OUTPUT_FAILURE);
			status.setMessage(e.getMessage());
			status.setCount(num);
			return status;
		}
		LOG.debug(mainUrl.get() + " has " + num + " records.");
		status.setStatus(Status.SUCCESS);
		status.setCount(num);
		status.setMessage("完成抓取");
		return status;
	}

	/**
	 * 解析详细页
	 * 
	 * @param page
	 *            详细页
	 * @param threadLocalDetailConf
	 *            详细页配置对象(Object of detail page configuration)
	 */
	public void fetchContent(final WebPage page) {
		RecordInfo info = new RecordInfo(page.getTitle(), page.getBaseUrl(), page.getFetchTime());

		Document document = page.getDocument();
		
		if (!CollectionUtils.isEmpty(document.select(threadLocalDetailConf.get().getReplyNum()))) {
			info.setComment_count(Integer.valueOf(document.select(threadLocalDetailConf.get().getReplyNum()).first()
			        .text()));
			// if url + reply number is not changed, then stop fetch.
			String _md5 = Md5Signatrue.generateMd5(mainUrl.get(), String.valueOf(info.getComment_count()));
			if (duplicateInspector.md5Exist(_md5))
				return;
			else
				duplicateInspector.addMd5(_md5);
		}


		String reviewNumDom = threadLocalDetailConf.get().getReviewNum();
		if (!StringUtils.isEmpty(reviewNumDom)
		        && !CollectionUtils.isEmpty(document.select(reviewNumDom))) {
			info.setRead_count(Integer.valueOf(document.select(reviewNumDom).first()
			        .text()));
		}

		Elements mainEles = document.select(threadLocalDetailConf.get().getMaster());
		// 主帖页面, 取得主帖信息
		if (!CollectionUtils.isEmpty(mainEles)) {
			Element mainEle = mainEles.first();
			String masterAuthorDom = threadLocalDetailConf.get().getAuthor();
			if (!StringUtils.isEmpty(masterAuthorDom) && !CollectionUtils.isEmpty(mainEle.select(masterAuthorDom))) {
				info.setNickname(mainEle.select(masterAuthorDom).first().text());
			}
			Elements contentEles = mainEle.select(threadLocalDetailConf.get().getContent());
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

            info.setId(Md5Signatrue.generateMd5(info.getNickname(), info.getContent(),
			        info.getPic_url(), info.getVoice_url(), info.getVideo_url()));
			
			threadLocalRecordInfos.get().add(info);
			parseReply(page);
		} else {
			LOG.warn("主帖信息配置可能有误:" + mainUrl.get());
			return;
		}
	}

	/**
	 * 解析回复
	 */
	private void parseReply(final WebPage page) {
		String newPageUrl = "", currentPageText = "1";
		Document doc = page.getDocument();
		String currentUrl = mainUrl.get(); // 首页

		if (!threadLocalDetailConf.get().isFetchorder()) { // 从第一页
			int pageNum = 1;
			do {
				boolean isContinue = parsePage(page, doc, currentUrl);
				if (!isContinue) {
					break;
				}
				// 获取下一页
				pageNum++;
				WebPage np = page.clone();
				np.setDocument(doc);
				ProtocolOutput ptemp = fetchNextPage(pageNum, np);
				if (ptemp == null || !ptemp.getStatus().isSuccess()) {
					break;
				}
				doc = ptemp.getDocument();
				currentUrl = newPageUrl;
			} while (!StringUtils.isEmpty(newPageUrl));

		} else { // fetch from last page
			// jump to last page
			WebPage np = page.clone();
			np.setDocument(doc);
			ProtocolOutput ptemp = fetchLastPage(np);
			Document lastDoc = null;
			if (ptemp == null || (lastDoc = ptemp.getDocument()) == null) {
				parsePage(np, doc, currentUrl);
				return;
			}

			doc = lastDoc;
			currentUrl = doc.location();
			while (true) {
				if (doc == null || StringUtils.isEmpty(currentUrl))
					break;
				boolean isContinue = parsePage(np, doc, currentUrl);
				if (!isContinue) {
					break;
				}
				// 获取上一页
//				LOG.info(doc.location());
				np.setDocument(doc);
				np.setBaseUrl(currentUrl);
				ProtocolOutput potemp = fetchPrevPage(-1, np);
				if (potemp == null || !potemp.getStatus().isSuccess()) {
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
	 * @param threadLocalDetailConf
	 *            详细页配置对象(Object of detail page configuration)
	 */
	private boolean parsePage(WebPage page, Document doc, String currentUrl) {
		Elements replyEles = doc.select(threadLocalDetailConf.get().getReply()); // 所有回复
		String tid = extractTid(mainUrl.get());
		Collections.reverse(replyEles);
		for (Element element : replyEles) {
			RecordInfo info = new RecordInfo();
			info.setOriginal_url(mainUrl.get());
			info.setUrl(currentUrl);

			String json = element.attr("data-field");
			String pid = extractPid(json);
			info = save(info, element, tid, pid, page); // 保存回复
			
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
			saveSub(subReply, surl, tid);
		}
		return true;
	}

	/**
	 * 保存回复
	 */
	private RecordInfo save(RecordInfo info, Element element, 
	        String tid, String pid, WebPage page) {

		String dateField = element.attr("data-field");
        try {
        	Date dateTemp = Utils.extractDate(dateField);
            info.setTimestamp(dateTemp.getTime());
        } catch (ParseException e) {
        	LOG.error("Cannot parse date: " + dateField + " in page " + info.getUrl());
        }
        
		if (!CollectionUtils.isEmpty(element.select(threadLocalDetailConf.get().getReplyAuthor()))) {
			info.setNickname(element.select(threadLocalDetailConf.get().getReplyAuthor()).first().text());
		}

		Elements contentEles = element.select(threadLocalDetailConf.get().getReplyContent());
		if (!CollectionUtils.isEmpty(contentEles)) {
			Element contentEle = contentEles.first();
			info.setContent(contentEle.text());
			info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, rule.get()));
			info.setVideo_url("");
		}
		if (!CollectionUtils.isEmpty(element.select("a.voice_player_inner"))) {
			info.setVoice_url("http://tieba.baidu.com/voice/index?tid=" + tid + "&pid=" + pid);
		}

		info.setId(Md5Signatrue.generateMd5(info.getNickname(), info.getContent(),
		        info.getPic_url(), info.getVoice_url(), info.getVideo_url()));
		
		threadLocalRecordInfos.get().add(info);
		return info;
	}

	/**
	 * 解析子回复
	 */
	private void saveSub(RecordInfo reply, String surl, String tid) {
		WebPage np = new WebPage(surl, ajax.get());
		ProtocolOutput ptemp = fetch(np);
		if (ptemp.getStatus().getCode() != STATUS_CODE.SUCCESS){
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
			u = getSubReplyLastPage(pagebar.first(), surl);
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
				newPageUrl = getSubReplyPrePage(doc.select("p.j_pager.l_pager.pager_theme_2")
				        .first(), surl);
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

			if (!CollectionUtils.isEmpty(element.select(threadLocalDetailConf.get().getSubReplyAuthor()))) {
				reply.setNickname(element.select(threadLocalDetailConf.get().getSubReplyAuthor()).first().text());
			}
			if (!CollectionUtils.isEmpty(element.select(threadLocalDetailConf.get().getSubReplyContent()))) {
				reply.setContent(element.select(threadLocalDetailConf.get().getSubReplyContent()).first().text());
			}
			Elements imgs = element.select(threadLocalDetailConf.get().getSubReplyContent()).select("img");
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

			if (!CollectionUtils.isEmpty(element.select(threadLocalDetailConf.get().getSubReplyDate()))) {
				String dateField = element.select(threadLocalDetailConf.get().getSubReplyDate()).first().text();
		        try {
		        	Date dateTemp = Utils.formatDate(dateField);
		            reply.setTimestamp(dateTemp.getTime());
		        } catch (ParseException e) {
		        	LOG.error("Cannot parse date: " + dateField + " in page " + _reply.getUrl());
		        }
			}

			reply.setId(Md5Signatrue.generateMd5(reply.getNickname(), reply.getContent(),
					reply.getPic_url(), reply.getVoice_url(), reply.getVideo_url()));

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
