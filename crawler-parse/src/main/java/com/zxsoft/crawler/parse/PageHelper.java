package com.zxsoft.crawler.parse;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.zxsoft.crawler.protocols.http.AjaxLoader;
import com.zxsoft.crawler.protocols.http.JsoupLoader;
import com.zxsoft.crawler.util.Utils;

/**
 * 获取分页信息，包括上一页、下一页、末页
 * 
 * @return url
 */
public final class PageHelper {

	/**
	 * 获取上一页
	 * 
	 * @param pagebar
	 *            分页栏
	 * @param currentPageText
	 *            text in achor
	 */
	public static Element getPrePage(Element pagebar, String currentPageText) {
		if (pagebar == null) {
			return null;
		}

		Elements preEles = pagebar.select("a:matchesOwn(上一页|上页)");
		if (!CollectionUtils.isEmpty(preEles)) {
			return preEles.first();
		}

		Elements links = pagebar.getElementsByTag("a");
		if (CollectionUtils.isEmpty(links) || !Utils.isNum(currentPageText)) {
			return null;
		}
		int preNum = Integer.valueOf(currentPageText) - 1;
		for (Element ele : links) {
			String v = ele.text();
			if (Utils.isNum(v) && Utils.extractNum(v) == preNum) {
				return ele;
			}
		}
		return null;
	}

	public static Document getPrePage(Document currentDoc) {
		return null;
	}

	/**
	 * 获取下一页
	 * 
	 * @param pagebar
	 *            分页栏
	 * @param currentPageText
	 *            text in achor
	 */
	public static Element getNextPage(Element pagebar, String currentPageText) {
		Elements preEles = pagebar.select("a:matchesOwn(下一页|下页)");
		if (!CollectionUtils.isEmpty(preEles)) {
			return preEles.first();
		}

		Elements links = pagebar.getElementsByTag("a");
		if (CollectionUtils.isEmpty(links) || !Utils.isNum(currentPageText)) {
			return null;
		}
		int nextNum = Integer.valueOf(currentPageText) + 1;
		for (Element ele : links) {
			String v = ele.text();
			if (Utils.isNum(v) && Utils.extractNum(v) == nextNum) {
				return ele;
			}
		}
		return null;
	}

	public static Document getNextPage(int pageNum, Document currentDoc) throws IOException {
		Assert.notNull(currentDoc);
		JsoupLoader jsoupLoader = new JsoupLoader();
		Document nextDoc = null;

		// 1. jsoup load
		nextDoc = jsoupLoader.loadNextPage(pageNum, currentDoc);

		// 2. ajax load
		if (nextDoc == null) {
			AjaxLoader aloader = new AjaxLoader();
			nextDoc = aloader.loadNextPage(pageNum, currentDoc);
		}
		// 3.
		if (nextDoc == null) {

		}

		return nextDoc;
	}

	public static Element getLastPage(Element pagebar) {
		Elements lastEles = pagebar.select("a:matchesOwn(尾页|末页|最后一页)");
		if (!CollectionUtils.isEmpty(lastEles)) {
			return lastEles.first();
		}
		
		// 1. get all links from page bar
		Elements links = pagebar.getElementsByTag("a");
		if (CollectionUtils.isEmpty(links)) {
			return null;
		}

		// 2. get max num or contains something in all links, that is last page
		int i = 1;
		Element el = null;
		for (Element ele : links) {
			String v = ele.text();
			if ("18255266882".equals(v)) {
				System.out.println(ele);
			}
			if (Utils.isNum(v) && Integer.valueOf(v) > i) { // get max num
				i = Integer.valueOf(v);
				el = ele;
			}
		}

		return el;
	}

	public static Element getPageBar(Document document) {
		Assert.notNull(document);
		Elements eles = document.select("a:matches(上一页|上页|<上一页|下一页|下页|下一页>|尾页|末页)");
		if (!CollectionUtils.isEmpty(eles)) {
			Elements siblingEles = eles.first().siblingElements();
			Element parentEle = eles.first().parent();

			if (!CollectionUtils.isEmpty(siblingEles)
			        || !CollectionUtils.isEmpty(siblingEles = parentEle.siblingElements())) {
				float sum = siblingEles.size() + 1, count = 0;
				for (Element ele : siblingEles) {
					if (!CollectionUtils.isEmpty(ele.getElementsByTag("a"))) { 
						count++;
					}
				}
				if (Math.round(count / sum) == 1) {
					return parentEle.parent();
				} else {
					return parentEle;
				}
			}
		} else { // get pagebar by 1,2,3...
			eles = document.getElementsByTag("a");
			if (CollectionUtils.isEmpty(eles))
				return null;
			for (Element element : eles) {
				if (Utils.isNum(element.text())) {
					Elements siblingEles = element.siblingElements();
					if (!CollectionUtils.isEmpty(siblingEles)
					        || !CollectionUtils.isEmpty(siblingEles = element.parent().siblingElements())) {
						float sum = siblingEles.size() + 1, count = 0; 
						int num = 1;
						for (Element ele : siblingEles) {
							if (!CollectionUtils.isEmpty(ele.getElementsByTag("a"))) {
								count++;
								String str = ele.getElementsByTag("a").first().text();
								if (Utils.isNum(str) && Integer.valueOf(str) < 1000) 
									num++;
							}
						}

						if (Math.round(count / sum) == 1 && num > 1) {
							return siblingEles.first().parent();
						}
					}
				}
			}
		}

		return null;
	}
}
