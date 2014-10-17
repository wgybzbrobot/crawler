package com.zxsoft.crawler.protocols.http.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;
import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.NetUtils;
import org.thinkingcloud.framework.util.StringUtils;

import com.zxsoft.crawler.metadata.Metadata;
import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocols.http.AuthHelper;
import com.zxsoft.crawler.protocols.http.CookieStore;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.EncodingDetector;
import com.zxsoft.crawler.util.Utils;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PageHelper;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;
import com.zxsoft.proxy.Proxy;

public class HttpClient extends HttpBase {

	public static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);
	
	private org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient(connectionManager);

	public HttpClient() {}
	
	public HttpClient(Configuration conf) {
		setConf(conf);
    }
	
	@Override
	public Response getResponse(WebPage page) throws ProtocolException,
	        IOException {
		
		int code = -1;
		Metadata headers = new Metadata();
		byte[] content = new byte[1024];
		
		ProtocolSocketFactory factory = new SSLProtocolSocketFactory();
		Protocol https = new Protocol("https", factory, 443);
		Protocol.registerProtocol("https", https);

		HttpConnectionManagerParams params = connectionManager.getParams();
		params.setConnectionTimeout(timeout);
		params.setSoTimeout(timeout);
		params.setSendBufferSize(BUFFER_SIZE);
		params.setReceiveBufferSize(BUFFER_SIZE);
		params.setMaxTotalConnections(maxThreadsTotal);

		params.setDefaultMaxConnectionsPerHost(maxThreadsTotal);

		client.getParams().setConnectionManagerTimeout(timeout);

		HostConfiguration hostConf = client.getHostConfiguration();
		ArrayList<Header> reqHeaders = new ArrayList<Header>();
		reqHeaders.add(new Header("User-Agent", userAgent));
		reqHeaders.add(new Header("Accept-Language", acceptLanguage));
		reqHeaders.add(new Header("Accept-Charset", acceptCharset));
		reqHeaders.add(new Header("Accept", accept));
		reqHeaders.add(new Header("Connection", "keep-alive"));
		url = new URL(page.getBaseUrl());
		String cookie = CookieStore.get(NetUtils.getHost(url));
		if (!StringUtils.isEmpty(cookie)) {
			reqHeaders.add(new Header("Cookie", cookie));
		}
		hostConf.getParams().setParameter("http.default-headers", reqHeaders);

		// HTTP proxy server details
		if (useProxy) {
//			Proxy proxy = proxyRandom.random(url.toString());
			Proxy proxy = getProxy(url.toString());
			if (proxy != null) {
				hostConf.setProxy(proxy.getIp(), proxy.getPort());
			}
			/*if (proxy.getUsername().length() > 0) {
				AuthScope proxyAuthScope = getAuthScope(proxy.getHost(),
						proxy.getPort(),proxy.getRealm());
				NTCredentials proxyCredentials = new NTCredentials(
				        this.proxyUsername, this.proxyPassword, Http.agentHost,
				        this.proxyRealm);
				client.getState().setProxyCredentials(proxyAuthScope,
				        proxyCredentials);
			}*/
		}
		
		GetMethod get = new GetMethod(url.toString());
		HttpMethodParams methodParams = get.getParams();
		methodParams.makeLenient();
		methodParams.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		methodParams.setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
		methodParams.setBooleanParameter(HttpMethodParams.SINGLE_COOKIE_HEADER, true);
		
		
//		boolean auth = AuthHelper.isAuth(url);
		if (page.isAuth()) {
			// get cookie
			
			
			// set auth cookie
			get.setRequestHeader("Cookie", "");
		}
		
		try {
			code = client.executeMethod(get);
			
			get.getRequestHeaders();
			
			Header[] heads = get.getResponseHeaders();
			for (int i = 0; i < heads.length; i++) 
				headers.set(heads[i].getName(), heads[i].getValue());
			
			String contentType = headers.get(Response.CONTENT_TYPE);
			charset = EncodingDetector.parseCharacterEncoding(contentType, get.getResponseBody());
			long contentLength = Long.MAX_VALUE;
			InputStream in = get.getResponseBodyAsStream();
			byte[] buffer = new byte[1024 * 1024];
			int bufferFilled = 0;
			int totalRead = 0;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				while ((bufferFilled = in.read(buffer, 0, buffer.length)) != -1
						&& totalRead + bufferFilled <= contentLength) {
					totalRead += bufferFilled;
					out.write(buffer, 0, bufferFilled);
				}
				content = out.toByteArray();
			} catch (Exception e) {
				if (code == 200) {

				}
			} finally {
				if (in != null) {
					in.close();
				}
				get.abort();
			}

			if (content != null) {
				// check if we have to uncompress it
				String contentEncoding = headers
						.get(Response.CONTENT_ENCODING);
				if ("gzip".equals(contentEncoding)
						|| "x-gzip".equals(contentEncoding)) {
					content = processGzipEncoded(content, url);
				} else if ("deflate".equals(contentEncoding)) {
					content = processDeflateEncoded(content, url);
				}
			}
		} finally {
			get.releaseConnection();
		}
		
		return new Response(url, code, headers, content, charset);
	}

	private static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	private int maxThreadsTotal = 10;

	@Override
	public Response postForResponse(URL url, NameValuePair[] data) throws IOException {
		PostMethod post = new PostMethod(url.toString());
		post.setRequestBody(data);
		HttpMethodParams params = post.getParams();
		params.makeLenient();
		params.setContentCharset("UTF-8");
		params.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		params.setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
		params.setBooleanParameter(HttpMethodParams.SINGLE_COOKIE_HEADER, true);
		
		try {
			code = client.executeMethod(post);
//			post.getResponseBodyAsString();
			Header[] cookies = post.getResponseHeaders("Set-Cookie");
			StringBuilder sb = new StringBuilder();
			for (Header cookie: cookies) {
	            sb.append(cookie.getValue());
            }
//			com.zxsoft.crawler.protocols.http.CookieStore.put(NetUtils.getHost(url), sb.toString());
			
			headers.set("Cookie", sb.toString());
			
			Header[] heads = post.getRequestHeaders();
			for (int i = 0; i < heads.length; i++)
				headers.set(heads[i].getName(), heads[i].getValue());
			
			long contentLength = Long.MAX_VALUE;
			InputStream in = post.getResponseBodyAsStream();
			byte[] buffer = new byte[1024 * 1024];
			int bufferFilled = 0;
			int totalRead = 0;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				while ((bufferFilled = in.read(buffer, 0, buffer.length)) != -1
						&& totalRead + bufferFilled <= contentLength) {
					totalRead += bufferFilled;
					out.write(buffer, 0, bufferFilled);
				}
				content = out.toByteArray();
			} catch (Exception e) {
				if (code == 200) {

				}
			} finally {
				if (in != null) {
					in.close();
				}
				post.abort();
			}
			if (content != null) {
				// check if we have to uncompress it
				String contentEncoding = headers
						.get(Response.CONTENT_ENCODING);
				if ("gzip".equals(contentEncoding)
						|| "x-gzip".equals(contentEncoding)) {
					content = processGzipEncoded(content, url);
				} else if ("deflate".equals(contentEncoding)) {
					content = processDeflateEncoded(content, url);
				}
			}
		} finally {
			post.releaseConnection();
		}
		return new Response(url, code, headers, content, charset);
	}
	
	@Override
	protected Response loadPrevPage(int pageNum, final WebPage page) throws ProtocolException,
	        IOException, PrevPageNotFoundException, PageBarNotFoundException {
		Document currentDoc = page.getDocument();
		Elements elements = currentDoc.select("a:matchesOwn(上一页|上页|<上一页)");
		if (!CollectionUtils.isEmpty(elements)) {
			url = new URL(elements.first().absUrl("href"));
		} else if (pageNum > 1) {
			Element pagebar = getPageBar(currentDoc);
			if (pagebar != null) {
				Elements achors = pagebar.getElementsByTag("a");
				if (pagebar != null || !CollectionUtils.isEmpty(achors)) {
					for (int i = 0; i < achors.size(); i++) {
						if (Utils.isNum(achors.get(i).text())
						        && Integer.valueOf(achors.get(i).text().trim()) == pageNum - 1) {
							url = new URL(achors.get(i).absUrl("href"));
						}
					}
				}
			}
		} else {
			url = PageHelper.calculatePrevPageUrl(currentDoc);
		}
		if (url != null) {
//			LOG.info(currentDoc.location() + " Next Page url: " + url.toString());
			WebPage np = page;
			np.setBaseUrl(url.toExternalForm());
			return getResponse(page);
		}

		throw new PrevPageNotFoundException("Preview Page Not Found");
	}

	@Override
	protected Response loadNextPage(int pageNum, final WebPage page) throws ProtocolException,
	        IOException, PageBarNotFoundException {
		Document currentDoc = page.getDocument();
		Elements elements = currentDoc.select("a:matchesOwn(下一页|下页|下一页>)");
		if (!CollectionUtils.isEmpty(elements)) {
			WebPage np = page;
			String next = elements.first().absUrl("href");
			if (StringUtils.isEmpty(next)) {
				throw new PageBarNotFoundException();
			}
			np.setBaseUrl(next);
			
			return getResponse(np);
		} else {
			/*
			 * Find the position of current page url from page bar, get next
			 * achor as the next page url. However, there is a problem. It's not
			 * very accurate, some url cannot find from page bar, because it
			 * changed when load it.
			 */
			Element pagebar = getPageBar(currentDoc);
			if (pagebar != null) {
				Elements achors = pagebar.getElementsByTag("a");
				if (pagebar != null || !CollectionUtils.isEmpty(achors)) {
					for (int i = 0; i < achors.size(); i++) {
						if (Utils.isNum(achors.get(i).text())
						        && Integer.valueOf(achors.get(i).text().trim()) == pageNum + 1) {
//							LOG.info(currentDoc.location() + "Prev Page url: " + url.toString());
							WebPage np = page;
							np.setBaseUrl(achors.get(i).absUrl("href"));
							return getResponse(np);
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	protected Response loadLastPage(WebPage page) throws ProtocolException, IOException, PageBarNotFoundException {
		Document currentDoc = page.getDocument();
		Elements lastEles = currentDoc.select("a:matchesOwn(尾页|末页|最后一页)");
		if (!CollectionUtils.isEmpty(lastEles)) {
			WebPage np = page;
			np.setBaseUrl(lastEles.first().absUrl("href"));
			return getResponse(np);
		}

		// 1. get all links from page bar
		Element pagebar = getPageBar(currentDoc);
		if (pagebar == null)
			return null;
		Elements links = pagebar.getElementsByTag("a");
		if (CollectionUtils.isEmpty(links)) {
			return null;
		}

		// 2. get max num or contains something in all links, that is last page
		int i = 1;
		Element el = null;
		for (Element ele : links) {
			String v = ele.text();
			if ("18255266882".equals(v)) {
				System.out.println(ele);
			}
			if (Utils.isNum(v) && Integer.valueOf(v) > i) { // get max num
				i = Integer.valueOf(v);
				el = ele;
			}
		}
		if (el == null || StringUtils.isEmpty(el.absUrl("href"))) {
			return null;
		}
//		LOG.info("Last Page url: " + url.toString());
		WebPage np = page;
		np.setBaseUrl(el.absUrl("href"));
		return getResponse(np);
	}
}