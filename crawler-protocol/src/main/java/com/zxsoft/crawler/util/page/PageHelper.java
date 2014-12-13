package com.zxsoft.crawler.util.page;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.Assert;
import org.thinkingcloud.framework.util.CollectionUtils;

import com.zxsoft.crawler.util.Utils;

public class PageHelper {

	private static Logger LOG = LoggerFactory.getLogger(PageHelper.class);

	/**
	 * Get Page Bar of current page[document]
	 * 
	 * @return page bar element.
	 * @throws PageBarNotFoundException
	 */
	public static Element getPageBar(Element document) throws PageBarNotFoundException {
		if (document == null) return null;
		
		Elements eles = document.select("a:matches(上一页|上页|<上一页|下一页|下页|下一页>|尾页|末页)");
		if (!CollectionUtils.isEmpty(eles)) { // bug: if only has '下一页'
			Elements siblingEles = eles.first().siblingElements();
			Element parentEle = eles.first().parent();

			Element element = eles.last()/*first()*/;
			/** test if  element's parent only have one anchor **/
			int count = 0;
			while (count++ < 20) { 
				Element parent = element.parent();
				if (parent.getElementsByTag("a").size() > 1) {
					break;
				}
				element = parent;
			}
			return element.parent();
			
//			if (!CollectionUtils.isEmpty(siblingEles)
//			        || !CollectionUtils.isEmpty(siblingEles = parentEle.siblingElements())) {
//				float sum = siblingEles.size() + 1, count = 0;
//				for (Element ele : siblingEles) {
//					if (!CollectionUtils.isEmpty(ele.getElementsByTag("a"))) {
//						count++;
//					}
//				}
//				if (Math.round(count / sum) == 1) {
//					return parentEle.parent();
//				} else {
//					return parentEle;
//				}
//			}
		} else { // get pagebar by 1,2,3...
			eles = document.getElementsByTag("a");
			if (CollectionUtils.isEmpty(eles))
				return null;
			for (Element element : eles) {
				String eleTxt = element.text();
				if (Utils.isNum(eleTxt)) {

					/** test if  element's parent only have one anchor **/
					int count = 0;
					while (count++ < 20) { 
						Element parent = element.parent();
						if (parent.getElementsByTag("a").size() > 1) {
							break;
						}
						element = parent;
					}
					
					/** Easy Finding **/
					String siblingTxt = "";
					if (element.nextElementSibling() != null) {
						siblingTxt = element.nextElementSibling().text();
						if (Utils.isNum(siblingTxt)
						        /*&& "a".equals(element.nextElementSibling().tagName())*/
						        && Integer.valueOf(eleTxt) + 1 == Integer.valueOf(siblingTxt)) {
							return element.parent();
						}
					} else if (element.previousElementSibling() != null) {
						siblingTxt = element.previousElementSibling().text();
						if (Utils.isNum(siblingTxt)
						        && "a".equals(element.previousElementSibling().tagName())
						        && Integer.valueOf(eleTxt) - 1 == Integer.valueOf(siblingTxt)) {
							return element.parent();
						}
					}

					/** Complex Finding **/
//					Elements siblingEles = element.siblingElements();
//					if (!CollectionUtils.isEmpty(siblingEles)) {
//						siblingEles = siblingEles.parents();
//					} else {
//						siblingEles = siblingEles.parents();
//						if (!CollectionUtils.isEmpty(siblingEles)) {
//							siblingEles = siblingEles.parents();
//						}
//					}
//					float sum = siblingEles.size() + 1, count = 0;
//					int num = 1;
//					SortedSet<Integer> nums = new TreeSet<Integer>();
//					for (Element ele : siblingEles) {
//						if (!CollectionUtils.isEmpty(ele.getElementsByTag("a"))) {
//							count++;
//							String str = ele.getElementsByTag("a").first().text();
//							if (Utils.isNum(str) && Integer.valueOf(str) < 1000) {
//								nums.add(Integer.valueOf(str));
//								num++;
//							}
//						}
//					}
//
//					if (Math.round(count / sum) == 1 && num > 1) {
//						return siblingEles.first().parent();
//					}
				}
			}
		}
		throw new PageBarNotFoundException();
	}

	/**
	 * 计算上一页的url
	 * 
	 * @throws PrevPageNotFoundException
	 */
	public static URL calculatePrevPageUrl(URL currentUrl, URL prevUrl)
	        throws PrevPageNotFoundException {

		String currentUrlStr = currentUrl.toString();
		String testUrlStr = prevUrl.toString();

		Assert.hasLength(testUrlStr);
		Assert.hasLength(currentUrlStr);

		int i = 0;
		while ((testUrlStr.length() > currentUrlStr.length() ? currentUrlStr.length() : testUrlStr
		        .length()) > 0) {
			String test = testUrlStr.substring(i, i + 1);
			String current = currentUrlStr.substring(i, i + 1);
			if (test.equals(current)) {
				testUrlStr = testUrlStr.substring(i + 1);
				currentUrlStr = currentUrlStr.substring(i + 1);
			} else {
				break;
			}
		}

		while (testUrlStr.length() > 0 && currentUrlStr.length() > 0) {
			int j = testUrlStr.length(), k = currentUrlStr.length();
			String test = testUrlStr.substring(j - 1, j);
			String current = currentUrlStr.substring(k - 1, k);
			if (test.equals(current)) {
				testUrlStr = testUrlStr.substring(0, j - 1);
				currentUrlStr = currentUrlStr.substring(0, k - 1);
			} else {
				break;
			}
		}

		if (Utils.isNum(currentUrlStr) && Utils.isNum(testUrlStr)) {
			int prev = Integer.valueOf(testUrlStr);
			int current = Integer.valueOf(currentUrlStr);
			if (prev + 1 == current)
				return prevUrl;
		}
		throw new PrevPageNotFoundException("Preview Page Not Found");
	}

	public static URL calculatePrevPageUrl(Document currentDoc) throws PrevPageNotFoundException,
	        PageBarNotFoundException {
		Element pagebar = getPageBar(currentDoc);
		List<String> urls = Utils.extractUrls(pagebar);
		if (CollectionUtils.isEmpty(urls))
			return null;
		URL currentUrl = null;
		try {
			currentUrl = new URL(currentDoc.location());
		} catch (MalformedURLException e) {
			throw new PrevPageNotFoundException(
			        "Preview Page Not Found beacause currentUrl cause malformed url exception: "
			                + currentDoc.location());
		}
		URL realUrl = null;
		for (String url : urls) {
			URL prevUrl = null;
			try {
				prevUrl = new URL(url);
			} catch (MalformedURLException e) {
				LOG.warn(url + " malfored url exception. Ignored and continue the loop.");
				continue;
			}

			try {
				realUrl = calculatePrevPageUrl(currentUrl, prevUrl);
			} catch (PrevPageNotFoundException e) {
				continue;
			}
			if (realUrl != null)
				break;
		}
		if (realUrl != null) {
			return realUrl;
		} else {
			throw new PrevPageNotFoundException("Preview Page Not Found");
		}
	}

}
