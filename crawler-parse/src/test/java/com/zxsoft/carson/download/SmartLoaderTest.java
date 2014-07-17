package com.zxsoft.carson.download;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.zxsoft.crawler.protocols.http.AjaxLoader;
import com.zxsoft.crawler.protocols.http.JsoupLoader;
import com.zxsoft.crawler.protocols.http.SmartLoader;

public class SmartLoaderTest {

	/*
	 * load current page
	 */
	@Test
	public void testLoadQQ() throws IOException {
		String url = "http://roll.news.qq.com";
		SmartLoader loader = new SmartLoader();
		String dom = "div#artContainer ul li";
		Document document = loader.load(url, dom);
		Elements elements = document.select(dom);
		Assert.notNull(elements);
		Assert.isTrue(elements.size() > 30);
	}

	@Test
	public void testLoadSina() throws IOException {
		String url = "http://roll.news.sina.com.cn/s/channel.php?ch=01#col=89&spec=&type=&ch=01&k=&offset_page=0&offset_num=0&num=60&asc=&page=1";
		SmartLoader loader = new SmartLoader();
		String dom = "div#d_list ul li";
		Document document = loader.load(url, dom);
		Elements elements = document.select(dom);
		Assert.notNull(elements);
		Assert.isTrue(elements.size() > 30);
	}

	@Test
	public void testLoad163() throws IOException {
		String url = "http://news.163.com/latest/";
		SmartLoader loader = new SmartLoader();
		String dom = "div#instantPanel ul li";
		Document document = loader.load(url, dom);
		Elements elements = document.select(dom);
		Assert.notNull(elements);
		Assert.isTrue(elements.size() > 30);
	}

	@Test
	public void testLoadSohu() throws IOException {
		String url = "http://roll.sohu.com/";
		SmartLoader loader = new SmartLoader();
		String dom = "div.list14 ul li";
		Document document = loader.load(url, dom);
		Elements elements = document.select(dom);
		Assert.notNull(elements);
		Assert.isTrue(elements.size() > 30);
	}

	@Test
	public void testLoadNextpageSohu() throws IOException {
		String url = "http://roll.sohu.com/index.shtml";
		JsoupLoader loader = new JsoupLoader();
		Document currentDoc = loader.load(url);
		currentDoc = loader.loadNextPage(1, currentDoc);
		Elements elements = currentDoc.select("div.list14 ul li");
		Assert.notNull(elements);
		Assert.isTrue(elements.size() > 30);
		/*
		 * for (Element element : elements) {
		 * System.out.println(element.text()); }
		 */
	}

	@Test
	public void testLoadNextpage163() throws IOException {
		String url = "http://news.163.com/latest/";
		AjaxLoader ajaxLoader = new AjaxLoader();
		Document currentDoc = ajaxLoader.load(url);
		Document document = ajaxLoader.loadNextPage(1, currentDoc);
		Elements elements = document.select("div#instantPanel ul li");
		Assert.notNull(elements);
		Assert.isTrue(elements.size() > 30);
	}

