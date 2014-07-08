package com.zxsoft.crawler.protocols.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;











// HTTP Client imports
import org.apache.avro.util.Utf8;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.metadata.SpellCheckedMetadata;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpException;
//import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.DefaultHttpResponseParser;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.util.CharArrayBuffer;

import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.cache.proxy.ProxyRandom;
import com.zxsoft.crawler.cache.proxy.ehcache.EhcacheProxyCacheStorage;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.storage.WebPage;

/**
 * An HTTP response.
 *
 */
public class WebClient implements Response {

	private URL url;
	private byte[] content;
	private int code;
	private Metadata headers = new SpellCheckedMetadata();

	/**
	 * Fetches the given <code>url</code> and prepares HTTP response.
	 *
	 * @param http
	 *            An instance of the implementation class of this plugin
	 * @param url
	 *            URL to be fetched
	 * @param page
	 *            WebPage
	 * @param followRedirects
	 *            Whether to follow redirects; follows redirect if and only if
	 *            this is true
	 * @return HTTP response
	 * @throws IOException
	 *             When an error occurs
	 * @throws HttpException
	 */
	WebClient(Http http, URL url, WebPage page, boolean followRedirects) throws IOException, HttpException {

		// Create global request configuration
		RequestConfig defaultRequestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BEST_MATCH)
		        .setExpectContinueEnabled(true).setStaleConnectionCheckEnabled(true)
		        .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
		        .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).setSocketTimeout(5000)
		        .setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();
		
		// Prepare GET method for HTTP request
		this.url = url;
		HttpGet httpget = new HttpGet(url.toString());
		
		Proxy proxy = new EhcacheProxyCacheStorage().get(null);
		RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
		        .setProxy(new HttpHost(proxy.getHost(), proxy.getPort())).build();
		httpget.setConfig(requestConfig);

		try {
			CookieStore cookieStore = new BasicCookieStore();
			// Use custom credentials provider if necessary.
			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			HttpClientContext context = HttpClientContext.create();
			// Contextual attributes set the local context level will take
			// precedence over those set at the client level.
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
			if (Http.LOG.isTraceEnabled()) {
				// Trace message
				fetchTrace = new StringBuilder("url: " + url + "; status code: " + code + "; bytes received: "
				        + content.length);
				if (getHeader(Response.CONTENT_LENGTH) != null)
					fetchTrace.append("; Content-Length: " + getHeader(Response.CONTENT_LENGTH));
				if (getHeader(Response.LOCATION) != null)
					fetchTrace.append("; Location: " + getHeader(Response.LOCATION));
			}
			// Extract gzip, x-gzip and deflate content
			if (content != null) {
				// check if we have to uncompress it
				String contentEncoding = headers.get(Response.CONTENT_ENCODING);
				if (contentEncoding != null && Http.LOG.isTraceEnabled())
					fetchTrace.append("; Content-Encoding: " + contentEncoding);
				if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
					content = http.processGzipEncoded(content, url);
					if (Http.LOG.isTraceEnabled())
						fetchTrace.append("; extracted to " + content.length + " bytes");
				} else if ("deflate".equals(contentEncoding)) {
					content = http.processDeflateEncoded(content, url);
					if (Http.LOG.isTraceEnabled())
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
			if (Http.LOG.isTraceEnabled()) {
				Http.LOG.trace(fetchTrace.toString());
			}
		} finally {
			httpget.releaseConnection();
		}
	}

	/*
	 * ------------------------- * <implementation:Response> *
	 * -------------------------
	 */

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

	/*
	 * -------------------------- * </implementation:Response> *
	 * --------------------------
	 */
}
