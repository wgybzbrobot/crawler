package com.zxsoft.crawler.protocols.http.httpclient;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.util.URLNormalizer;
import com.zxsoft.crawler.util.Utils;

/**
 * 获取分页信息，包括上一页、下一页、末页
 * 
 * @return url
 */
@Component
public final class HttpClientPageHelper {

	public enum PageType {
		CURRENT_PAGE,
		PREV_PAGE,
		NEXT_PAGE,
		LAST_PAGE
	}
	@Autowired
	private HttpFetcher httpFetcher;
	
	private String currentUrl;
	private String currentText;
	
	private ProtocolOutput load(Element ele) {
		String url = ele.absUrl("href");
		currentText = ele.text();
			if (StringUtils.isEmpty(url))
				return null;
			url = URLNormalizer.normalize(url);
		return httpFetcher.fetch(url, false);
	}

	public ProtocolOutput loadNextPage(int pageNum, Document currentDoc)  {
		currentUrl = currentDoc.location();
		Elements elements = currentDoc.select("a:matchesOwn(下一页|下页|下一页>)");
		if (!CollectionUtils.isEmpty(elements)) {
			return load(elements.first());
		} else {
			/*
			 * Find the position of current page url from page bar, get next
			 * achor as the next page url. However, there is a problem. It's not
			 * very accurate, some url cannot find from page bar, because it
			 * changed when load it.
			 */
			Element pagebar = HttpClientPageHelper.getPageBar(currentDoc);
			if (pagebar != null) {
				Elements achors = pagebar.getElementsByTag("a");
				if (pagebar != null || !CollectionUtils.isEmpty(achors)) {
					for (int i = 0; i < achors.size(); i++) {
						if (Utils.isNum(achors.get(i).text())
								&& Integer.valueOf(achors.get(i).text().trim()) == pageNum + 1) {
							return load(achors.get(i));
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 加载上一页
	 */
	public ProtocolOutput loadPrevPage(Document currentDoc, boolean ajax)  {
		currentUrl = currentDoc.location();
		Elements elements = currentDoc.select("a:matchesOwn(上一页|上页|<上一页)");
		if (CollectionUtils.isEmpty(elements)) {
			return null;
		}
		return load(elements.first());
	}

	public ProtocolOutput loadLastPage(Document currentDoc, boolean ajax) {
		currentUrl = currentDoc.location();
		Elements lastEles = currentDoc.select("a:matchesOwn(尾页|末页|最后一页)");
		if (!CollectionUtils.isEmpty(lastEles)) {
			return load(lastEles.first());
		}

		// 1. get all links from page bar
		Element pagebar = HttpClientPageHelper.getPageBar(currentDoc);
		if (pagebar == null)
			return null;
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

		return load(el);
	}

	/**
	 * Get Page Bar of current page[document] 
	 * @return page bar element.
	 */
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
					        || !CollectionUtils.isEmpty(siblingEles = element.parent()
					                .siblingElements())) {
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
