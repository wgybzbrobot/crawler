package com.zxsoft.crawler.protocols.http;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.zxsoft.crawler.util.Utils;

public class PageHelper {

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
