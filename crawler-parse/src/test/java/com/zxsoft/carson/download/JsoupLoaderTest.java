package com.zxsoft.carson.download;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.zxsoft.crawler.util.Utils;

public class JsoupLoaderTest {
	
	@Test
	public void testLoadNexPageTieBa() throws IOException {
		String url = "http://tieba.baidu.com/f?kw=%D6%D3%BA%BA%C1%BC";
		JsoupLoader loader = new JsoupLoader();
		Document currentDoc = loader.load(url);
		currentDoc = loader.loadNextPage(1, currentDoc);
		Elements elements = currentDoc.select("div#content_leftList ul li");
		Assert.notNull(elements);
		Assert.isTrue(elements.size() > 40);
	}
	
	@Test
	public void testLoadNexPageHefeiBBS() throws IOException {
		String url = "http://bbs.hefei.cc/forum-196-1.html";
		JsoupLoader loader = new JsoupLoader();
		Document currentDoc = loader.load(url);
		currentDoc = loader.loadNextPage(1, currentDoc);
		Elements elements = currentDoc.select("form#moderate table tr");
		Assert.notNull(elements);
		Assert.isTrue(elements.size() > 40);
		for (Element element : elements) {
	        System.out.println(element.text());
        }
	}

	@Test
	public void testLoadNexPageDaQi() throws IOException {
		String url = "http://shehui.daqi.com/bbs/709319.html";
		JsoupLoader loader = new JsoupLoader();
		Document currentDoc = loader.load(url);
		currentDoc = loader.loadNextPage(1, currentDoc);
		Elements elements = currentDoc.select("div.postsBox ul li");
		Assert.notNull(elements);
		Assert.isTrue(elements.size() > 30);
		for (Element element : elements) {
			System.out.println(element.text());
		}
	}
	
	
	
	
	
	
	@Test
    public void test() throws IOException {
//        String url = "http://roll.news.sina.com.cn/s/channel.php?ch=01#col=89&spec=&type=&ch=01&k=&offset_page=0&offset_num=0&num=60&asc=&page=1";
        String url = "http://roll.news.qq.com";
        
        Document document = Jsoup.connect(url).get();
        System.out.println(document.html());
        Element element = document.select("div#d_list").first();
        Elements elements = element.select("a[href]");
        int i = 0;
        for (Element ele : elements) {
            i++;
            System.out.println(i + ": " + ele.absUrl("href") + ele.text());
        }
    }
	
//    @Test
    public void testTieba()  {
//    	String url = "http://tieba.baidu.com/f?kw=%B0%F6%B2%BA&pn=0&statsInfo=frs_pager";
    	String url = "http://tieba.baidu.com/f?kw=%D0%A1%CD%C3gaara";
    	
    	Document document = null;
        try {
        	document = Jsoup.connect(url).userAgent("Mozilla/5.0") .timeout(30000).maxBodySize(4 * 1024 * 1024).get();
        } catch (IOException e) {
	        e.printStackTrace();
        }
    	
//        removeComments(document);
        
    	Element threadlist = document.select("div#content_leftList").first();
    	
    	Elements line = threadlist.select("li div.threadlist_li_right");
    	int i = 0;
    	for (Element element : line) {
    		i++;
	        Element ele = element.select("a.j_th_tit").first();
	        if (ele == null) {
	        	continue;
	        }
	        System.out.println(i + ": " + ele.text() + " " + ele.absUrl("href"));
	        
        }
    	
//    	System.out.println(document.body().html());
    	
    	String htm = document.html().replaceAll("<!--", "");
        htm = htm.replaceAll("-->", "");
    	document = Jsoup.parse(htm, document.baseUri());
//    	removeComments(document);
    	threadlist = document.select("div#content_leftList").first();
    	
    	 line = threadlist.select("li div.threadlist_li_right");
    	i = 0;
    	Set<String> set = new HashSet<String>();
    	for (Element element : line) {
    		i++;
	        Element ele = element.select("a.j_th_tit").first();
	        if (ele == null) {
	        	continue;
	        }
	        set.add(ele.absUrl("href"));
	        System.out.println(i + ": " + ele.text() + " " + ele.absUrl("href"));
	        
        }
    	System.out.println(set.size());
    	
    	
    }
    
    
//    @Test
    public void testAnhuinews() throws IOException {
    	String url = "http://bbs.anhuinews.com/forum-319-1.html";
    	
    	Document document = Jsoup.connect(url).get();
    	
    	Element threadlist = document.select("div#threadlist").first();
    	
    	Elements line = threadlist.select("tbody tr");
    	int i = 0;
    	for (Element element : line) {
    		i++;
    		if (CollectionUtils.isEmpty(element.select("a.xst"))) {
    			continue;
    		}
    		Element ele = element.select("a.xst").first();
    		System.out.println(i + ": " +ele.text() + " " + ele.absUrl("href"));
    		
    	}
    	
    }
    
//    @Test
    public void testSinaNews2() {
    	String url = "http://roll.news.sina.com.cn/s/channel.php?ch=01#col=89&spec=&type=&ch=01&k=&offset_page=0&offset_num=0&num=60&asc=&page=1";
    	Document document = null;
        try {
	        document = Jsoup.connect(url)//.data("query", "Java")
	        	.userAgent("Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36")
//       		  .cookie("auth", "token")
	        	  .timeout(30000)
	        	  .get();
        } catch (IOException e) {
	        e.printStackTrace();
        }
    	
    	Element threadlist = document.select("div#pL_Main").first();
    	
    	Elements line = threadlist.select("ul li");
    	int i = 0;
    	for (Element element : line) {
    		i++;
    		if (CollectionUtils.isEmpty(element.select("span.c_tit"))) {
    			continue;
    		}
    		Element ele = element.select("a").first();
    		System.out.println(i + ": " +ele.text() + " " + ele.absUrl("href"));
    		
    	}
    }
    
//    @Test
    public void testTieba2()  {
    	String url = "http://tieba.baidu.com/p/3070137467";
    	Document document = null;
//    	Document document = new JsoupLoader().load(url);
    	try {
    		 document = Jsoup.connect(url).get();
    	} catch (Exception exception) {
    		exception.printStackTrace();
    	}
    	System.out.println(document.html());
    	Elements elements = document.select("div.l_post.l_post_bright");
    	for (Element element : elements) {
//    		System.out.println(element.html());
    		System.out.println(element.select("div.core_reply_tail>ul.p_tail>li:eq(1)>span").first().text());
	        
        }
    	
    }
    
    /**
     * Only for Baidu Tieba
     * @throws MalformedURLException 
     */
    @Test
	public void test1() throws MalformedURLException {
//		String url = "http://tieba.baidu.com/p/3070706026";
		String url = "http://61.191.206.4/p/3070706026";
		URL u = new URL("http", "61.191.206.4", 80, "/p/3070706026");
		Document document = null;
        try {
        	URLConnection conn = u.openConnection();
        	conn.connect();
        	System.out.println(conn.getContent());
        	document = Jsoup.parse(u, 8000);
//	        document = Jsoup.connect(url).get();
        } catch (IOException e) {
	        e.printStackTrace();
        }
		
		Elements eles = document.select("div.l_post.l_post_bright").select("ul.p_tail li:eq(1) span");
		
		Element datafield = document.select("div.l_post.l_post_bright").first();
		String json = datafield.attr("data-field");
		
		System.out.println(Utils.extractDate(json));
	}
    
}
