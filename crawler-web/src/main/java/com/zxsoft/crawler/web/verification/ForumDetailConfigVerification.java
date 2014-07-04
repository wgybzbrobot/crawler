package com.zxsoft.crawler.web.verification;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.zxsoft.crawler.web.download.AjaxLoader;
import com.zxsoft.crawler.web.download.JsoupLoader;
import com.zxsoft.crawler.web.model.ForumDetailConf;
import com.zxsoft.crawler.web.model.Reply;
import com.zxsoft.crawler.web.model.ThreadInfo;
import com.zxsoft.framework.utils.PageHelper;
import com.zxsoft.framework.utils.Utils;

@Service
public class ForumDetailConfigVerification {

	public Map<String, Object> verify(String testUrl, boolean ajax, ForumDetailConf forumDetailConf) {

		Map<String, Object> map = new HashMap<String, Object>();
		List<FieldError> errors = new LinkedList<FieldError>();
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("测试页URL", testUrl);
		Document document = null;
		/*if (ajax) {
			AjaxLoader loader = new AjaxLoader();
			try {
	            document = loader.load(testUrl);
            } catch (FailingHttpStatusCodeException | IOException e) {
	            e.printStackTrace();
            }
		} else {
			JsoupLoader loader = new JsoupLoader();
			document = loader.load(testUrl);
		}*/
		
		JsoupLoader loader = new JsoupLoader();
		document = loader.load(testUrl);
		
		try {
	        String host = Utils.getHost(testUrl);
        } catch (MalformedURLException e) {
	        e.printStackTrace();
        }

		if (document == null) {
			FieldError error = new FieldError("conf", "forumDetailConf.testUrl", "连接" + testUrl + "失败");
			errors.add(error);
		} else {
			Elements replyNumEles = document.select(forumDetailConf.getReplyNum());
			if (CollectionUtils.isEmpty(replyNumEles)) {
				FieldError error = new FieldError("conf", "forumDetailConf.replyNum", "无法从" + forumDetailConf.getReplyNum() + "获取回复数, 请检查是否正确.");
				errors.add(error);
			} else {
				String replyNum = String.valueOf(Utils.extractNum(replyNumEles.first().text()));
				info.put("回复数", replyNum);
			}
			
			if (!StringUtils.isEmpty(forumDetailConf.getReviewNum())) {
				Elements reviewNumEles = document.select(forumDetailConf.getReviewNum());
				if (CollectionUtils.isEmpty(reviewNumEles)) {
					FieldError error = new FieldError("conf", "forumDetailConf.reviewNum", "无法从" + forumDetailConf.getReviewNum() + "获取浏览数, 请检查是否正确.");
					errors.add(error);
				} else {
					String replyNum = String.valueOf(Utils.extractNum(reviewNumEles.first().text()));
					info.put("浏览数", replyNum);
				}
			}
			
			if (!StringUtils.isEmpty(forumDetailConf.getForwardNum())) {
				Elements forwardNumEles = document.select(forumDetailConf.getForwardNum());
				if (CollectionUtils.isEmpty(forwardNumEles)) {
					FieldError error = new FieldError("conf", "forumDetailConf.forwardNum", "无法从" + forumDetailConf.getForwardNum() + "获取转帖数, 请检查是否正确.");
					errors.add(error);
				} else {
					String replyNum = String.valueOf(Utils.extractNum(forwardNumEles.first().text()));
					info.put("转帖数", replyNum);
				}
			}
			
			Element pagebar = PageHelper.getPageBar(document);
			String pagebarText = pagebar == null ? "" : pagebar.html();
			info.put("分页栏", pagebarText);

			Elements masterEles = document.select(forumDetailConf.getMaster());
			if (CollectionUtils.isEmpty(masterEles)) {
				FieldError error = new FieldError("conf", "forumDetailConf.master", "无法从" + forumDetailConf.getMaster() + "获取主帖信息, 请检查是否正确.");
				errors.add(error);
			} else {
				Elements masterAuthorEles = masterEles.select(forumDetailConf.getMasterAuthor());
				if (CollectionUtils.isEmpty(masterAuthorEles)) {
					FieldError error = new FieldError("conf", "forumDetailConf.masterAuthor", "无法从" + forumDetailConf.getMasterAuthor() + "获取楼主, 请检查是否正确.");
					errors.add(error);
				} else {
					String masterAuthor = masterAuthorEles.first().text();
					info.put("楼主", masterAuthor);
				}
				
				Elements masterDateEles = masterEles.select(forumDetailConf.getMasterDate());
				if (CollectionUtils.isEmpty(masterDateEles)) {		
					FieldError error = new FieldError("conf", "forumDetailConf.masterDate", "无法从" + forumDetailConf.getMasterDate() + "获取发布时间, 请检查是否正确.");
					errors.add(error);
				} else {
					Date date = Utils.formatDate(masterDateEles.first().text());
					String releasedate = date != null ? date.toString() : "";
					info.put("发布时间", releasedate);
				}
				
				Elements masterContentEles = masterEles.select(forumDetailConf.getMasterContent());
				if (CollectionUtils.isEmpty(masterContentEles)) {
					FieldError error = new FieldError("conf", "forumDetailConf.masterContent", "无法从" + forumDetailConf.getMasterContent() + "获取主帖内容, 请检查是否正确.");
					errors.add(error);
				} else {
					String masterContent = masterContentEles.first().text();
					info.put("主帖内容", masterContent);
				}
			}
			
			Elements replyEles = document.select(forumDetailConf.getReply());
			if (CollectionUtils.isEmpty(replyEles)) {
				FieldError error = new FieldError("conf", "forumDetailConf.reply", "无法从" + forumDetailConf.getReply() + "获取回复信息, 请检查是否正确.");
				errors.add(error);
			} else {
				info.put("当前页回复数(可能包含主帖)", replyEles.size());
				List<Reply> replies = new ArrayList<Reply>();
				for (int i = 1; i <  replyEles.size(); i++) {
					Element replyEle = replyEles.get(i);
					Reply reply = new Reply();
					
					Elements replyAuthorEles = replyEle.select(forumDetailConf.getReplyAuthor());
					if (CollectionUtils.isEmpty(replyAuthorEles)) {
						FieldError error = new FieldError("conf", "forumDetailConf.replyAuthor", "无法从" + forumDetailConf.getReplyAuthor() + "获取回复人, 请检查是否正确.");
						errors.add(error);
					} else {
						reply.setAuthor(replyAuthorEles.first().text());
					}
					
					Elements replyDateEles = replyEle.select(forumDetailConf.getReplyDate());
					if (CollectionUtils.isEmpty(replyDateEles)) {
						FieldError error = new FieldError("conf", "forumDetailConf.replyDate", "无法从" + forumDetailConf.getReplyDate() + "获取回复时间, 请检查是否正确.");
						errors.add(error);
					} else {
						if (Utils.formatDate(replyDateEles.first().text()) == null) {
							reply.setReleaseDate(null);
//							errors.add(new FieldError("forumDetailConf", "replyDate", ""));
						} else {
							reply.setReleaseDate(Utils.formatDate(replyDateEles.first().text()).toString());
						}
					}
					
					Elements replyContentEles = replyEle.select(forumDetailConf.getReplyContent());
					if (CollectionUtils.isEmpty(replyContentEles)) {
						FieldError error = new FieldError("conf", "forumDetailConf.replyContent", "无法从" + forumDetailConf.getReplyContent() + "获取回复内容, 请检查是否正确.");
						errors.add(error);
					} else {
						reply.setContent(replyContentEles.first().text());
					}
//					replies.add(reply);
				}
//				info.put("回复帖子", replies);
			}
			
		}
		
		map.put("errors", errors);
		map.put("info", info);
		return map;
	}

}