	@Test
	public void testLoadNextpageSina() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String url = "http://roll.news.sina.com.cn/s/channel.php?ch=01#col=89&spec=&type=&ch=01&k=&offset_page=0&offset_num=0&num=60&asc=&page=1";
		AjaxLoader ajaxLoader = new AjaxLoader();
		Document currentDoc = ajaxLoader.load(url);
		Document document = ajaxLoader.loadNextPage(1, currentDoc);
		Elements elements = document.select("div#d_list ul li");
		Assert.notNull(elements);
		Assert.isTrue(elements.size() > 30);
	}

	@Test
	public void testLoadNexPageSohuBBS() throws IOException {
		String url = "http://club.news.sohu.com/minjian/threads";
		JsoupLoader loader = new JsoupLoader();
		Document currentDoc = loader.load(url);
		currentDoc = loader.loadNextPage(1, currentDoc);
		if (currentDoc == null) {
			AjaxLoader aloader = new AjaxLoader();
			currentDoc = aloader.load(url);
			currentDoc = aloader.loadNextPage(1, currentDoc);
		}
		Elements elements = currentDoc.select("div#bbs_list table tr");
		Assert.notNull(elements);
		Assert.isTrue(elements.size() > 40);
		for (Element element : elements) {
			System.out.println(element.text());
		}
	}

	@Test
	public void testLoadNexPagePepople() throws IOException { // failure,因为强国论坛翻译没有链接<a>标签
		String url = "http://bbs1.people.com.cn/board/1.html";
		JsoupLoader loader = new JsoupLoader();
		Document currentDoc = loader.load(url);
		currentDoc = loader.loadNextPage(1, currentDoc);
		if (currentDoc == null) {
			AjaxLoader aloader = new AjaxLoader();
			currentDoc = aloader.load(url);
			currentDoc = aloader.loadNextPage(1, currentDoc);
		}
		Elements elements = currentDoc.select("ul.replayList li");
		Assert.notNull(elements);
		Assert.isTrue(elements.size() > 40);
		/*
		 * for (Element element : elements) {
		 * System.out.println(element.text()); }
		 */
	}

	@Test
	public void testLoadNexPageXinHua() throws IOException {
		// String url = "http://forum.home.news.cn/index.jsp"; //
		// <iframe></iframe>
		String url = "http://forum.home.news.cn/list/50-0-0-1.html";
		JsoupLoader loader = new JsoupLoader();
		Document currentDoc = loader.load(url);
		Document nextDoc = loader.loadNextPage(1, currentDoc);
		if (nextDoc == null) {
			AjaxLoader aloader = new AjaxLoader();
			currentDoc = aloader.load(url);
			currentDoc = aloader.loadNextPage(1, currentDoc);
		}
		Elements elements = nextDoc.select("table#tab3 tr>td.zhengwen div.title_limit a");
		Assert.notNull(elements);
		Assert.isTrue(elements.size() > 40);
		for (Element element : elements) {
			System.out.println(element.text());
		}
	}

	@Test
	public void testLoadNexPage() throws IOException {
		String url = "http://www.19lou.com/forum-9-1.html";
		JsoupLoader loader = new JsoupLoader();
		Document currentDoc = loader.load(url);
		Document nextDoc = loader.loadNextPage(1, currentDoc);
		if (nextDoc == null) {
			AjaxLoader aloader = new AjaxLoader();
			currentDoc = aloader.load(url);
			currentDoc = aloader.loadNextPage(1, currentDoc);
		}
		Elements elements = nextDoc.select("table.list-data tbody th.title div.subject a");
		Assert.notNull(elements);
		Assert.isTrue(elements.size() > 40);
	}

	@Test
	public void testLoadNextpageQQ() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		try {
			String url = "http://roll.news.qq.com";
			JsoupLoader loader = new JsoupLoader();
			Document currentDoc = loader.load(url);
			Document nextDoc = loader.loadNextPage(1, currentDoc);
			if (nextDoc == null) {
				AjaxLoader ajaxLoader = new AjaxLoader();
				currentDoc = ajaxLoader.load(url);
				nextDoc = ajaxLoader.loadNextPage(1, currentDoc);
			}

			Elements elements = nextDoc.select("div#artContainer ul li");
			Assert.notNull(elements);
			Assert.isTrue(elements.size() > 30);
			for (Element element : elements) {
				System.out.println(element.text());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLoadLastPageTieba () throws IOException {
		String url = "http://tieba.baidu.com/p/3130652422";
		SmartLoader loader = new SmartLoader();
		Document currentDoc = loader.load(url);
		Document lastDoc = loader.loadLastPage(currentDoc);
		Elements elements = lastDoc.select("div#j_p_postlist > div.l_post");
		Assert.isTrue(elements.size() >= 1);
	}
}
