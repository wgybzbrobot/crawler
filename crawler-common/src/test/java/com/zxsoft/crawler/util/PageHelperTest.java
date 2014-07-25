package com.zxsoft.crawler.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.springframework.util.Assert;

import com.gemstone.gemfire.internal.tools.gfsh.app.commands.pr;
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
	public void testGetPageBar1() throws IOException, PageBarNotFoundException {
		Document currentDoc = Jsoup.connect("http://bbs.anhuinews.com/thread-1102159-4-2.html").get();
		Element pagebar = PageHelper.getPageBar(currentDoc);
		Assert.notNull(pagebar);
		System.out.println(pagebar);
	}
}
