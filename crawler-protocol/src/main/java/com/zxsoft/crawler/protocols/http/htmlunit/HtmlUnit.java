package com.zxsoft.crawler.protocols.http.htmlunit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.NetUtils;
import org.thinkingcloud.framework.util.StringUtils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.Utils;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;

public class HtmlUnit extends HttpBase {

	public static final Logger LOG = LoggerFactory.getLogger(HtmlUnit.class);

	/**
	 * <p>
	 * Ajax download page.
	 * <p>
	 * Note: Each thread has its own <code>AjaxLoader</code> object
	 */
	private WebClient client = new WebClient(BrowserVersion.FIREFOX_24);
	private HtmlPage htmlPage;

	private void setUp() {
//		client = new WebClient(BrowserVersion.FIREFOX_24);
		client.getOptions().setJavaScriptEnabled(true);
		client.getOptions().setCssEnabled(false);
		client.getOptions().setRedirectEnabled(true);
		client.setAjaxController(new NicelyResynchronizingAjaxController());
//		client.getOptions().setTimeout(50000);
		client.getOptions().setThrowExceptionOnScriptError(false);
		client.getOptions().setPrintContentOnFailingStatusCode(false);
		client.waitForBackgroundJavaScript(5000);
		client.waitForBackgroundJavaScriptStartingBefore(2000);
//		client.getCookieManager().setCookiesEnabled(true);//开启cookie管理
		
	}
	/**
	 * Configure request body and send request.
	 */
	private HtmlPage makeRequest(WebPage page) throws FailingHttpStatusCodeException, IOException {
		setUp();

		URL url = new URL(page.getBaseUrl());
		
		WebRequest request = new WebRequest(url);
		request.setAdditionalHeader("User-Agent", userAgent);
		request.setAdditionalHeader("Accept-Language", acceptLanguage);
		request.setAdditionalHeader("Accept-Charset", acceptCharset);
		request.setAdditionalHeader("Accept", accept);
		request.setAdditionalHeader("Connection", "keep-alive");
		
		if (useProxy ) {
			request.setProxyHost(proxyHost);
			request.setProxyPort(proxyPort);
		}
		
		htmlPage = client.getPage(request);
		return htmlPage;
	}

	
	private void processResponse() throws IOException {
		WebResponse response = htmlPage.getWebResponse();
		charset = response.getContentCharset();
		code = response.getStatusCode();
		contentType = response.getContentType();
		List<NameValuePair> pairs = response.getResponseHeaders();
		for (NameValuePair pair : pairs) {
			headers.set(pair.getName(), pair.getValue());
		}

		content = htmlPage.asXml().getBytes(charset);
		// Limit download size
//		long contentLength = Long.MAX_VALUE;
//		InputStream in = response.getContentAsStream();
//		try {
//			byte[] buffer = new byte[HttpBase.BUFFER_SIZE];
//			int bufferFilled = 0;
//			int totalRead = 0;
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			while ((bufferFilled = in.read(buffer, 0, buffer.length)) != -1
//			        && totalRead + bufferFilled <= contentLength) {
//				totalRead += bufferFilled;
//				out.write(buffer, 0, bufferFilled);
//			}
//			content = out.toByteArray();
//		} catch (Exception e) {
//			if (code == 200)
//				throw new IOException(e.toString());
//		} finally {
//			if (in != null) {
//				in.close();
//			}
//			// request.close();
//		}
//		
//		// Extract gzip, x-gzip and deflate content
//		if (content != null) {
//			// check if we have to uncompress it
//			String contentEncoding = headers.get(Response.CONTENT_ENCODING);
//			if (contentEncoding != null && HtmlUnit.LOG.isTraceEnabled())
//				if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
//					content = processGzipEncoded(content, url);
//				} else if ("deflate".equals(contentEncoding)) {
//					content = processDeflateEncoded(content, url);
//				}
//		}
	}

	/**
	 * 加载当前页
	 * @param url 当前页url地址
	 */
	@Override
	public Response getResponse(WebPage page)
	        throws ProtocolException, IOException {
		try {
			htmlPage = makeRequest(page);
//			LOG.debug(htmlPage.asText());
			processResponse();
		} finally {
			// get.releaseConnection();
			client.closeAllWindows();
		}
		
//		LOG.debug(new String(content, charset));
		URL url = new URL(page.getBaseUrl());
		return new Response(url, code, headers, content, charset);
	}

	/**
	 * 加载上一页
	 */
	@Override
	protected Response loadPrevPage(int pageNum, WebPage page) throws IOException, PageBarNotFoundException {
		setUp();
		Document currentDoc = page.getDocument();
		String urlStr = currentDoc.location();
		// URL url = null;
		if (htmlPage == null) {
			try {
//				url = new URL(urlStr);
				htmlPage = makeRequest(page);
			} catch (FailingHttpStatusCodeException | IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		String host = "";
		try {
			host = Utils.getHost(urlStr);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		}
		String pageXml = htmlPage.asXml();
		Document document = Jsoup.parse(pageXml, host);
		
		// 从列表块中选
//		ListConf listConf = page.getListConf();
//		String listdom = listConf == null ? null : listConf.getListdom();
//		Element listElement = null;
//		if (!StringUtils.isEmpty(listConf)) {
//			listElement = CollectionUtils.isEmpty(document.select(listdom)) ? null : document.select(listdom).first();
//		}
		
		Elements elements = document.select("a:matchesOwn(上一页|上页|<上一页)");
		HtmlAnchor prevAnchor = null;
		if (!CollectionUtils.isEmpty(elements)) {
			prevAnchor = htmlPage.getAnchorByText(elements.first().text());
		} else if (pageNum > 0) {
			Element pagebar = getPageBar(currentDoc);
			Elements achors = pagebar.getElementsByTag("a");
			if (pagebar != null || !CollectionUtils.isEmpty(achors)) {
				for (int i = 0; i < achors.size(); i++) {
					if (Utils.isNum(achors.get(i).text())
					        && Integer.valueOf(achors.get(i).text().trim()) == pageNum - 1) {
						prevAnchor = htmlPage.getAnchorByText(achors.get(i).text());
						break;
					}
				}
			}
		}
		if (prevAnchor != null) {
			htmlPage = prevAnchor.click();
			processResponse();
			client.closeAllWindows();
			return new Response(htmlPage.getUrl(), code, headers, content, headers.get(Response.CONTENT_ENCODING));
		}
		client.closeAllWindows();
		return null;
	}

	/**
	 * 加载下一页
	 */
	@Override
	protected Response loadNextPage(int pageNum, WebPage page) throws IOException, PageBarNotFoundException {
		setUp();
		Document currentDoc = page.getDocument();
		String urlStr = currentDoc.location();
		URL url = null;
//		if (htmlPage == null || !urlStr.equals(htmlPage.getUrl().toExternalForm())) {
			try {
				url = new URL(urlStr);
				htmlPage = makeRequest(page);
			} catch (FailingHttpStatusCodeException | IOException e) {
				e.printStackTrace();
				return null;
			}
//		}
		
		String host = "";
		try {
			host = Utils.getHost(urlStr);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		}
		String pageXml = htmlPage.asXml();
		Document document = Jsoup.parse(pageXml, host);
		Elements elements = document.select("a:matchesOwn(下一页|下页|加载更多)");
		HtmlAnchor nextAnchor = null;
		String newUrl = "";
		if (!CollectionUtils.isEmpty(elements)) {
			newUrl = NetUtils.absUrl(url.toExternalForm(), elements.first().attr("href"));
			nextAnchor = htmlPage.getAnchorByText(elements.first().text());
		} else { // no "下一页|下页|下一页>"
			Element pagebar = getPageBar(currentDoc);
			Elements achors = pagebar.getElementsByTag("a");
			if (pagebar != null || !CollectionUtils.isEmpty(achors)) {
				for (int i = 0; i < achors.size(); i++) {
					if (Utils.isNum(achors.get(i).text())
					        && Integer.valueOf(achors.get(i).text().trim()) == pageNum + 1) {
						try {
						nextAnchor = htmlPage.getAnchorByText(achors.get(i).text());
						} catch (ElementNotFoundException e) {
							return null;
						}
						newUrl = NetUtils.absUrl(url.toExternalForm(),  achors.get(i).attr("href"));
						break;
					}
				}
			}
		}
		if (nextAnchor != null) {
			if (NetUtils.isUrl(newUrl)) {
				WebPage np = page;
				np.setBaseUrl(newUrl);
				htmlPage = makeRequest(np);
			} else {
				htmlPage = nextAnchor.click();
//				nextUrl = htmlPage.getUrl().toExternalForm();
				// do not work ! url is the same as preview.
				newUrl = htmlPage.executeJavaScript("document.location").getJavaScriptResult().toString();
			}
			
			processResponse();
			client.closeAllWindows();
//			System.out.println(htmlPage.asXml());
			return new Response(new URL(newUrl), code, headers, content, headers.get(Response.CONTENT_ENCODING));
		}
		client.closeAllWindows();
		return null;
	}

	/**
	 * 加载最后一页
	 */
	@Override
	protected Response loadLastPage(WebPage page) throws IOException, PageBarNotFoundException {
		setUp();
		String urlStr = page.getDocument().location();
		if (htmlPage == null) {
			try {
//				url = new URL(urlStr);
				htmlPage = makeRequest(page);
			} catch (FailingHttpStatusCodeException | IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		String host = "";
		try {
			host = Utils.getHost(urlStr);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		}
		String pageXml = htmlPage.asXml();
		Document document = Jsoup.parse(pageXml, host);

		// 从列表块中选分页栏
		ListConf listConf = page.getListConf();
		String listdom = listConf == null ? null : listConf.getListdom();
		Element listElement = null;
		if (!StringUtils.isEmpty(listConf)) {
			listElement = CollectionUtils.isEmpty(document.select(listdom)) ? null : document.select(listdom).first();
		}
		
		Elements elements = document.select("a:matchesOwn(尾页|末页|最后一页|最末页)");
		HtmlAnchor lastAnchor = null;
		if (!CollectionUtils.isEmpty(elements)) {
			lastAnchor = htmlPage.getAnchorByText(elements.first().text());
		} else {
			Element pagebar = getPageBar(listElement == null ? document : listElement);
			if (pagebar == null)
				return null;
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
				lastAnchor = htmlPage.getAnchorByText(el.text());

			}
		}

		if (lastAnchor != null) {
			htmlPage = lastAnchor.click();
			pageXml = htmlPage.asXml();
			document = Jsoup.parse(pageXml, host);
			processResponse();
			client.closeAllWindows();
			return new Response(htmlPage.getUrl(), code, headers, content, headers.get(Response.CONTENT_ENCODING));
		}
		client.closeAllWindows();
		return null;
	}

	/**
	 * 用POST方法请求
	 */
	@Override
    public Response postForResponse(URL url,
            org.apache.commons.httpclient.NameValuePair[] data)
            throws IOException {
	    // TODO Auto-generated method stub
	    return null;
    }

	public static void main(String[] args) throws FailingHttpStatusCodeException, IOException {
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setRedirectEnabled(true);
		webClient.waitForBackgroundJavaScript(5000);
		webClient.waitForBackgroundJavaScriptStartingBefore(1000);

		String u = "http://news.163.com/latest/";
//		u = "http://roll.news.sina.com.cn/s/";
		WebRequest request = new WebRequest(new URL(u));
		HtmlPage page = webClient.getPage(request);
//		webClient.getAjaxController().processSynchron(page, request, false);
		System.out.println(page.asXml());
	}
}
