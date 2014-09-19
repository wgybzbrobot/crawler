package com.zxsoft.crawler.web.verification;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.StringUtils;

import com.zxsoft.crawler.parse.ParseTool;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.util.CrawlerConfiguration;
import com.zxsoft.crawler.util.Utils;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PageHelper;

public class DetailConfigVerification extends ParseTool {

	private static Logger LOG = LoggerFactory.getLogger(DetailConfigVerification.class);
	
	public DetailConfigVerification() {
//		Configuration conf = CrawlerConfiguration.create();
//		setConf(conf);
	}
	
	public Map<String, Object> verify(DetailConf detailConf) {
		Map<String, Object> info = new LinkedHashMap<String, Object>();
		info.put("测试页URL", detailConf.getTestUrl());
		List<Map<String, String>> errors = new LinkedList<Map<String, String>>();
		ProtocolOutput protocolOutput = fetch(detailConf.getTestUrl(), false);
		Document document = null;
		if (protocolOutput == null || !protocolOutput.getStatus().isSuccess()) {
			Map<String, String> error = new HashMap<String, String>();
			error.put("field", "urlerror");
			error.put("msg", "连接" + detailConf.getTestUrl() + "失败");
			errors.add(error);
		} else {
			document = protocolOutput.getDocument();
			if (!StringUtils.isEmpty(detailConf.getReplyNum())) {
				Elements replyNumEles = document.select(detailConf.getReplyNum());
				if (CollectionUtils.isEmpty(replyNumEles)) {
					Map<String, String> error = new HashMap<String, String>();
					error.put("field", "replyerror");
					error.put("msg", "无法从" + detailConf.getReplyNum() + "获取回复数");
					errors.add(error);
				} else {
					String replyNum = String.valueOf(Utils.extractNum(replyNumEles.first().text()));
					info.put("回复数", replyNum);
				}
			}
			
			if (!StringUtils.isEmpty(detailConf.getReviewNum())) {
				Elements reviewNumEles = document.select(detailConf.getReviewNum());
				if (CollectionUtils.isEmpty(reviewNumEles)) {
					Map<String, String> error = new HashMap<String, String>();
					error.put("field", "reviewNumerror");
					error.put("msg", "无法从" + detailConf.getReviewNum() + "获取浏览数");
					errors.add(error);
				} else {
					String replyNum = String.valueOf(Utils.extractNum(reviewNumEles.first().text()));
					info.put("浏览数", replyNum);
				}
			}
			
			if (!StringUtils.isEmpty(detailConf.getForwardNum())) {
				Elements forwardNumEles = document.select(detailConf.getForwardNum());
				if (CollectionUtils.isEmpty(forwardNumEles)) {
					Map<String, String> error = new HashMap<String, String>();
					error.put("field", "forwardNumerror");
					error.put("msg", "无法从" + detailConf.getForwardNum() + "获取转帖数");
					errors.add(error);
				} else {
					String replyNum = String.valueOf(Utils.extractNum(forwardNumEles.first().text()));
					info.put("转帖数", replyNum);
				}
			}
			
			Element pagebar = null;
            try {
	            pagebar = PageHelper.getPageBar(document);
            } catch (PageBarNotFoundException e) {
	            LOG.warn(e.getMessage());
            }
			String pagebarText = pagebar == null ? "" : pagebar.html();
			info.put("分页栏", pagebarText);
			
			/*
			 * 主贴
			 */
			Elements masterEles = document.select(detailConf.getMaster());
			if (CollectionUtils.isEmpty(masterEles)) {
				Map<String, String> error = new HashMap<String, String>();
				error.put("field", "mastererror");
				error.put("msg", "无法从" + detailConf.getMaster() + "获取主帖信息");
				errors.add(error);
			} else {
				if (!StringUtils.isEmpty(detailConf.getAuthor())) {
					Elements masterAuthorEles = masterEles.select(detailConf.getAuthor());
					if (CollectionUtils.isEmpty(masterAuthorEles)) {
						Map<String, String> error = new HashMap<String, String>();
						error.put("field", "authorerror");
						error.put("msg", "无法从" + detailConf.getAuthor() + "获取楼主");
						errors.add(error);
					} else {
						String masterAuthor = masterAuthorEles.first().text();
						info.put("楼主", masterAuthor);
					}
				}
				if (!StringUtils.isEmpty(detailConf.getDate())) {
					Elements masterDateEles = masterEles.select(detailConf.getDate());
					if (CollectionUtils.isEmpty(masterDateEles)) {		
						Map<String, String> error = new HashMap<String, String>();
						error.put("field", "dateerror");
						error.put("msg", "无法从" + detailConf.getDate() + "获取发布时间");
						errors.add(error);
					} else {
	                    try {
	                    	Date date = Utils.formatDate(masterDateEles.first().text());
		                    String releasedate = date != null ? date.toLocaleString() : "";
		                    info.put("发布时间", releasedate);
	                    } catch (ParseException e) {
	                    	info.put("发布时间", "无法获取发布时间, 原因:" + e.getMessage());
	                    }
					}
				}
				
				if (StringUtils.isEmpty(detailConf.getContent())) {
					Map<String, String> error = new HashMap<String, String>();
					error.put("field", "contenterror");
					error.put("msg", "不能为空");
					errors.add(error);
				} else {
					Elements masterContentEles = masterEles.select(detailConf.getContent());
					if (CollectionUtils.isEmpty(masterContentEles)) {
						Map<String, String> error = new HashMap<String, String>();
						error.put("field", "contenterror");
						error.put("msg", "无法从" + detailConf.getContent() + "获取主帖内容");
						errors.add(error);
					} else {
						String masterContent = masterContentEles.first().text();
						info.put("主帖内容", masterContent);
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
					error.put("field", "replyerror");
					error.put("msg", "无法从" + detailConf.getReply() + "获取回复信息");
					errors.add(error);
				} else {
					info.put("当前页回复数(可能包含主帖)", replyEles.size());
					List<Map<String, String>> replies = new ArrayList<Map<String,String>>();
					int count1 = 0, count2 = 0, count3 = 0;
					for (int i = 1; i <  replyEles.size(); i++) {
						Element replyEle = replyEles.get(i);
						Map<String, String> reply = new HashMap<String, String>();
						Elements replyAuthorEles = replyEle.select(detailConf.getReplyAuthor());
						if (CollectionUtils.isEmpty(replyAuthorEles)) {
							count1 ++;
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
		                        	reply.put("replyDate", Utils.formatDate(replyDateEles.first().text()).toString());
		                        }
	                        } catch (ParseException e) {
		                        e.printStackTrace();
	                        }
						}
						
						Elements replyContentEles = replyEle.select(detailConf.getReplyContent());
						if (CollectionUtils.isEmpty(replyContentEles)) {
							count3++;
						} else {
							reply.put("replyContent",replyContentEles.first().text());
						}
						replies.add(reply);
					}
					
					if (count1 > 10) {
						Map<String, String> error = new HashMap<String, String>();
						error.put("field", "replyAuthorerror");
						error.put("msg", "无法从" + detailConf.getReplyAuthor() + "获取回复人");
						errors.add(error);
					}
					if (count2 > 10) {
						Map<String, String> error = new HashMap<String, String>();
						error.put("field", "replyDateerror");
						error.put("msg", "无法从" + detailConf.getReplyDate() + "获取回复时间");
						errors.add(error);
					}
					if (count3 > 10) {
						Map<String, String> error = new HashMap<String, String>();
						error.put("field", "replyContenterror");
						error.put("msg", "无法从" + detailConf.getReplyContent() + "获取回复内容");
						errors.add(error);
					}
//					info.put("回复", replies);
				}
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("errors", errors);
		map.put("info", info);
		return map;
	}
}
