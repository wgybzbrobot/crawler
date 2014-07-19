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
public class HttpClientResponse {

	private URL url;
	private byte[] content;
	private int code;
	private Metadata headers = new SpellCheckedMetadata();
	private String charset;
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

	public HttpClientResponse(HttpClient http, URL url, Proxy proxy, boolean followRedirects)
	        throws IOException, HttpException {

		// Prepare GET method for HTTP request
		this.url = url;
		HttpGet httpget = new HttpGet(url.toString());

		if (proxy != null) {
			this.proxyUsername = proxy.getUsername();
			this.proxyPassword = proxy.getPassword();
			this.proxyHost = proxy.getHost();
			this.proxyPort = proxy.getPort();
			this.proxyRealm = proxy.getRealm();
		}
		
		
		// Request configuration can be overridden at the request level.
		// They will take precedence over the one set at the client
		// level.
		RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
		        .setSocketTimeout(5000).setConnectTimeout(5000)
		        .setConnectionRequestTimeout(5000)
		        .setProxy(new HttpHost(proxyHost, proxyPort)).build();
		httpget.setConfig(requestConfig);

		// Execution context can be customized locally.
		HttpClientContext context = HttpClientContext.create();
		// Contextual attributes set the local context level will take
		// precedence over those set at the client level.

		System.out.println("executing request " + httpget.getURI());
		CloseableHttpResponse response = null;
		try {
			response = http.getClient().execute(httpget, context);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		
//		RequestConfig requestConfig = null;
//		if (StringUtils.isEmpty(proxyHost)) {
//			requestConfig = RequestConfig.copy(defaultRequestConfig).build();
//		} else 
//		 requestConfig = RequestConfig.copy(defaultRequestConfig)
//		        .setProxy(new HttpHost(proxyHost, proxyPort)).build();
//		httpget.setConfig(requestConfig);
		try {
//			CookieStore cookieStore = new BasicCookieStore();
//			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//			AuthScope proxyAuthScope = getAuthScope(this.proxyHost, this.proxyPort, this.proxyRealm);
//
//			NTCredentials proxyCredentials = new NTCredentials(this.proxyUsername, this.proxyPassword,
//			        proxyHost, this.proxyRealm);
//			credentialsProvider.setCredentials(proxyAuthScope, proxyCredentials);
//			
//			HttpClientContext context = HttpClientContext.create();
//			context.setCookieStore(cookieStore);
//			context.setCredentialsProvider(credentialsProvider);
//			httpget.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
//			CloseableHttpResponse response = http.getClient().execute(httpget, context);

			code = response.getStatusLine().getStatusCode();
			Header[] heads = response.getAllHeaders();
			for (int i = 0; i < heads.length; i++) {
				headers.set(heads[i].getName(), heads[i].getValue());
			}
			long contentLength = Long.MAX_VALUE;
			InputStream in = null;
			try {
				in = response.getEntity().getContent();
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
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// Extract gzip, x-gzip and deflate content
			if (content != null) {
				// check if we have to uncompress it
				String contentEncoding = headers.get(Response.CONTENT_ENCODING);
				if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
					content = http.processGzipEncoded(content, url);
				} else if ("deflate".equals(contentEncoding)) {
					content = http.processDeflateEncoded(content, url);
				}
			}

		} finally {
			
			try {
				http.getClient().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
//			httpget.releaseConnection();
//			 http.getClient().close();
		}
	}

	private static AuthScope getAuthScope(String host, int port, String realm) {

		return getAuthScope(host, port, realm, "");
	}

	private static AuthScope getAuthScope(String host, int port, String realm, String scheme) {

		if (StringUtils.isEmpty(host))
			host = null;

		if (StringUtils.isEmpty(port))
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

	public String getCharset() {
	    return charset;
    }

}
