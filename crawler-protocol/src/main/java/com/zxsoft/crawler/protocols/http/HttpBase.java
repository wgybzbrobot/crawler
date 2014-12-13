package com.zxsoft.crawler.protocols.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.apache.commons.httpclient.NameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.StringUtils;
import com.zxsoft.crawler.metadata.Metadata;
import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus;
import com.zxsoft.crawler.protocol.ProtocolStatusUtils;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
import com.zxsoft.crawler.protocol.util.DeflateUtils;
import com.zxsoft.crawler.protocol.util.GZIPUtils;
import com.zxsoft.crawler.protocols.http.htmlunit.HtmlUnit;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClient;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PageHelper;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

/**
 * @see HtmlUnit
 * @see HttpClient
 */
public abstract class HttpBase extends PageHelper {

	public static final int BUFFER_SIZE = 1024 * 1024;

	/** Indicates if a proxy is used */
	protected static boolean useProxy = false;
	/** The proxy hostname. */
	protected static String proxyHost = null;

	/** The proxy port. */
	protected static int proxyPort = 8080;

	/** Indicates if a proxy is used */
	/** The network timeout in millisecond */
	protected static int timeout = 10000;

	/** The length limit for downloaded content, in bytes. */
	protected int maxContent = 1024 * 1024 * 3;

	/** The Crawler 'User-Agent' request header */
	protected String userAgent = "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36";

	protected String acceptCharset = "utf-8,ISO-8859-1;q=0.7,*;q=0.7";

	/** The "Accept-Language" request header value. */
	protected String acceptLanguage = "en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4,zh-TW;q=0.2";

	/** The "Accept" request header value. */
	protected String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";

	/** The default logger */
	private final static Logger LOG = LoggerFactory.getLogger(HttpBase.class);

	/** Do we use HTTP/1.1? */
	protected boolean useHttp11 = false;

	protected int code;
	protected Metadata headers = new Metadata();
	protected byte[] content = null;
	protected String charset = "utf-8";
	protected String contentType;

	static {
		Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream("protocol.properties");
		try {
			prop.load(stream);
		} catch (IOException e1) {
			LOG.error("Read protocol.properties file failed.\n" + e1.getMessage());
			e1.printStackTrace();
		}
		proxyHost = prop.getProperty("http.proxy.host");
		proxyPort = Integer.valueOf(prop.getProperty("http.proxy.port"));
		try {
			useProxy = (proxyHost != null && proxyHost.length() > 0);
		} catch (NumberFormatException e ) {
			if (StringUtils.isEmpty(proxyHost)) {
				LOG.warn("http.proxy.port set error, use default:" + proxyPort);
			}
		}
		try {
			timeout = Integer.valueOf(prop.getProperty("http.timeout"));
		} catch (NumberFormatException e ) {
			LOG.warn("http.timeout set error, use default:" + timeout);
		}
	}

	/**
	 * Get ProtocolOutput of current, prev, next, last page
	 * 
	 * @throws IOException
	 * @throws ProtocolException
	 **/
	public ProtocolOutput getProtocolOutput(WebPage page) throws ProtocolException, IOException {
		// URL u = new URL(url);
		Response response = getResponse(page);
		return dealResponse(response);
	}

	public ProtocolOutput getProtocolOutputOfPrevPage(int pageNum, WebPage page) throws PrevPageNotFoundException, PageBarNotFoundException {
		try {
			Response response = loadPrevPage(pageNum, page);
			return dealResponse(response);
		} catch (PrevPageNotFoundException e) {
			throw e;
		} catch (PageBarNotFoundException e) {
			throw e;
		} catch (Throwable e) {
			LOG.error("Failed with the following error: ", e.getMessage());
			e.printStackTrace();
			return new ProtocolOutput(new ProtocolStatus(page.getDocument().location(), STATUS_CODE.FAILED, e.getMessage()));
		}
	}

	public ProtocolOutput getProtocolOutputOfNextPage(int pageNum, WebPage page) throws PageBarNotFoundException {
		try {
			Response response = loadNextPage(pageNum, page);
			return dealResponse(response);
		} catch (PageBarNotFoundException e) {
			throw e;
		} catch (Throwable e) {
			LOG.error("Failed with the following error: ", e);
			return new ProtocolOutput(new ProtocolStatus(page.getDocument().location(), STATUS_CODE.FAILED, e.getMessage()));
		}
	}

