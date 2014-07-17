package com.zxsoft.crawler.parse;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.springframework.util.Assert;

import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.protocols.http.PageHelper;

public class PageHelperTest {

	HttpFetcher httpFetcher;
	
	@Test
	public void testGetLastPage1() { // 3
		String url = "http://bbs.news.qq.com/t-1881967-1.htm";
		Document document = null;
        try {
	        document = Jsoup.connect(url).get();
        } catch (IOException e) {
	        e.printStackTrace();
        }	
		Element element = document.select("div#pgt").first();
		System.out.println("1: " + new PageHelper().getLastPage(element));
		
	}
	
	@Test
	public void testGetLastPage2() { // 91
		String url = "http://myfj.qq.com/t-270592-1.htm";
		Document document = null;
		try {
			document = Jsoup.connect(url).userAgent("Mozilla 5.0").get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element element = document.select("div.pgb").first();
		System.out.println("2: " + new PageHelper().getLastPage(element));
		
	}
	
	@Test
	public void testGetLastPage3() { //3
		String url = "http://club.ent.sina.com.cn/viewthread.php?tid=2055756&extra=&page=1";
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element element = document.select("div.pages").first();
		System.out.println("3: " + (new PageHelper().getLastPage(element)));
		
	}
	
	@Test
	public void testGetLastPage4() {
		String url = "http://club.eladies.sina.com.cn/thread-5713845-1-1.html";
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element element = document.select("div.pages").first();
		System.out.println("4: " + new PageHelper().getLastPage(element));
		
	}
	
	@Test
	public void testGetLastPage5() {
		String url = "http://bbs.anhuinews.com/thread-1115108-1-1.html";
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element element = document.select("div#pgt").first();
		System.out.println("5: " + new PageHelper().getLastPage(element));
		
	}
	
	@Test
	public void testGetLastPage6() {
		String url = "http://tieba.baidu.com/p/2941818290?pn=1";
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element element = document.select("div.l_thread_info ul.l_posts_num").first();
		System.out.println("6: " + new PageHelper().getLastPage(element));
		
	}
	
	@Test
	public void testGetLastPage7() {
		String url = "http://bbs.news.163.com/bbs/hometown/408334986,2.html";
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element element = document.select("div.tie-page").first();
		System.out.println("6: " + new PageHelper().getLastPage(element));
		
	}
	
	
	
	
	@Test
	public void testGetPrePage1() {
		String url = "http://tieba.baidu.com/p/2941818290?pn=4";
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element element = document.select("div.l_thread_info ul.l_posts_num").first();
//		System.out.println("6: " + new PageHelper().getPrePage(element));
	}
	
	@Test
	public void testGetPagebarTianYa () throws IOException {
		String url = "http://bbs.tianya.cn/list-52985-1.shtml";
		Document document = httpFetcher.fetch(url).getDocument();
		Element pagebar = PageHelper.getPageBar(document);
		Assert.notNull(pagebar);
		Assert.notEmpty(pagebar.select("a:matches(上一页|上页|<上一页|下一页|下页|下一页>|尾页|末页)"));
		System.out.println(pagebar);
	}

	@Test
	public void testGetPagebarTieXue () throws IOException {
		String url = "http://bbs.tiexue.net/bbs32-0-1.html";
		Document document = httpFetcher.fetch(url).getDocument();
		Element pagebar = PageHelper.getPageBar(document);
		Assert.notNull(pagebar);
		Assert.notEmpty(pagebar.select("a:matches(上一页|上页|<上一页|下一页|下页|下一页>|尾页|末页|>>)"));
		System.out.println(pagebar); // << 
	}
	
	@Test
	public void testGetPagebarDaQi () throws IOException {
		String url = "http://shehui.daqi.com/bbs/709319.html";
		Document document = httpFetcher.fetch(url).getDocument();
		Element pagebar = PageHelper.getPageBar(document);
		Assert.notNull(pagebar);
		Assert.notEmpty(pagebar.select("a:matches(^上一页|上页|<上一页|下一页|下页|下一页>|尾页|末页|>>|4$)"));
		System.out.println(pagebar);
	}
}
