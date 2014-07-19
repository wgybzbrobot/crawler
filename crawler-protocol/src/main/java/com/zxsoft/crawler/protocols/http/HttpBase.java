package com.zxsoft.crawler.protocols.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.util.DeflateUtils;
import org.apache.nutch.util.GZIPUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus;
import com.zxsoft.crawler.protocol.ProtocolStatusCodes;
import com.zxsoft.crawler.protocol.ProtocolStatusUtils;
import com.zxsoft.crawler.protocols.http.htmlunit.HtmlUnit;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClient;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClientPageHelper;
import com.zxsoft.crawler.protocols.http.proxy.ProxyRandom;

/**
 * @see HtmlUnit
 * @see HttpClient
 */
@Component
@Scope("prototype")
public abstract class HttpBase {

	public static final int BUFFER_SIZE = 8 * 1024;

	/** Indicates if a proxy is used */
	protected boolean useProxy = false;

	/** The network timeout in millisecond */
	protected int timeout = 10000;

	/** The length limit for downloaded content, in bytes. */
	protected int maxContent = 1024 * 1024;

	/** The Nutch 'User-Agent' request header */
	protected String userAgent = "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36";

	/** The "Accept-Language" request header value. */
	protected String acceptLanguage = "en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4,zh-TW;q=0.2";

	/** The "Accept" request header value. */
	protected String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";

	/** The default logger */
	private final static Logger LOG = LoggerFactory.getLogger(HttpBase.class);

	/** The nutch configuration */
	private Configuration conf = null;

	/** Do we use HTTP/1.1? */
	protected boolean useHttp11 = false;

	// Inherited Javadoc
	public void setConf(Configuration conf) {
		this.conf = conf;
		this.useProxy = conf.getBoolean("http.proxy.use", true);
		this.timeout = conf.getInt("http.timeout", 5000);
		this.maxContent = conf.getInt("http.content.limit", 1024 * 1024);
		this.userAgent = "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36";
		this.acceptLanguage = conf.get("http.accept.language", acceptLanguage);
		this.accept = conf.get("http.accept", accept);
		this.useHttp11 = conf.getBoolean("http.useHttp11", false);
	}

	// Inherited Javadoc
	public Configuration getConf() {
		return this.conf;
	}
	
	@Autowired
	private ProxyRandom proxyRandom;

	
	public ProtocolOutput getProtocolOutput(String url, HttpClientPageHelper.PageType pageType, boolean followRedirect) {
		
		return null;
	}
	
	public ProtocolOutput getProtocolOutput(String url) {
		Proxy proxy = proxyRandom.random();
		
		Document document = null;
		try {
			LOG.info(url);
			URL u = new URL(url);
			Response response = getResponse(u, proxy , false); 
			int code = response.code;
			byte[] content = response.content;

			InputStream in = new ByteArrayInputStream(content);

			document = Jsoup.parse(in, response.charset, response.url.toString());

			if (code == 200) { // got a good response
				return new ProtocolOutput(document); // return it
			} else if (code == 410) { // page is gone
				return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(
				        ProtocolStatusCodes.GONE, "Http: " + code + " url=" + url));
			} else if (code >= 300 && code < 400) { // handle redirect
				String location = response.getHeader("Location");
				// some broken servers, such as MS IIS, use lowercase header
				// name...
				if (location == null)
					location = response.getHeader("location");
				if (location == null)
					location = "";
				u = new URL(u, location);
				int protocolStatusCode;
				switch (code) {
				case 300: // multiple choices, preferred value in Location
					protocolStatusCode = ProtocolStatusCodes.MOVED;
					break;
				case 301: // moved permanently
				case 305: // use proxy (Location is URL of proxy)
					protocolStatusCode = ProtocolStatusCodes.MOVED;
					break;
				case 302: // found (temporarily moved)
				case 303: // see other (redirect after POST)
				case 307: // temporary redirect
					protocolStatusCode = ProtocolStatusUtils.TEMP_MOVED;
					break;
				case 304: // not modified
					protocolStatusCode = ProtocolStatusUtils.NOTMODIFIED;
					break;
				default:
					protocolStatusCode = ProtocolStatusUtils.MOVED;
				}
				// handle this in the higher layer.
				return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(protocolStatusCode, u));
			} else if (code == 400) { // bad request, mark as GONE
				LOG.trace("400 Bad request: " + u);
				return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(
				        ProtocolStatusCodes.GONE, u));
			} else if (code == 401) { // requires authorization, but no valid
									  // auth provided.
				LOG.trace("401 Authentication Required");
				return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(
				        ProtocolStatusCodes.ACCESS_DENIED, "Authentication required: " + url));
			} else if (code == 404) {
				return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(
				        ProtocolStatusCodes.NOTFOUND, u));
			} else if (code == 410) { // permanently GONE
				return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(
				        ProtocolStatusCodes.GONE, u));
			} else {
				return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(
				        ProtocolStatusCodes.EXCEPTION, "Http code=" + code + ", url=" + u));
			}
		} catch (Throwable e) {
			LOG.error("Failed with the following error: ", e);
			return null;
		}
	}

	public boolean useProxy() {
		return useProxy;
	}

	public int getTimeout() {
		return timeout;
	}

	public int getMaxContent() {
		return maxContent;
	}

	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * Value of "Accept-Language" request header sent by Nutch.
	 * 
	 * @return The value of the header "Accept-Language" header.
	 */
	public String getAcceptLanguage() {
		return acceptLanguage;
	}

	public String getAccept() {
		return accept;
	}

	public boolean getUseHttp11() {
		return useHttp11;
	}

	public byte[] processGzipEncoded(byte[] compressed, URL url) throws IOException {

		if (LOG.isTraceEnabled()) {
			LOG.trace("uncompressing....");
		}

		byte[] content;
		if (getMaxContent() >= 0) {
			content = GZIPUtils.unzipBestEffort(compressed, getMaxContent());
		} else {
			content = GZIPUtils.unzipBestEffort(compressed);
		}

		if (content == null)
			throw new IOException("unzipBestEffort returned null");

		if (LOG.isTraceEnabled()) {
			LOG.trace("fetched " + compressed.length + " bytes of compressed content (expanded to "
			        + content.length + " bytes) from " + url);
		}
		return content;
	}

	public byte[] processDeflateEncoded(byte[] compressed, URL url) throws IOException {

		if (LOG.isTraceEnabled()) {
			LOG.trace("inflating....");
		}

		byte[] content = DeflateUtils.inflateBestEffort(compressed, getMaxContent());

		if (content == null)
			throw new IOException("inflateBestEffort returned null");

		if (LOG.isTraceEnabled()) {
			LOG.trace("fetched " + compressed.length + " bytes of compressed content (expanded to "
			        + content.length + " bytes) from " + url);
		}
		return content;
	}

	protected abstract Response getResponse(URL url, Proxy proxy,
	        boolean followRedirects) throws ProtocolException, IOException;

}
