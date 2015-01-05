package com.zxsoft.crawler.parse;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.zxisl.commons.utils.Assert;

public class JsoupTest {

	@Test
	public void testMatchesOwn() throws IOException {
		Document document = Jsoup.connect("http://roll.news.sina.com.cn/s/channel.php#col=89&spec=&type=&ch=&k=&offset_page=0&offset_num=0&num=60&asc=&page=1").get();
		document.getElementsByAttribute("href").last().absUrl("href");
		Elements elements = document.select("a:matchesOwn(尾页|末页|最后一页|最末页)");
		Assert.notNull(elements);
	}
}
