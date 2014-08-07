package com.zxsoft.crawler.web.verification;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.zxsoft.crawler.parse.ParseTool;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.util.Utils;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PageHelper;
import com.zxsoft.crawler.web.model.ThreadInfo;

@Service
public class ListConfigVerification extends ParseTool {

	@Autowired
	private ApplicationContext ctx;

	public Map<String, Object> verify(ListConf listConf) {
		super.init(ctx);
		Map<String, Object> map = new HashMap<String, Object>();
		List<ThreadInfo> list = new ArrayList<ThreadInfo>();
		String pageStr = "";
		ProtocolOutput protocolOutput = fetch(listConf.getUrl(), listConf.isAjax());
		ObjectError error = null;
		Document document = null;
		if (protocolOutput == null || !protocolOutput.getStatus().isSuccess()) {
			error = new FieldError("listConf", "url", "连接" + listConf.getUrl() + "失败");
		} else {
			document = protocolOutput.getDocument();
			
			if (StringUtils.isEmpty(listConf.getLinedom())) {
				error = new FieldError("listConf", "listdomerror", "必填");
				map.put("error", error);
				return map;
			}
			Elements elements = document.select(listConf.getListdom());
			if (CollectionUtils.isEmpty(elements)) {
				error = new FieldError("listConf", "listdom", "无法从" + listConf.getListdom()
				        + "获取列表信息, 请检查是否正确.");
				map.put("error", error);
				return map;
			}

			Element listElement = elements.first();
			Elements lineElements = listElement.select(listConf.getLinedom());
			if (CollectionUtils.isEmpty(lineElements) || lineElements.size() < 3) {
				error = new FieldError("listConf", "linedomerror", "无法从" + listConf.getLinedom()
				        + "获取列表行信息, 请检查是否正确.");
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
					error = new FieldError("listConf", "urldom", "无法从" + listConf.getUrldom()
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
						error = new FieldError("listConf", "datedom", "无法从"
						        + listConf.getDatedom() + "获取发布日期, 请检查是否正确.");
						map.put("error", error);
						return map;
					}
					try {
						releasedate = Utils.formatDate(dateElements.first().text());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				ThreadInfo info = new ThreadInfo(url, title, releasedate);
				list.add(info);
			}
			Element pagebar;
			try {
				pagebar = PageHelper.getPageBar(document);
				pageStr = pagebar.html();
			} catch (PageBarNotFoundException e) {

				e.printStackTrace();
			}
		}

		map.put("error", error);
		map.put("list", list);
		map.put("pagebar", pageStr);
		return map;
	}

}
