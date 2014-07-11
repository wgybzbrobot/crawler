package com.zxsoft.crawler.protocols.http.htmlunit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.gargoylesoftware.htmlunit.BinaryPage;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClientResponse;
import com.zxsoft.crawler.protocols.http.proxy.ProxyRandom;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.storage.WebPageMy;
import com.zxsoft.crawler.util.Utils;

public class HtmlUnit extends HttpBase {

	public static final Logger LOG = LoggerFactory.getLogger(HtmlUnit.class);

	/**
	 * <p>
	 * Ajax download page.
	 * <p>
	 * Note: Each thread has its own <code>AjaxLoader</code> object
	 */

	private WebClient client = new WebClient();
	private HtmlPage htmlPage;
	

	public HtmlUnit() {
		client.getOptions().setJavaScriptEnabled(true);
		client.getOptions().setCssEnabled(false);
		client.setAjaxController(new NicelyResynchronizingAjaxController());
		client.getOptions().setTimeout(20000);
		client.getOptions().setThrowExceptionOnScriptError(false);
		Proxy proxy = ProxyRandom.random();
		if (proxy != null) {
			ProxyConfig proxyConfig = new ProxyConfig(proxy.getHost(), proxy.getPort());
			client.getOptions().setProxyConfig(proxyConfig);
		}
	}



	/**
	 * 根据配置加载下一页
	 * 
	 * @return page html
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws FailingHttpStatusCodeException
	 */
	@Deprecated
	public Document loadNextPage(WebPageMy webPage) throws IOException {
		return loadNextPage(1, webPage.getDocument());
	}

	public Document loadNextPage(int pageNum, Document currentDoc) throws IOException {
		String url = currentDoc.location();
		if (htmlPage == null) {
			try {
				htmlPage = client.getPage(url);
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
			return document;
		} else { // no "下一页|下页|下一页>"
//			Element pagebar = PageHelper.getPageBar(currentDoc);
//			Elements achors = pagebar.getElementsByTag("a");
//			if (pagebar != null || !CollectionUtils.isEmpty(achors)) {
//				for (int i = 0; i < achors.size(); i++) {
//					if (Utils.isNum(achors.get(i).text())
//					        && Integer.valueOf(achors.get(i).text().trim()) == pageNum + 1) {
//						return load(achors.get(i).absUrl("href"));
//					}
//				}
//			}
		}
		return null;
	}

	/**
	 * 加载上一页
	 * 
	 * @return page html
	 */
	public String loadPrePage(String url) {

		return null;
	}

	@Override
	protected Response getResponse(URL url, Proxy proxy, WebPage page, boolean followRedirects)
	        throws com.zxsoft.crawler.net.protocols.ProtocolException, IOException {
		try {
	        return new HtmlUnitResponse(this, url, proxy, page, followRedirects);
        } catch (Exception e) {
	        e.printStackTrace();
        }
		return null;
	}
	
	public WebClient getClient() {
		return this.client;
	}

}
