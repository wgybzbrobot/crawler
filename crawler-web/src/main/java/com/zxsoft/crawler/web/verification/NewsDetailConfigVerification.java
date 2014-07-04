package com.zxsoft.crawler.web.verification;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;

import com.zxsoft.crawler.web.download.JsoupLoader;
import com.zxsoft.crawler.web.model.NewsDetailConf;
import com.zxsoft.framework.utils.Utils;

@Service
public class NewsDetailConfigVerification {

	public Map<String, Object> verify(String testUrl, NewsDetailConf newsDetailConf) {

		Map<String, Object> map = new HashMap<String, Object>();
		List<FieldError> errors = new LinkedList<FieldError>();
		Map<String, Object> info = new HashMap<String, Object>();

		Document document = null;
		JsoupLoader loader = new JsoupLoader();
		document = loader.load(testUrl);

		try {
			String host = Utils.getHost(testUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		if (document == null) {
			FieldError error = new FieldError("detailConf.newsDetailConf", "testUrl", "连接" + testUrl + "失败");
			errors.add(error);
		} else {
			Elements replyNumEles = document.select(newsDetailConf.getReplyNum());
			if (CollectionUtils.isEmpty(replyNumEles)) {
				FieldError error = new FieldError("detailConf.newsDetailConf", "replyNum", "无法从" + newsDetailConf.getReplyNum()
				        + "获取回复数, 请检查是否正确.");
				errors.add(error);
			} else {
				String replyNum = String.valueOf(Utils.extractNum(replyNumEles.first().text()));
				info.put("回复数", replyNum);
			}

			if (!StringUtils.isEmpty(newsDetailConf.getReviewNum())) {
				Elements reviewNumEles = document.select(newsDetailConf.getReviewNum());
				if (CollectionUtils.isEmpty(reviewNumEles)) {
					FieldError error = new FieldError("detailConf.newsDetailConf", "reviewNum", "无法从"
					        + newsDetailConf.getReviewNum() + "获取浏览数, 请检查是否正确.");
					errors.add(error);
				} else {
					String replyNum = String.valueOf(Utils.extractNum(reviewNumEles.first().text()));
					info.put("浏览数", replyNum);
				}
			}

			if (!StringUtils.isEmpty(newsDetailConf.getForwardNum())) {
				Elements forwardNumEles = document.select(newsDetailConf.getForwardNum());
				if (CollectionUtils.isEmpty(forwardNumEles)) {
					FieldError error = new FieldError("detailConf.newsDetailConf", "forwardNum", "无法从"
					        + newsDetailConf.getForwardNum() + "获取转帖数, 请检查是否正确.");
					errors.add(error);
				} else {
					String replyNum = String.valueOf(Utils.extractNum(forwardNumEles.first().text()));
					info.put("转帖数", replyNum);
				}
			}

			Elements masterAuthorEles = document.select(newsDetailConf.getAuthor());
			if (CollectionUtils.isEmpty(masterAuthorEles)) {
				FieldError error = new FieldError("detailConf.newsDetailConf", "author", "无法从" + newsDetailConf.getAuthor()
				        + "获取楼主, 请检查是否正确.");
				errors.add(error);
			} else {
				String masterAuthor = masterAuthorEles.first().text();
				info.put("楼主", masterAuthor);
			}

			Elements masterDateEles = document.select(newsDetailConf.getReleaseDate());
			if (CollectionUtils.isEmpty(masterDateEles)) {
				FieldError error = new FieldError("detailConf.newsDetailConf", "releaseDate", "无法从"
				        + newsDetailConf.getReleaseDate() + "获取发布时间, 请检查是否正确.");
				errors.add(error);
			} else {
				Date date = Utils.formatDate(masterDateEles.first().text());
				String releasedate = date != null ? date.toString() : "";
				info.put("发布时间", releasedate);
			}

			Elements masterContentEles = document.select(newsDetailConf.getContent());
			if (CollectionUtils.isEmpty(masterContentEles)) {
				FieldError error = new FieldError("detailConf.newsDetailConf", "content", "无法从"
				        + newsDetailConf.getContent() + "获取主帖内容, 请检查是否正确.");
				errors.add(error);
			} else {
				String masterContent = masterContentEles.first().text();
				info.put("主帖内容", masterContent);
			}
		}

		map.put("errors", errors);
		map.put("info", info);
		return map;
	}

}
