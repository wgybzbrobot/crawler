package com.zxsoft.crawler.protocol.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import com.zxisl.commons.utils.Assert;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PageHelper;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

public class PageHelperTest {

	@Test
	public void testCalculatePrevPageUrl1() throws MalformedURLException, PrevPageNotFoundException {
		URL currentUrl = new URL("http://tieba.baidu.com/p/2274241991?pn=11");
		URL testUrl = new URL("http://tieba.baidu.com/p/2274241991?pn=10");
		URL prevUrl = PageHelper.calculatePrevPageUrl(currentUrl, testUrl);
		Assert.isTrue(testUrl.toString().equals(prevUrl.toString()));
	}
	
	@Test
	public void testCalculatePrevPageUrl2() throws MalformedURLException, PrevPageNotFoundException {
		URL currentUrl = new URL("http://bbs.anhuinews.com/thread-1102159-4-2.html");
		URL testUrl = new URL("http://bbs.anhuinews.com/thread-1102159-3-2.html");
		URL prevUrl = PageHelper.calculatePrevPageUrl(currentUrl, testUrl);
		Assert.isTrue(testUrl.toString().equals(prevUrl.toString()));
	}
	
	@Test
	public void testCalculatePrevPageUrl3() throws MalformedURLException, PrevPageNotFoundException {
		URL currentUrl = new URL("http://roll.news.sina.com.cn/s/channel.php?ch=01#col=89&spec=&type=&ch=01&k=&offset_page=0&offset_num=0&num=60&asc=&page=2");
		URL testUrl = 	 new URL("http://roll.news.sina.com.cn/s/channel.php?ch=01#col=89&spec=&type=&ch=01&k=&offset_page=0&offset_num=0&num=60&asc=&page=1");
		URL prevUrl = PageHelper.calculatePrevPageUrl(currentUrl, testUrl);
		Assert.isTrue(testUrl.toString().equals(prevUrl.toString()));
	}
	
	@Test
	public void testCalculatePrevPageUrl4() throws IOException, PrevPageNotFoundException, PageBarNotFoundException {
		Document currentDoc = Jsoup.connect("http://tieba.baidu.com/p/2274241991?pn=11").get();
		URL prevUrl = new URL("http://tieba.baidu.com/p/2274241991?pn=10");
		URL testUrl = PageHelper.calculatePrevPageUrl(currentDoc);
		Assert.isTrue(testUrl.toString().equals(prevUrl.toString()));
	}
	@Test
	public void testCalculatePrevPageUrl5() throws IOException, PrevPageNotFoundException, PageBarNotFoundException {
		Document currentDoc = Jsoup.connect("http://bbs.anhuinews.com/thread-1102159-4-2.html").get();
		URL prevUrl = new URL("http://bbs.anhuinews.com/thread-1102159-3-2.html");
		URL testUrl = PageHelper.calculatePrevPageUrl(currentDoc);
		Assert.isTrue(testUrl.toString().equals(prevUrl.toString()));
	}
	
	@Test
	public void testGetPageBarZhongAn() throws IOException, PageBarNotFoundException {
		Document currentDoc = Jsoup.connect("http://bbs.anhuinews.com/thread-464919-2-1.html").get();
		Element pagebar = PageHelper.getPageBar(currentDoc);
		Assert.notNull(pagebar);
		System.out.println(pagebar);
	}

	@Test
	public void testGetPageBarBaidu() throws IOException, PageBarNotFoundException {
		Document currentDoc = Jsoup.connect("http://www.baidu.com/s?wd=中国人").get();
		Element pagebar = PageHelper.getPageBar(currentDoc);
		Assert.notNull(pagebar);
		System.out.println(pagebar);
	}

	@Test
	public void testGetPageBarTianYa() throws IOException, PageBarNotFoundException {
		Document currentDoc = Jsoup.connect("http://bbs.tianya.cn/list-free-1.shtml").get();
		Element pagebar = PageHelper.getPageBar(currentDoc);
		Assert.notNull(pagebar);
		System.out.println(pagebar);
	}
	@Test
	public void testGetPageBarShangLin() throws IOException, PageBarNotFoundException {
		Connection conn = Jsoup.connect("http://bbs.shanglin360.com/forum-24-1.html");
		conn.userAgent("Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
		Document currentDoc = conn.get();
		Element pagebar = PageHelper.getPageBar(currentDoc);
		Assert.notNull(pagebar);
		System.out.println(pagebar);
	}

	/**
	 * 猫扑
	 */
	@Test
	public void testGetPageBarMop() throws IOException, PageBarNotFoundException {
		Connection conn = Jsoup.connect("http://www.chinaso.com/search/pagesearch.htm?q=%E2%80%9D9%E6%9C%883%E6%97%A5%E5%85%A8%E5%9B%BD%E6%94%BE%E5%81%87%E2%80%9C&page=2&wd=%E2%80%9D9%E6%9C%883%E6%97%A5%E5%85%A8%E5%9B%BD%E6%94%BE%E5%81%87%E2%80%9C");
		conn.userAgent("Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
		Document currentDoc = conn.get();
		Element pagebar = PageHelper.getPageBar(currentDoc);
		Assert.notNull(pagebar);
		System.out.println(pagebar);
	}
}
