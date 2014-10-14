package com.zxsoft.crawler.protocols.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.hadoop.conf.Configuration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.dns.DNSCache;
import com.zxsoft.crawler.metadata.Metadata;
import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus;
import com.zxsoft.crawler.protocol.ProtocolStatusCodes;
import com.zxsoft.crawler.protocol.ProtocolStatusUtils;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
import com.zxsoft.crawler.protocols.http.htmlunit.HtmlUnit;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClient;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PageHelper;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;
import com.zxsoft.crawler.util.protocol.DeflateUtils;
import com.zxsoft.crawler.util.protocol.GZIPUtils;
import com.zxsoft.proxy.Proxy;
import com.zxsoft.proxy.ProxyRandom;

/**
 * @see HtmlUnit
 * @see HttpClient
 */
public abstract class HttpBase extends PageHelper {

	public static final int BUFFER_SIZE = 1024 * 1024;

	/** Indicates if a proxy is used */
	protected boolean useProxy = true;

	/** The network timeout in millisecond */
	protected int timeout = 10000;

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

	/** The nutch configuration */
	private Configuration conf = null;

	/** Do we use HTTP/1.1? */
	protected boolean useHttp11 = false;

	protected URL url;
	
	protected int code;
	protected Metadata headers = new Metadata();
	protected byte[] content = null;
	protected String charset = "utf-8";
	protected String contentType;
	
	// Inherited Javadoc
	public void setConf(Configuration conf) {
		this.conf = conf;
		this.useProxy = conf.getBoolean("http.proxy.use", true);
		this.timeout = conf.getInt("http.timeout", 5000);
		this.maxContent = conf.getInt("http.content.limit", 1024 * 1024);
		this.userAgent =conf.get("", userAgent);
		this.acceptLanguage = conf.get("http.accept.language", acceptLanguage);
		this.accept = conf.get("http.accept", accept);
		this.useHttp11 = conf.getBoolean("http.useHttp11", false);
	}

	public Configuration getConf() {
		return this.conf;
	}
	
	private ProxyRandom proxyRandom = new ProxyRandom();
	
	public HttpBase() {
		
	}
	
	protected Proxy getProxy(String url) {
		return proxyRandom.random(url);
	}

	
	/** Get ProtocolOutput of current, prev, next, last page 
	 * @throws IOException 
	 * @throws ProtocolException **/
	public ProtocolOutput getProtocolOutput(String url) throws ProtocolException, IOException {
		URL u = new URL(url);
		Response response = getResponse(u , false); 
		return dealResponse(response);
	}
	
	public ProtocolOutput getProtocolOutputOfPrevPage(int pageNum, Document currentDoc, boolean needAuth) throws PrevPageNotFoundException, PageBarNotFoundException {
        try {
        	Response response = loadPrevPage(pageNum, currentDoc, needAuth);
	        return dealResponse(response); 
        } catch(PrevPageNotFoundException e) { 
        	throw e;
        } catch(PageBarNotFoundException e) { 
        	throw e;
        } catch (Throwable e) {
        	LOG.error("Failed with the following error: ", e);
        	return new ProtocolOutput(new ProtocolStatus(currentDoc.location(), STATUS_CODE.FAILED, e.getMessage()));
        }
	}
	
	public ProtocolOutput getProtocolOutputOfNextPage(int pageNum, Document currentDoc, boolean needAuth) throws PageBarNotFoundException {
		try {
			Response response = loadNextPage(pageNum, currentDoc, needAuth);
			return dealResponse(response); 
		} catch(PageBarNotFoundException e) { 
        	throw e;
        } catch (Throwable e) {
			LOG.error("Failed with the following error: ", e);
			return new ProtocolOutput(new ProtocolStatus(currentDoc.location(), STATUS_CODE.FAILED, e.getMessage()));
		}
	}

	public ProtocolOutput getProtocolOutputOfLastPage(Document currentDoc, boolean needAuth) throws PageBarNotFoundException {
		try {
			Response response = loadLastPage(currentDoc, needAuth);
			return dealResponse(response); 
		} catch(PageBarNotFoundException e) { 
        	throw e;
        }  catch (Throwable e) {
			LOG.error("Failed with the following error: ", e);
			return new ProtocolOutput(new ProtocolStatus(currentDoc.location(), STATUS_CODE.FAILED, e.getMessage()));
		}
	}
	/** End get ProtocolOutput of current, prev, next, last page **/
	
	
	public ProtocolOutput dealResponse (Response response) throws IOException {
		if (response == null) {
			return new ProtocolOutput(new ProtocolStatus("", STATUS_CODE.FAILED, "response is null"));
		}
		int code = response.code;
		byte[] content = response.content;
		URL u = response.url;
		if (content == null) // there is a error here 
			return null;
		InputStream in = new ByteArrayInputStream(content);

		Document document = Jsoup.parse(in, response.charset, u.toString());

		if (code == 200) { // got a good response
//			String ip = new DNSCache().getAsString(u);
			return new ProtocolOutput(document); // return it
		} else if (code == 410) { // page is gone
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(
			        STATUS_CODE.GONE, "Http: " + code + " url=" + response.url.toString()));
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
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(
					STATUS_CODE.GONE, u.toString()));
		} else if (code == 401) { // requires authorization, but no valid
								  // auth provided.
			LOG.trace("401 Authentication Required");
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(
					STATUS_CODE.ACCESS_DENIED, "Authentication required: " + u.toString()));
		} else if (code == 404) {
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(
					STATUS_CODE.NOTFOUND, u.toString()));
		} else if (code == 410) { // permanently GONE
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(
					STATUS_CODE.GONE, u.toString()));
		} else {
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(
					STATUS_CODE.EXCEPTION, "Http code=" + code + ", url=" + u));
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

	public abstract Response postForResponse(URL url, NameValuePair[] data) throws IOException;
	
	public abstract Response getResponse(URL url,
	        boolean needAuth) throws ProtocolException, IOException;
	protected abstract Response loadPrevPage(int pageNum, Document currentDoc, boolean needAuth) throws IOException, ProtocolException, PrevPageNotFoundException, PageBarNotFoundException;
	protected abstract Response loadNextPage(int pageNum, Document currentDoc, boolean needAuth) throws IOException, ProtocolException, PageBarNotFoundException;
	protected abstract Response loadLastPage(Document currentDoc, boolean needAuth) throws IOException, ProtocolException, PageBarNotFoundException;

}
