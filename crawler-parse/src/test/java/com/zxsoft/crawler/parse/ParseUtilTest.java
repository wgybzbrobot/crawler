package com.zxsoft.crawler.parse;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class ParseUtilTest {

	@Test
	public void testGetUrlList() throws IOException {
		String url = "http://bbs.hefei.cc/forum-61-1.html";
		Document doc = Jsoup.connect(url).userAgent("Mozilla 5.0").get();
		
		doc.select("div#threadlist");
		Elements elements = doc.select("div#threadlist div.bm_c form").select("table tbody");
		System.out.println(elements.size());
		int i = 0;
		for (Element element : elements) {
			System.out.print(++i + " :");
			/*if (CollectionUtils.isEmpty(element.select("tr th  a"))) {
				continue;
			}*/
	        System.out.println(element.select("tr th  a").first().absUrl("href"));
        }
	}
}
