package com.zxsoft.crawler.web.verification;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.StringUtils;

import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.parse.ParseTool;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.Utils;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PageHelper;
import com.zxsoft.crawler.web.model.ThreadInfo;

public class ListConfigVerification extends ParseTool {

	private static Logger LOG = LoggerFactory.getLogger(ListConfigVerification.class);
	
	public Map<String, Object> verify(ConfList listConf, String keyword) {

		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String> errors = new HashMap<String, String>();

		if (StringUtils.isEmpty(listConf.getUrl())) {
			errors.put("urlerror", "必填");
			map.put("errors", errors);
			return map;
		}

		List<ThreadInfo> list = new ArrayList<ThreadInfo>();
		String pageStr = "", testurl = listConf.getUrl();
		if ("search".equals(listConf.getCategory())) {
			try {
	            keyword = URLEncoder.encode(keyword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
            }
			testurl = listConf.getUrl().replace("%s", keyword);
		}
		WebPage page = new WebPage(testurl, false);
		ProtocolOutput protocolOutput = fetch(page);
		Document document = null;

		if (protocolOutput == null || !protocolOutput.getStatus().isSuccess()) {
			errors.put("urlerror", "连接失败");

		} else {
			document = protocolOutput.getDocument();
			
			if (StringUtils.isEmpty(listConf.getListdom())) {
				errors.put("listdomerror", "必填");
			} else {//document.select("form#moderate  table:gt(1)");
				Elements elements = document.select(listConf.getListdom());
				if (CollectionUtils.isEmpty(elements)) {
					errors.put("listdom", "获取列表失败");
				} else {
					Element listElement = elements.first();
					if (StringUtils.isEmpty(listConf.getLinedom())) {
						errors.put("linedom", "必填");
					} else {
						
						try {
							Element pagebar = PageHelper.getPageBar(listElement);
							pageStr = pagebar.html();
						} catch (NullPointerException | PageBarNotFoundException e) {
							e.printStackTrace();
						}
						
						Elements lineElements = listElement.select(listConf.getLinedom());
						if (CollectionUtils.isEmpty(lineElements) || lineElements.size() < 3) {
							errors.put("linedom", "获取列表行失败");
							LOG.info(listElement.html());
						} else {
							int i = 0;
							int updateErrorCount = 0;
							int urlErrorCount = 0;
							for (Element lineEle : lineElements) {
								i++;
								if (CollectionUtils.isEmpty(lineEle.select(listConf.getUrldom()))) {
									if (i < 10) {
										continue;
									}
									urlErrorCount++;
								}

								Element urlEle = lineEle.select(listConf.getUrldom()).first();
								if (urlEle == null) {
									continue;
								}
								String url = urlEle.absUrl("href");
								String title = urlEle.text();
								Date update = null;
								if (!StringUtils.isEmpty(listConf.getUpdatedom())) {
									Elements dateElements = lineEle.select(listConf.getUpdatedom());
									if (CollectionUtils.isEmpty(dateElements)) {
										updateErrorCount++;
									} else {
										try {
											update = Utils.formatDate(dateElements.first()
											        .text());
										} catch (ParseException e) {
											e.printStackTrace();
										}
									}
								}
								
								
								ThreadInfo info = new ThreadInfo(url, title, update);

								if (!StringUtils.isEmpty(listConf.getSynopsisdom())) {
									Elements synoEles = lineEle.select(listConf.getSynopsisdom());
									if (!CollectionUtils.isEmpty(synoEles)) {
										info.setSynopsis(synoEles.first().text());
									}
								}
								
								list.add(info);
							}
							if (updateErrorCount > 10) {
								errors.put("updatedom", "获取更新时间失败");
							}
							if (urlErrorCount > 10) {
								errors.put("urldom", "获取详细页URL失败");
							}
						}
					}
				}
			}
			
			if (StringUtils.isEmpty(pageStr)) {
				try {
					Element pagebar = PageHelper.getPageBar(document);
					pageStr = pagebar.html();
				} catch (NullPointerException | PageBarNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		map.put("errors", errors);
		map.put("list", list);
		if (StringUtils.isEmpty(pageStr))
			pageStr = "没有找到";
		map.put("pagebar", pageStr);
		return map;
	}

}
