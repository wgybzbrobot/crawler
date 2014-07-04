package com.zxsoft.crawler.web.verification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.zxsoft.crawler.web.model.ListConf;
import com.zxsoft.crawler.web.model.ThreadInfo;
import com.zxsoft.framework.utils.PageHelper;
import com.zxsoft.framework.utils.Utils;

@Service
public class ListConfigVerification {

	public Map<String, Object> verify(ListConf listConf) {

		Map<String, Object> map = new HashMap<String, Object>();
		List<ThreadInfo> list = new ArrayList<ThreadInfo>();

		ObjectError error = null;

		Document document = null;
		
	/*	if (listConf.isAjax()) {
			AjaxLoader loader = new AjaxLoader();
			try {
				document = loader.load(listConf.getUrl());
			} catch (FailingHttpStatusCodeException | IOException e) {
				e.printStackTrace();
			}
		} else {
			JsoupLoader loader = new JsoupLoader();
			document = loader.load(listConf.getUrl());
		}*/
		JsoupLoader loader = new JsoupLoader();
		document = loader.load(listConf.getUrl());
		String pageStr = "";
		
		if (document == null) {
			error = new FieldError("conf", "listConf.url", "连接" + listConf.getUrl() + "失败");
		} else {
				Elements elements = document.select(listConf.getListdom());
				if (CollectionUtils.isEmpty(elements)) {
					error = new FieldError("conf", "listConf.listdom", "无法从" + listConf.getListdom() + "获取列表信息, 请检查是否正确.");
					map.put("error", error);
					return map;
				}

				Element listElement = elements.first();
				Elements lineElements = listElement.select(listConf.getLinedom());
				if (CollectionUtils.isEmpty(lineElements) || lineElements.size() < 3) {
					error = new FieldError("conf", "listConf.linedom", "无法从" + listConf.getLinedom() + "获取列表行信息, 请检查是否正确.");
					map.put("error", error);
					return map;
				}

				int i = 0;
				for (Element lineEle : lineElements) {
					i++;
					if (CollectionUtils.isEmpty(lineEle.select(listConf.getUrldom()))) {
						if (i < 10) {
							continue;
						}
						error = new FieldError("conf", "listConf.urldom", "无法从" + listConf.getUrldom()
						        + "获取详细页URL链接, 请检查是否正确.");
						map.put("error", error);
						return map;
					}

					Element urlEle = lineEle.select(listConf.getUrldom()).first();
					String url = urlEle.absUrl("href");
					String title = urlEle.text();
					Date releasedate = null;
					if (!StringUtils.isEmpty(listConf.getDatedom())) {
						Elements dateElements = lineEle.select(listConf.getDatedom());
						if (CollectionUtils.isEmpty(dateElements)) {
							error = new FieldError("conf", "listConf.datedom", "无法从" + listConf.getDatedom()
							        + "获取发布日期, 请检查是否正确.");
							map.put("error", error);
							return map;
						}
						releasedate = Utils.formatDate(dateElements.first().text());
					}
					ThreadInfo info = new ThreadInfo(url, title, releasedate);
					list.add(info);
				}
				Element pagebar = PageHelper.getPageBar(document);
				if (pagebar != null) {
					pageStr = pagebar.html();
				}
		}

		map.put("error", error);
		map.put("list", list);
		map.put("pagebar", pageStr);
		return map;
	}
	
}
