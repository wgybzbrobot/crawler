package com.zxsoft.crawler.web.verification;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.StringUtils;

import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.parse.ParseTool;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.CrawlerConfiguration;
import com.zxsoft.crawler.util.Utils;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PageHelper;

public class DetailConfigVerification extends ParseTool {

	private static Logger LOG = LoggerFactory.getLogger(DetailConfigVerification.class);

	public Map<String, Object> verify(ConfDetail detailConf, String testUrl) {
		Map<String, Object> info = new LinkedHashMap<String, Object>();
		// info.put("测试页URL", detailConf.getTestUrl());
		List<Map<String, String>> errors = new LinkedList<Map<String, String>>();
		WebPage page = new WebPage(testUrl, false);
		ProtocolOutput protocolOutput = fetch(page);
		Document document = null;
		if (protocolOutput == null || protocolOutput.getDocument() == null || !protocolOutput.getStatus().isSuccess()) {
			Map<String, String> error = new HashMap<String, String>();
			error.put("field", "url");
			error.put("msg", "连接失败");
			errors.add(error);
		} else {
			document = protocolOutput.getDocument();
			if (!StringUtils.isEmpty(detailConf.getReplyNum())) {
				Elements replyNumEles = document.select(detailConf.getReplyNum());
				if (CollectionUtils.isEmpty(replyNumEles)) {
					Map<String, String> error = new HashMap<String, String>();
					error.put("field", "replyNum");
					error.put("msg", "获取回复数失败");
					errors.add(error);
				} else {
					String replyNum = String.valueOf(Utils.extractNum(replyNumEles.first().text()));
					info.put("replyNum", replyNum);
				}
			}

			if (!StringUtils.isEmpty(detailConf.getReviewNum())) {
				Elements reviewNumEles = document.select(detailConf.getReviewNum());
				if (CollectionUtils.isEmpty(reviewNumEles)) {
					Map<String, String> error = new HashMap<String, String>();
					error.put("field", "reviewNum");
					error.put("msg", "获取浏览数失败");
					errors.add(error);
				} else {
					String reviewNum = String
					        .valueOf(Utils.extractNum(reviewNumEles.first().text()));
					info.put("reviewNum", reviewNum);
				}
			}

			if (!StringUtils.isEmpty(detailConf.getForwardNum())) {
				Elements forwardNumEles = document.select(detailConf.getForwardNum());
				if (CollectionUtils.isEmpty(forwardNumEles)) {
					Map<String, String> error = new HashMap<String, String>();
					error.put("field", "forwardNum");
					error.put("msg", "获取转帖数失败");
					errors.add(error);
				} else {
					String forwardNum = String.valueOf(Utils
					        .extractNum(forwardNumEles.first().text()));
					info.put("forwardNum", forwardNum);
				}
			}

			Element pagebar = null;
			try {
				pagebar = PageHelper.getPageBar(document);
			} catch (PageBarNotFoundException e) {
				LOG.warn(e.getMessage());
			}
			String pagebarText = pagebar == null ? "" : pagebar.html();
			info.put("pagebar", pagebarText);

			/*
			 * 主贴
			 */
			if (StringUtils.hasLength(detailConf.getMaster())) {
				Elements masterEles = document.select(detailConf.getMaster());
				if (CollectionUtils.isEmpty(masterEles)) {
					Map<String, String> error = new HashMap<String, String>();
					error.put("field", "master");
					error.put("msg", "获取主帖信息失败");
					errors.add(error);
				} else {
					if (!StringUtils.isEmpty(detailConf.getAuthor())) {
						Elements masterAuthorEles = masterEles.select(detailConf.getAuthor());
						if (CollectionUtils.isEmpty(masterAuthorEles)) {
							Map<String, String> error = new HashMap<String, String>();
							error.put("field", "author");
							error.put("msg", "获取楼主失败");
							errors.add(error);
						} else {
							String masterAuthor = masterAuthorEles.first().text();
							info.put("author", masterAuthor);
						}
					}
					if (!StringUtils.isEmpty(detailConf.getDate())) {
						Elements masterDateEles = masterEles.select(detailConf.getDate());
						if (CollectionUtils.isEmpty(masterDateEles)) {
							Map<String, String> error = new HashMap<String, String>();
							error.put("field", "date");
							error.put("msg", "获取发布时间失败");
							errors.add(error);
						} else {
							try {
								Date date = Utils.formatDate(masterDateEles.first().text());
								String releasedate = date != null ? date.toLocaleString() : "";
								info.put("releasedate", releasedate);
							} catch (ParseException e) {
								info.put("releasedate", "无法获取发布时间, 原因:" + e.getMessage());
							}
						}
					}

					if (StringUtils.isEmpty(detailConf.getContent())) {
						Map<String, String> error = new HashMap<String, String>();
						error.put("field", "content");
						error.put("msg", "不能为空");
						errors.add(error);
					} else {
						Elements masterContentEles = masterEles.select(detailConf.getContent());
						if (CollectionUtils.isEmpty(masterContentEles)) {
							Map<String, String> error = new HashMap<String, String>();
							error.put("field", "content");
							error.put("msg", "获取主帖内容失败");
							errors.add(error);
						} else {
							String masterContent = masterContentEles.first().text();
							info.put("masterContent", masterContent);
						}
					}
				}
			}

			/*
			 * 回复
			 */
			if (!StringUtils.isEmpty(detailConf.getReply())) {
				Elements replyEles = document.select(detailConf.getReply());
				if (CollectionUtils.isEmpty(replyEles)) {
					Map<String, String> error = new HashMap<String, String>();
					error.put("field", "reply");
					error.put("msg", "获取回复信息失败");
					errors.add(error);
				} else {
					info.put("curReplyNum", replyEles.size());
					List<Map<String, String>> replies = new ArrayList<Map<String, String>>();
					int count1 = 0, count2 = 0, count3 = 0;
					for (int i = 1; i < replyEles.size(); i++) {
						Element replyEle = replyEles.get(i);
						Map<String, String> reply = new HashMap<String, String>();
						Elements replyAuthorEles = replyEle.select(detailConf.getReplyAuthor());
						if (CollectionUtils.isEmpty(replyAuthorEles)) {
							count1++;
						} else {
							reply.put("replyAuthor", replyAuthorEles.first().text());
						}

						Elements replyDateEles = replyEle.select(detailConf.getReplyDate());
						if (CollectionUtils.isEmpty(replyDateEles)) {
							count2++;
						} else {
							try {
								if (Utils.formatDate(replyDateEles.first().text()) == null) {
									reply.put("replyDate", "");
								} else {
									reply.put("replyDate",
									        Utils.formatDate(replyDateEles.first().text())
									                .toString());
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}

						Elements replyContentEles = replyEle.select(detailConf.getReplyContent());
						if (CollectionUtils.isEmpty(replyContentEles)) {
							count3++;
						} else {
							reply.put("replyContent", replyContentEles.first().text());
						}
						replies.add(reply);
					}

					if (count1 > 10) {
						Map<String, String> error = new HashMap<String, String>();
						error.put("field", "replyAuthorerror");
						error.put("msg", "获取回复人失败");
						errors.add(error);
					}
					if (count2 > 10) {
						Map<String, String> error = new HashMap<String, String>();
						error.put("field", "replyDate");
						error.put("msg", "无法从" + detailConf.getReplyDate() + "获取回复时间");
						errors.add(error);
					}
					if (count3 > 10) {
						Map<String, String> error = new HashMap<String, String>();
						error.put("field", "replyContent");
						error.put("msg", "获取回复内容失败");
						errors.add(error);
					}
					// info.put("回复", replies);
				}
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("errors", errors);
		map.put("info", info);
		return map;
	}
}
