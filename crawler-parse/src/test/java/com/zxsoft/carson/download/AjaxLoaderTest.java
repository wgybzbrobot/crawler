package com.zxsoft.carson.download;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.util.Assert;

public class AjaxLoaderTest {

	@Test
	public void testLoadNextPageSohuBBS() throws IOException {
		String url = "http://club.news.sohu.com/minjian/threads";
		AjaxLoader loader = new AjaxLoader();
		Document currentDoc = loader.load(url);
		Document nextDoc = loader.loadNextPage(1, currentDoc);
		Elements elements = nextDoc.select("div#bbs_list table tr");
		Assert.notNull(elements);
		Assert.isTrue(elements.size() > 40);
	}
}
