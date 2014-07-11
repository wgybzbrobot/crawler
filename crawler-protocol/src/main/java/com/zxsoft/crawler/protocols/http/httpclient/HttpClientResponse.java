package com.zxsoft.crawler.protocols.http.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import org.apache.avro.util.Utf8;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.springframework.util.StringUtils;

import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.metadata.Metadata;
import com.zxsoft.crawler.metadata.SpellCheckedMetadata;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.storage.WebPage;

/**
 * An HTTP response.
 *
 */
public class HttpClientResponse implements Response {

	private URL url;
	private byte[] content;
	private int code;
	private Metadata headers = new SpellCheckedMetadata();
	
	private String proxyUsername;
	private String proxyPassword;
	private String proxyHost;
	private int proxyPort;
	private String proxyRealm;
	

	private RequestConfig defaultRequestConfig = RequestConfig.custom()
	        .setCookieSpec(CookieSpecs.BEST_MATCH).setExpectContinueEnabled(true)
	        .setStaleConnectionCheckEnabled(true)
	        .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
	        .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).setSocketTimeout(5000)
	        .setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();

	public HttpClientResponse(HttpClient http, URL url, Proxy proxy, WebPage page, boolean followRedirects)
	        throws IOException, HttpException {

		// Prepare GET method for HTTP request
		this.url = url;
		HttpGet httpget = new HttpGet(url.toString());

		this.proxyUsername = proxy.getUsername();
		this.proxyPassword = proxy.getPassword();
		this.proxyHost = proxy.getHost();
		this.proxyPort = proxy.getPort();
		this.proxyRealm = proxy.getRealm();
		
		RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
		        .setProxy(new HttpHost(proxyHost, proxyPort)).build();
		httpget.setConfig(requestConfig);

		try {
			CookieStore cookieStore = new BasicCookieStore();
			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			AuthScope proxyAuthScope = getAuthScope(this.proxyHost, this.proxyPort, this.proxyRealm);

			NTCredentials proxyCredentials = new NTCredentials(this.proxyUsername, this.proxyPassword,
			        proxyHost, this.proxyRealm);
			credentialsProvider.setCredentials(proxyAuthScope, proxyCredentials);
			
			HttpClientContext context = HttpClientContext.create();
			context.setCookieStore(cookieStore);
			context.setCredentialsProvider(credentialsProvider);
			CloseableHttpResponse response = http.getClient().execute(httpget, context);

			code = response.getStatusLine().getStatusCode();
			Header[] heads = response.getAllHeaders();

			for (int i = 0; i < heads.length; i++) {
				headers.set(heads[i].getName(), heads[i].getValue());
			}

			// Limit download size
			int contentLength = Integer.MAX_VALUE;
			String contentLengthString = headers.get(Response.CONTENT_LENGTH);
			if (contentLengthString != null) {
				try {
					contentLength = Integer.parseInt(contentLengthString.trim());
				} catch (NumberFormatException ex) {
					throw new HttpException("bad content length: " + contentLengthString);
				}
			}
			if (http.getMaxContent() >= 0 && contentLength > http.getMaxContent()) {
				contentLength = http.getMaxContent();
			}

			// always read content. Sometimes content is useful to find a cause
			// for error.
			InputStream in = response.getEntity().getContent();
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
				// for codes other than 200 OK, we are fine with empty content
			} finally {
				if (in != null) {
					in.close();
				}
				httpget.abort();
			}

			StringBuilder fetchTrace = null;
			if (HttpClient.LOG.isTraceEnabled()) {
				// Trace message
				fetchTrace = new StringBuilder("url: " + url + "; status code: " + code
				        + "; bytes received: " + content.length);
				if (getHeader(Response.CONTENT_LENGTH) != null)
					fetchTrace.append("; Content-Length: " + getHeader(Response.CONTENT_LENGTH));
				if (getHeader(Response.LOCATION) != null)
					fetchTrace.append("; Location: " + getHeader(Response.LOCATION));
			}
			// Extract gzip, x-gzip and deflate content
			if (content != null) {
				// check if we have to uncompress it
				String contentEncoding = headers.get(Response.CONTENT_ENCODING);
				if (contentEncoding != null && HttpClient.LOG.isTraceEnabled())
					fetchTrace.append("; Content-Encoding: " + contentEncoding);
				if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
					content = http.processGzipEncoded(content, url);
					if (HttpClient.LOG.isTraceEnabled())
						fetchTrace.append("; extracted to " + content.length + " bytes");
				} else if ("deflate".equals(contentEncoding)) {
					content = http.processDeflateEncoded(content, url);
					if (HttpClient.LOG.isTraceEnabled())
						fetchTrace.append("; extracted to " + content.length + " bytes");
				}
			}

			// add headers in metadata to row
			if (page.getHeaders() != null) {
				page.getHeaders().clear();
			}
			for (String key : headers.names()) {
				page.putToHeaders(new Utf8(key), new Utf8(headers.get(key)));
			}

			// Logger trace message
			if (HttpClient.LOG.isTraceEnabled()) {
				HttpClient.LOG.trace(fetchTrace.toString());
			}
		} finally {
			httpget.releaseConnection();
		}
	}

	private static AuthScope getAuthScope(String host, int port, String realm) {

		return getAuthScope(host, port, realm, "");
	}

	private static AuthScope getAuthScope(String host, int port, String realm, String scheme) {

		if (host.length() == 0)
			host = null;

		if (port < 0)
			port = -1;

		if (StringUtils.isEmpty(realm))
			realm = null;

		if (StringUtils.isEmpty(scheme))
			scheme = null;

		return new AuthScope(host, port, realm, scheme);
	}
	
	public URL getUrl() {
		return url;
	}

	public int getCode() {
		return code;
	}

	public String getHeader(String name) {
		return headers.get(name);
	}

	public Metadata getHeaders() {
		return headers;
	}

	public byte[] getContent() {
		return content;
	}

	@Override
    public String getCharset() {
	    // TODO Auto-generated method stub
	    return null;
    }

}
