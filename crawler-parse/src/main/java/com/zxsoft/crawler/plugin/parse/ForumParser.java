package com.zxsoft.crawler.plugin.parse;

import java.net.ConnectException;
import java.util.Date;
import java.util.UUID;

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
import com.zxsoft.crawler.parse.Category;
import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.PageHelper;
import com.zxsoft.crawler.parse.ParseException;
import com.zxsoft.crawler.parse.ParseStatus;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.storage.Forum;
import com.zxsoft.crawler.storage.ForumDetailConf;
import com.zxsoft.crawler.storage.Reply;
import com.zxsoft.crawler.storage.Seed;
import com.zxsoft.crawler.storage.WebPageMy;
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
	private JsoupLoader loader = new JsoupLoader();
	
	private DuplicateInspector duplicateInspector;
	private ConfDao confDao;

	private String rule; // filter regular expression
	@Override
	public ParseStatus parse(WebPageMy page) throws Exception {
		Assert.notNull(page, "Page is null");
		Document document = page.getDocument();
		Assert.notNull(document, "Document is null");
		Seed seed = page.getSeed();
		Assert.notNull(seed, "Seed is null");

		String url = seed.getUrl();
		String md5 = Md5Signatrue.generateMd5(url);
		if (duplicateInspector.md5Exist(md5)) return null;
		
		ParseStatus status = new ParseStatus(url);
		ForumDetailConf detailConf = confDao.getForumDetailConf(page.getHost());

		if (detailConf == null) {
			LOG.warn(url + " detail page has no configuration in database.");
			status.setMessage(" detail page has no configuration in database.");
			return status;
		}

		if (seed.getType() == Category.DETAIL_PAGE) { // 新的详细页 或 丢失的详细页
			fetchContent(page, detailConf);
		} else if (seed.getType() == Category.REPLY_PAGE) { // 丢失的回复页
			parsePage(page, document, seed.getMainUrl(), seed.getUrl(), detailConf);
		} else {
			LOG.error("Seed error occur.");
		}

		return status;
	}

	private void fetchContent(WebPageMy page, ForumDetailConf detailConf) throws ParseException, ConnectException {
		Seed seed = page.getSeed();
		Forum forum = new Forum(seed.getTitle(), seed.getUrl(), new Date());
		Document document = page.getDocument();

		// Elements titleEle = document.select(detailConf.getTitle()); //
		if (!CollectionUtils.isEmpty(document.select(detailConf.getReplyNum()))) {
			forum.setReplyNum(Utils.extractNum(document.select(detailConf.getReplyNum()).first().text()));
		}
		if (!StringUtils.isEmpty(detailConf.getReviewNum())
		        && !CollectionUtils.isEmpty(document.select(detailConf.getReviewNum()))) {
			forum.setReviewNum(Integer.valueOf(Utils.extractNum(document.select(detailConf.getReviewNum()).first().text())));
		}
		Elements mainEles = document.select(detailConf.getMaster());
		// 主帖页面, 取得主帖信息
		if (!CollectionUtils.isEmpty(mainEles)) {
			Element mainEle = mainEles.first();
			if (!StringUtils.isEmpty(detailConf.getMasterAuthor())
			        && !CollectionUtils.isEmpty(mainEle.select(detailConf.getMasterAuthor())))
				forum.setAuthor(mainEle.select(detailConf.getMasterAuthor()).first().text());

			Elements contentEles = mainEle.select(detailConf.getMasterContent());
			if (!CollectionUtils.isEmpty(contentEles)) {
				Element contentEle = contentEles.first();
				forum.setContent(contentEle.text());
				forum.setImgUrl(MultimediaExtractor.extractImgUrl(contentEle, rule));
				forum.setAudioUrl(MultimediaExtractor.extractAudioUrl(contentEle));
				forum.setVideoUrl(MultimediaExtractor.extractVideoUrl(contentEle));
			}

			String dateStr = "";
			if (forum.getReleasedate() == null) {
				if (!StringUtils.isEmpty(detailConf.getMasterDate())
				        && !CollectionUtils.isEmpty(mainEle.select(detailConf.getMasterDate()))) {
					dateStr = mainEle.select(detailConf.getMasterDate()).first().text();
					if (!StringUtils.isEmpty(dateStr)) {
						forum.setReleasedate(Utils.formatDate(dateStr));
					}
				}
			}
			// dateStr = forum.getReleasedate().toString();

			String md5 = Md5Signatrue.generateMd5(forum.getUrl(), forum.getAuthor(), forum.getContent(),
			        forum.getImgUrl(), forum.getAudioUrl(), forum.getVideoUrl());
			if (!duplicateInspector.md5Exist(md5)) {
				tools.getRedisSerivice().addMd5(md5);
				tools.getInfoService().addForum(forum);
			}

			parseReply(page, detailConf);
		} else {
			LOG.warn(forum.getUrl() + "主帖信息配置有误.");
			return;
		}
	}

	/**
	 * 解析回复
	 */
	private void parseReply(WebPageMy page, ForumDetailConf detailConf) {
		Document doc = page.getDocument();
		Seed seed = new Seed();
		try {
			seed = page.getSeed().clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		String mainUrl = seed.getMainUrl();
		seed.setType(Category.REPLY_PAGE);
		String currentUrl = mainUrl;
		String newPageUrl = "", currentPageText = "";
		if (!detailConf.isFetchorder()) { // 从第一页
			do {
				parsePage(page, doc, mainUrl, currentUrl, detailConf);
				// get next page
				Element pagebar = PageHelper.getPageBar(doc);
				if (pagebar != null) {
					Element pg = PageHelper.getNextPage(pagebar, currentPageText);
					if (pg != null) {
						currentPageText = pg.text();
						newPageUrl = pg.absUrl("href");
					}
				}
				seed.setUrl(newPageUrl);
				doc = loader.load(seed);
				currentUrl = newPageUrl;
			} while (!StringUtils.isEmpty(newPageUrl));

		} else { // fetch from last page
			// jump to last page
			String lastPageUrl = "";
			Element pagebar = PageHelper.getPageBar(doc);
			if (pagebar != null) {
				Element pg = PageHelper.getLastPage(pagebar);
				if (pg != null) {
					currentPageText = String.valueOf(Utils.extractNum(pg.text()));
					lastPageUrl = pg.absUrl("href");
				}
			}
			if (StringUtils.isEmpty(lastPageUrl)) { // 无分页
				parsePage(page, doc, mainUrl, currentUrl, detailConf);
			} else {
				seed.setUrl(lastPageUrl);
				doc = loader.load(seed);
				currentUrl = lastPageUrl;
				while (true) {
					if (doc == null || StringUtils.isEmpty(currentUrl))
						break;
					parsePage(page, doc, mainUrl, currentUrl, detailConf);
					// 获取上一页
					pagebar = PageHelper.getPageBar(doc);
					Element pg = PageHelper.getPrePage(pagebar, currentPageText);
					if (pg == null)
						break;
					currentPageText = pg.text();
					newPageUrl = pg.absUrl("href");
					seed.setUrl(newPageUrl);
					doc = loader.load(seed);
					currentUrl = newPageUrl;
					LOG.info(currentUrl);
				}
			}
		}
	}

	/**
	 * 解析一页
	 */
	private void parsePage(WebPageMy page, Document doc, String mainUrl, String currentUrl, ForumDetailConf detailConf) {
		Elements replyEles = doc.select(detailConf.getReply());
		for (Element element : replyEles) {
			Reply reply = new Reply();
			reply.setMainUrl(mainUrl);
			reply.setCurrentUrl(currentUrl);
			String id = save(page, reply, element, detailConf, null); // 保存回复

			// 解析子回复
			String subReplyDom = detailConf.getSubReply();
			if (StringUtils.isEmpty(subReplyDom))
				continue;
			Elements subReplyEles = element.select(subReplyDom);

			String parentId = id;
			if (!CollectionUtils.isEmpty(subReplyEles)) {
				for (Element ele : subReplyEles) {
					Reply subReply = new Reply();
					subReply.setMainUrl(mainUrl);
					subReply.setCurrentUrl(currentUrl);
					saveSub(subReply, ele, detailConf, parentId);
				}
			}
		}
	}

	/**
	 * 保存回复
	 */
	private String save(WebPageMy page, Reply reply, Element element, ForumDetailConf detailConf, String parentId) {
		String id = UUID.randomUUID().toString();
		reply.setId(id);
		if (!CollectionUtils.isEmpty(element.select(detailConf.getReplyAuthor()))) {
			reply.setAuthorAccount(element.select(detailConf.getReplyAuthor()).first().text());
		}

		Elements contentEles = element.select(detailConf.getReplyContent());
		if (!CollectionUtils.isEmpty(contentEles)) {
			Element contentEle = contentEles.first();
			reply.setContent(contentEle.text());
			reply.setImgUrl(MultimediaExtractor.extractImgUrl(contentEle, rule));
			reply.setAudioUrl(MultimediaExtractor.extractAudioUrl(contentEle));
			reply.setVideoUrl(MultimediaExtractor.extractVideoUrl(contentEle));
		}

		reply.setAddress(null);

		String dateStr = "";
		if (!CollectionUtils.isEmpty(element.select(detailConf.getReplyDate()))) {
			dateStr = element.select(detailConf.getReplyDate()).first().text();
			reply.setReleasedate(Utils.formatDate(dateStr));
		}

		String md5 = Md5Signatrue.generateMd5(reply.getMainUrl(), reply.getAuthorAccount(), reply.getContent(),
		        reply.getImgUrl(), reply.getAudioUrl(), reply.getVideoUrl());
		if (!duplicateInspector.md5Exist(md5)) {
			reply.setParentId(parentId);
			reply.setMd5(md5);
			tools.getRedisSerivice().addMd5(md5);
			tools.getInfoService().addReply(reply);
		}
		
		return id;
	}

	/**
	 * 保存子回复
	 */
	private String saveSub(Reply reply, Element element, ForumDetailConf detailConf, String parentId) {
		String id = UUID.randomUUID().toString();
		reply.setId(id);
		if (!CollectionUtils.isEmpty(element.select(detailConf.getSubReplyAuthor()))) {
			reply.setAuthorAccount(element.select(detailConf.getSubReplyAuthor()).first().text());
		}
		if (!CollectionUtils.isEmpty(element.select(detailConf.getSubReplyContent()))) {
			reply.setContent(element.select(detailConf.getSubReplyContent()).first().text());
		}
		Elements imgs = element.select(detailConf.getSubReplyContent()).select("img");
		StringBuilder imgUrlSb = new StringBuilder();
		for (Element img : imgs) {
			imgUrlSb.append(img.attr("abs:src")).append(" ");// 多个url用空格隔开
		}
		reply.setImgUrl(imgUrlSb.toString());
		reply.setAudioUrl(null);
		reply.setVideoUrl(null);
		reply.setAddress(null);

		String dateStr = "";
		if (!CollectionUtils.isEmpty(element.select(detailConf.getSubReplyDate()))) {
			dateStr = element.select(detailConf.getSubReplyDate()).first().text();
			reply.setReleasedate(Utils.formatDate(dateStr));
		}

		String md5 = Md5Signatrue.generateMd5(reply.getMainUrl(), reply.getAuthorAccount(), reply.getContent(),
		        reply.getImgUrl(), reply.getAudioUrl(), reply.getVideoUrl());
		if (!duplicateInspector.md5Exist(md5)) {
			reply.setMd5(md5);
			reply.setParentId(parentId);
			tools.getRedisSerivice().addMd5(md5);
			tools.getInfoService().addReply(reply);
		}
		
		return id;
	}

}