	public ProtocolOutput getProtocolOutputOfLastPage(WebPage page) throws PageBarNotFoundException {
		try {
			Response response = loadLastPage(page);
			return dealResponse(response);
		} catch (PageBarNotFoundException e) {
			throw e;
		} catch (Throwable e) {
			LOG.error("Failed with the following error: ", e);
			return new ProtocolOutput(new ProtocolStatus(page.getDocument().location(), STATUS_CODE.FAILED, e.getMessage()));
		}
	}

	/** End get ProtocolOutput of current, prev, next, last page **/

	public ProtocolOutput dealResponse(Response response) throws IOException {
		if (response == null) {
			return new ProtocolOutput(new ProtocolStatus("", STATUS_CODE.FAILED, "response is null"));
		}
		int code = response.code;
		byte[] content = response.content;
		URL u = response.url;
		if (content == null) // there is a error here
			return new ProtocolOutput(ProtocolStatusUtils.makeStatus(STATUS_CODE.FAILED, u.toString(), "content is null"));
		InputStream in = new ByteArrayInputStream(content);

		Document document = Jsoup.parse(in, response.charset, u.toString());

		if (code == 200) { // got a good response
			return new ProtocolOutput(document); // return it
		} else if (code == 410) { // page is gone
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(STATUS_CODE.GONE,
			        "Http: " + code + " url=" + response.url.toString()));
		} else if (code >= 300 && code < 400) { // handle redirect
			String location = response.getHeader("Location");
			// some broken servers, such as MS IIS, use lowercase header
			// name...
			if (location == null)
				location = response.getHeader("location");
			if (location == null)
				location = "";
			STATUS_CODE protocolStatusCode;
			switch (code) {
			case 300: // multiple choices, preferred value in Location
				protocolStatusCode = STATUS_CODE.MOVED;
				break;
			case 301: // moved permanently
			case 305: // use proxy (Location is URL of proxy)
				protocolStatusCode = STATUS_CODE.MOVED;
				break;
			case 302: // found (temporarily moved)
			case 303: // see other (redirect after POST)
			case 307: // temporary redirect
				protocolStatusCode = STATUS_CODE.TEMP_MOVED;
				break;
			case 304: // not modified
				protocolStatusCode = STATUS_CODE.NOTMODIFIED;
				break;
			default:
				protocolStatusCode = STATUS_CODE.MOVED;
			}
			// handle this in the higher layer.
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(protocolStatusCode, u.toString()));
		} else if (code == 400) { // bad request, mark as GONE
			LOG.trace("400 Bad request: " + u);
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(STATUS_CODE.GONE, u.toString()));
		} else if (code == 401) { // requires authorization, but no valid
			                      // auth provided.
			LOG.trace("401 Authentication Required");
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(STATUS_CODE.ACCESS_DENIED,
			        "Authentication required: " + u.toString()));
		} else if (code == 404) {
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(STATUS_CODE.NOTFOUND, u.toString()));
		} else if (code == 410) { // permanently GONE
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(STATUS_CODE.GONE, u.toString()));
		} else {
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(STATUS_CODE.EXCEPTION, "Http code=" + code + ", url=" + u));
		}
	}

	public ProtocolOutput post(String u, NameValuePair[] data) throws IOException {
		URL url = new URL(u);
		Response response = postForResponse(url, data);
		ProtocolOutput output = dealResponse(response);
		return output;
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
			LOG.trace("fetched " + compressed.length + " bytes of compressed content (expanded to " + content.length + " bytes) from "
			        + url);
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
			LOG.trace("fetched " + compressed.length + " bytes of compressed content (expanded to " + content.length + " bytes) from "
			        + url);
		}
		return content;
	}

	public abstract Response postForResponse(URL url, NameValuePair[] data) throws IOException;

	/**
	 * @param page
	 *            包含url, auth, website URL url, boolean needAuth, String webiste
	 */
	public abstract Response getResponse(WebPage page) throws ProtocolException, IOException;

	protected abstract Response loadPrevPage(int pageNum, final WebPage page) throws IOException, ProtocolException,
	        PrevPageNotFoundException, PageBarNotFoundException;

	protected abstract Response loadNextPage(int pageNum, final WebPage page) throws IOException, ProtocolException,
	        PageBarNotFoundException;

	protected abstract Response loadLastPage(final WebPage page) throws IOException, ProtocolException, PageBarNotFoundException;

}
