package com.zxsoft.crawler.protocols.http;

import java.io.IOException;
import java.util.regex.Matcher;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.zxsoft.crawler.util.Utils;

/**
 * Smart Loader contains Jsoup Loader and HtmlUnit Loader, do not care whether
 * the url is loaded by ajax.
 */
public class SmartLoader {

	/**
	 * 加载url
	 * 
	 * @throws IOException
	 */
	public Document load(String url) throws IOException {
		if (StringUtils.isEmpty(url)) return null;
		JsoupLoader jsoupLoader = new JsoupLoader();
		return jsoupLoader.load(url);
	}
	public Document load(String url, String dom) throws IOException {
		JsoupLoader jsoupLoader = new JsoupLoader();
		Document document = jsoupLoader.load(url);

		if (document != null) {
			Elements elements = document.select(dom);
			if (CollectionUtils.isEmpty(elements) || elements.size() < 25) {
				AjaxLoader ajaxLoader = new AjaxLoader();
				document = ajaxLoader.load(url);
			}
		}
		return document;
	}

	/**
	 * @param currentDoc
	 *            Current page document
	 * @return Next page document.
	 * @throws IOException
	 */
	public Document loadNexPage(int pageNum, Document currentDoc) throws IOException {
		Assert.notNull(currentDoc);
		// 1. jsoup load first.
		JsoupLoader loader = new JsoupLoader();
		Document nextDoc = loader.loadNextPage(pageNum, currentDoc);

		// 2. if jsoup load fail, use htmlunit to load.
		if (nextDoc == null) {
			AjaxLoader aloader = new AjaxLoader();
			currentDoc = aloader.loadNextPage(pageNum, currentDoc);
		}
		return nextDoc;
	}

	
	public Document loadLastPage(Document currentDoc) throws IOException {
		Assert.notNull(currentDoc);
		Element pagebar = PageHelper.getPageBar(currentDoc);
		if (pagebar == null) {
			return null;
		}
		String url = "";
		Elements elements = pagebar.select("a:matchesOwn(尾页|末页|最后一页|>\\|)");
		if (!CollectionUtils.isEmpty(elements)) {
			url = elements.first().absUrl("href");
		} else { // Get max num in all links, that is last page
			Elements links = pagebar.getElementsByTag("a");
			if (CollectionUtils.isEmpty(links)) {
				return null;
			}
			int i = 1;
			Element el = null;
			for (Element ele : links) {
				String v = ele.text();
				if (Utils.isNum(v) && Integer.valueOf(v) > i) { // get max num
					i = Integer.valueOf(v);
					el = ele;
				}
			}
			if (el != null) {
				url = el.absUrl("href");
			}
		}
		return load(url);
	}

}
