package com.zxsoft.crawler.protocols.http.htmlunit;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClientPageHelper;
import com.zxsoft.crawler.util.Utils;

/**
 * <p>
 * Ajax download page.
 * <p>
 * Note: Each thread has its own <code>AjaxLoader</code> object
 */
@Component
@Scope("prototype")
public final class HtmlUnitPageHelper {

	@Autowired
	private HttpBase htmlUnit;

	private WebClient webClient = new WebClient();
	private HtmlPage htmlPage;

	public void configureClient() {
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setCssEnabled(false);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setTimeout(20000);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
	}

	/**
	 * 加载上一页
	 * 
	 * @return page html
	 */
	public String loadPrePage(String url) {
		configureClient();
		return null;
	}

	/**
	 * 根据配置加载下一页
	 * 
	 * @return page html
	 * @throws IOException
	 */
	public ProtocolOutput loadNextPage(int pageNum, Document currentDoc) throws IOException {
		configureClient();
		String url = currentDoc.location();
		if (htmlPage == null) {
			try {
				htmlPage = webClient.getPage(url);
			} catch (FailingHttpStatusCodeException | IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		String host = "";
		try {
			host = Utils.getHost(url);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		}
		String pageXml = htmlPage.asXml();
		Document document = Jsoup.parse(pageXml, host);
		Elements elements = document.select("a:matchesOwn(下一页|下页|下一页>)");
		if (!CollectionUtils.isEmpty(elements)) {
			HtmlAnchor nextaAnchor = htmlPage.getAnchorByText(elements.first().text());
			htmlPage = nextaAnchor.click();
			pageXml = htmlPage.asXml();
			document = Jsoup.parse(pageXml, host);
			return new ProtocolOutput(document);
		} else { // no "下一页|下页|下一页>"
			Element pagebar = HttpClientPageHelper.getPageBar(currentDoc);
			Elements achors = pagebar.getElementsByTag("a");
			if (pagebar != null || !CollectionUtils.isEmpty(achors)) {
				for (int i = 0; i < achors.size(); i++) {
					if (Utils.isNum(achors.get(i).text())
					        && Integer.valueOf(achors.get(i).text().trim()) == pageNum + 1) {
						return htmlUnit.getProtocolOutput(url);
					}
				}
			}
		}
		return null;
	}

	public ProtocolOutput loadLastPage(Document currentDoc) throws IOException {
		configureClient();
		String url = currentDoc.location();
		if (htmlPage == null) {
			try {
				htmlPage = webClient.getPage(url);
			} catch (FailingHttpStatusCodeException | IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		String host = "";
		try {
			host = Utils.getHost(url);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		}
		String pageXml = htmlPage.asXml();
		Document document = Jsoup.parse(pageXml, host);
		System.out.println(document);
		Elements elements = document.select("a:matchesOwn(尾页|末页|最后一页|最末页)");
		HtmlAnchor nextaAnchor = null;
		if (!CollectionUtils.isEmpty(elements)) {
			nextaAnchor = htmlPage.getAnchorByText(elements.first().text());
		} else {
			Element pagebar = HttpClientPageHelper.getPageBar(currentDoc);
			if (pagebar == null) return null;
			Elements links = pagebar.getElementsByTag("a");
			if (pagebar != null || !CollectionUtils.isEmpty(links)) {
				Element el = null;
				int i = 1;
				for (Element ele : links) {
					String v = ele.text();
					// get max num as last page.
					if (Utils.isNum(v) && Integer.valueOf(v) > i) { 
						i = Integer.valueOf(v);
						el = ele;
					}
				}
				nextaAnchor = htmlPage.getAnchorByText(el.text());

			}
		}

		if (nextaAnchor != null) {
			htmlPage = nextaAnchor.click();
			pageXml = htmlPage.asXml();
			document = Jsoup.parse(pageXml, host);
			System.out.println(document);
			return new ProtocolOutput(document);
		}
		
		return null;
	}
}
