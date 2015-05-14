package com.zxsoft.crawler.protocols.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.tika.metadata.Metadata;
import org.jsoup.Jsoup;
import org.jsoup.helper.DataUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.StringUtils;
//import com.zxsoft.crawler.metadata.Metadata;
import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
import com.zxsoft.crawler.protocol.ProtocolStatusUtils;
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

	public static final int BUFFER_SIZE = 8 * 1024;

	/** Indicates if a proxy is used */
	protected static   boolean useProxy = false;
	/** The proxy hostname. */
	protected  static String proxyHost = null;

	/** The proxy port. */
	protected  static  int proxyPort = 8080;
	
	protected static  String proxyUsername;
	protected static  String proxyPassword;
	
	protected static final int retryNum = 3;

	/** Indicates if a proxy is used */
	/** The network timeout in millisecond */
	protected static   int timeout = 20000;
	protected static   int sotimeout = 30000;

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

//	public void setup() {
	static {
		Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		LOG.info("Loading protocol.properties ...");
		InputStream stream = loader.getResourceAsStream("protocol.properties");
		if (stream == null) {
		        LOG.error("Load protocol.properties failed.");
		}
		try {
			prop.load(stream);
		} catch (IOException e1) {
			LOG.error("Read protocol.properties file failed.\n" + e1.getMessage());
			e1.printStackTrace();
		}
		proxyHost = prop.getProperty("http.proxy.host");
		proxyPort = Integer.valueOf(prop.getProperty("http.proxy.port","28129"));
		proxyUsername =  prop.getProperty("http.proxy.username");
		proxyPassword =  prop.getProperty("http.proxy.password");
//		LOG.info("proxy:" + proxyHost + ":" + proxyPort);
		try {
			useProxy = (proxyHost != null && proxyHost.length() > 0);
			if (useProxy) {
			    LOG.info("use proxy " + proxyHost + ":" + proxyPort);
			}
		} catch (NumberFormatException e ) {
			if (StringUtils.isEmpty(proxyHost)) {
				LOG.warn("http.proxy.port set error, use default:" + proxyPort);
			}
		}
		try {
			timeout = Integer.valueOf(prop.getProperty("http.timeout"));
			LOG.info("http request timeout: " + timeout);
		} catch (NumberFormatException e ) {
			LOG.warn("http.timeout set error, use default:" + timeout);
		}
		try {
		        sotimeout = Integer.valueOf(prop.getProperty("http.sotimeout"));
		        LOG.info("http socket timeout: " + sotimeout);
		} catch (NumberFormatException e ) {
		        LOG.warn("http.sotimeout set error, use default:" + sotimeout);
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
	    
		Response response = null;
		for (int i = 0; i < retryNum; i++) {
    		try {
    		    response = getResponse(page);
    		} catch (IOException e) {
    		    LOG.debug("IOException, try again");
    		   continue; 
    		} catch (Exception e) {
    		    response = null;
    		}
    		break;
		}
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
	
	static final String defaultCharset = "UTF-8"; // used if not found in header or meta charset
	private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*(?:\"|')?([^\\s,;\"']*)");
	
	// reads bytes first into a buffer, then decodes with the appropriate charset. done this way to support
    // switching the chartset midstream when a meta http-equiv tag defines the charset.
    static Document parseByteData(ByteBuffer byteData, String charsetName, String baseUri, Parser parser) {
        String docData;
        Document doc = null;
        if (charsetName == null) { // determine from meta. safe parse as UTF-8
            // look for <meta http-equiv="Content-Type" content="text/html;charset=gb2312"> or HTML5 <meta charset="gb2312">
            docData = Charset.forName(defaultCharset).decode(byteData).toString();
            doc = parser.parseInput(docData, baseUri);
            Element meta = doc.select("meta[http-equiv=content-type], meta[charset]").first();
            if (meta != null) { // if not found, will keep utf-8 as best attempt

                String foundCharset;
                if (meta.hasAttr("http-equiv")) {
                    foundCharset = getCharsetFromContentType(meta.attr("content"));
                    if (foundCharset == null && meta.hasAttr("charset")) {
                        try {
                            if (Charset.isSupported(meta.attr("charset"))) {
                                foundCharset = meta.attr("charset");
                            }
                        } catch (IllegalCharsetNameException e) {
                            foundCharset = null;
                        }
                    }
                } else {
                    foundCharset = meta.attr("charset");
                }

                if (foundCharset != null && foundCharset.length() != 0 && !foundCharset.equals(defaultCharset)) { // need to re-decode
                    foundCharset = foundCharset.trim().replaceAll("[\"']", "");
                    charsetName = foundCharset;
                    byteData.rewind();
                    docData = Charset.forName(foundCharset).decode(byteData).toString();
                    doc = null;
                }
            }
        } else { // specified by content type header (or by user on file load)
            Validate.notEmpty(charsetName, "Must set charset arg to character set of file to parse. Set to null to attempt to detect from HTML");
            docData = Charset.forName(charsetName).decode(byteData).toString();
        }
        if (doc == null) {
            // there are times where there is a spurious byte-order-mark at the start of the text. Shouldn't be present
            // in utf-8. If after decoding, there is a BOM, strip it; otherwise will cause the parser to go straight
            // into head mode
            if (docData.length() > 0 && docData.charAt(0) == 65279)
                docData = docData.substring(1);

            doc = parser.parseInput(docData, baseUri);
            doc.outputSettings().charset(charsetName);
        }
        return doc;
    }


    /**
     * Parse out a charset from a content type header. If the charset is not supported, returns null (so the default
     * will kick in.)
     * @param contentType e.g. "text/html; charset=EUC-JP"
     * @return "EUC-JP", or null if not found. Charset is trimmed and uppercased.
     */
    static String getCharsetFromContentType(String contentType) {
        if (contentType == null) return null;
        Matcher m = charsetPattern.matcher(contentType);
        if (m.find()) {
            String charset = m.group(1).trim();
            charset = charset.replace("charset=", "");
            if (charset.isEmpty()) return null;
            try {
                if (Charset.isSupported(charset)) return charset;
                charset = charset.toUpperCase(Locale.ENGLISH);
                if (Charset.isSupported(charset)) return charset;
            } catch (IllegalCharsetNameException e) {
                // if our advanced charset matching fails.... we just take the default
                return null;
            }
        }
        return null;
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
		
		Document document = null;

		ByteBuffer byteData = ByteBuffer.wrap(content);
		document = parseByteData(byteData, response.charset, u.toString(), Parser.htmlParser());
		byteData.rewind();
		
//		try {
//		     document = Jsoup.parse(in, response.charset, u.toString());
//		} catch (IllegalCharsetNameException e) {
//		    throw new IOException("Jsoup parse exception:" + e.getMessage());
//		} finally {
//		    if (in != null)
//		        in.close();
//		}
		
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
			case 305: // use proxy (Location is URL of proxy)
				protocolStatusCode = STATUS_CODE.MOVED;
				break;
			case 301: // moved permanently
			case 302: // found (temporarily moved), such as tianya mobile
			case 303: // see other (redirect after POST)
			case 307: // temporary redirect
				protocolStatusCode = STATUS_CODE.TEMP_MOVED;
				break;
			case 304: // not modified
			    return new ProtocolOutput(document); // return it
			default:
				protocolStatusCode = STATUS_CODE.MOVED;
			}
			// handle this in the higher layer.
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(protocolStatusCode, u.toString()));
		} else if (code == 400) { // bad request, mark as GONE
			LOG.error("400 Bad request: " + u);
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(STATUS_CODE.GONE, u.toString()));
		} else if (code == 401 || code == 403) { // requires authorization, but no valid
			                      // auth provided.
			LOG.error("401 Authentication Required: " + u);
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(STATUS_CODE.ACCESS_DENIED,
			        "Authentication required: " + u.toString()));
		} else if (code == 404) {
		        LOG.error("404 not found: " + u);
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(STATUS_CODE.NOTFOUND, u.toString()));
		} else if (code == 410) { // permanently GONE
		        LOG.error("410 Permanently gone: " + u);
			return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(STATUS_CODE.GONE, u.toString()));
		} else if (code == 502 | code == 504) {
		    LOG.error("Bad Gateway: " + u);
            return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(STATUS_CODE.RETRY, u.toString()));
		} else if (code == -2) {
		        LOG.error("Connection reset: " + u);
		        return new ProtocolOutput(document, ProtocolStatusUtils.makeStatus(STATUS_CODE.CONNECTION_RESET, u.toString()));
		} else {
		        LOG.error(u + " return code: " +  code + ". " + document.html());
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
