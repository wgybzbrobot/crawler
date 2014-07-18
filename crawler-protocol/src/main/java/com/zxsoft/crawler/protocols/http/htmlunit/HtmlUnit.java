package com.zxsoft.crawler.protocols.http.htmlunit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.http.auth.NTCredentials;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.metadata.Metadata;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.Utils;

@Component
@Scope("prototype")
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
		client.getOptions().setRedirectEnabled(true);
		client.setAjaxController(new NicelyResynchronizingAjaxController());
		client.getOptions().setTimeout(50000);
		client.getOptions().setThrowExceptionOnScriptError(false);
		client.waitForBackgroundJavaScript(5000);
		// webClient.waitForBackgroundJavaScriptStartingBefore(10000);
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
	protected Response getResponse(URL url, Proxy proxy, boolean followRedirects)
	        throws com.zxsoft.crawler.net.protocols.ProtocolException, IOException {
		int code;
		Metadata headers = new Metadata();
		byte[] content = null;
		String charset = "utf-8";

		WebRequest request = new WebRequest(url);
		if (proxy != null) {
			request.setProxyHost(proxyHost);
			request.setProxyPort(proxyPort);

			NTCredentials proxyCredentials = new NTCredentials(proxy.getUsername(),
			        proxy.getPassword(), proxyHost, "http");
			request.setCredentials(proxyCredentials);
			if ("SOCKS".equalsIgnoreCase(proxy.getType()))
				request.setSocksProxy(true);
			else
				request.setSocksProxy(false);
		}
		
		try {
			HtmlPage htmlPage = getClient().getPage(request);
			WebResponse response = htmlPage.getWebResponse();
			charset = response.getContentCharset();
			code = response.getStatusCode();
			String contentType = response.getContentType();

			List<NameValuePair> pairs = response.getResponseHeaders();
			for (NameValuePair pair : pairs) {
				headers.set(pair.getName(), pair.getValue());
			}

			// Limit download size
			long contentLength = Long.MAX_VALUE;
			InputStream in = response.getContentAsStream();
			try {
				byte[] buffer = new byte[HttpBase.BUFFER_SIZE];
				int bufferFilled = 0;
				int totalRead = 0;
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				while ((bufferFilled = in.read(buffer, 0, buffer.length)) != -1
				        && totalRead + bufferFilled <= contentLength) {
					totalRead += bufferFilled;
					out.write(buffer, 0, bufferFilled);
				}
				content = out.toByteArray();
			} catch (Exception e) {
				if (code == 200)
					throw new IOException(e.toString());
			} finally {
				if (in != null) {
					in.close();
				}
				// request.close();
			}

			// Extract gzip, x-gzip and deflate content
			if (content != null) {
				// check if we have to uncompress it
				String contentEncoding = headers.get(Response.CONTENT_ENCODING);
				if (contentEncoding != null && HtmlUnit.LOG.isTraceEnabled())
					if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
						content = processGzipEncoded(content, url);
					} else if ("deflate".equals(contentEncoding)) {
						content = processDeflateEncoded(content, url);
					}
			}

		} finally {
			// get.releaseConnection();
			getClient().closeAllWindows();
		}
		return new Response(url, code, headers, content, headers.get(Response.CONTENT_ENCODING));
	}

	public WebClient getClient() {
		return this.client;
	}

}
