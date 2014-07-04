package com.zxsoft.carson.parse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.zxsoft.carson.util.Utils;

public class TestMultimediaExtractor {

	private static WebClient webClient = new WebClient();
	private static HtmlPage htmlPage;

	static {
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setCssEnabled(false);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setTimeout(50000);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
	}

	@Test
	public void testExtractVideo() throws MalformedURLException {
		String url = "http://tieba.baidu.com/p/3074726692?pn=9";
		try {
			htmlPage = webClient.getPage(url);
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<HtmlElement> list = (List<HtmlElement>) htmlPage.getByXPath("//div[@class='vpic_wrap']");
		if (CollectionUtils.isEmpty(list)) {
			System.out.println("Url does not have any video");
			return;
		}

		for (HtmlElement htmlElement : list) {
			try {
				htmlPage = htmlElement.click();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		/*try {
		htmlPage = list.get(0).click();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		String pageXml = htmlPage.asXml();
		Document document = Jsoup.parse(pageXml, Utils.getHost(url));
		Elements elements = document.getElementsByTag("embed"); 
		System.out.println(elements.size());
		
		for (Element element : elements) {
			String videoUrl = element.absUrl("src");
			System.out.println(videoUrl);
        }

	}
	
	/*@Test
	public void testExtractAudioUrl() throws IOException {
		Document document = Jsoup.connect("http://tieba.baidu.com/p/3110651581").get();
		Element element = document.getElementById("j_p_postlist").select("div.d_post_content_main.d_post_content_firstfloor").first();
		MultimediaExtractor extractor = new MultimediaExtractor();
		String url = extractor.extractAudioUrl(element);
		
	}*/
}
