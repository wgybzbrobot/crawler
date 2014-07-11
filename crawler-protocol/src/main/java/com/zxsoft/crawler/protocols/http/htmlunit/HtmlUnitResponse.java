package com.zxsoft.crawler.protocols.http.htmlunit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;








import java.util.List;



// HTTP Client imports
import org.apache.avro.util.Utf8;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.HttpException;

// Nutch imports




import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;

import com.gargoylesoftware.htmlunit.BinaryPage;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.metadata.Metadata;
import com.zxsoft.crawler.metadata.SpellCheckedMetadata;
import com.zxsoft.crawler.net.protocols.HttpDateFormat;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.storage.WebPage;

/**
 * An HTTP response.
 *
 * @author Susam Pal
 */
public class HtmlUnitResponse implements Response {

	private URL url;
	private byte[] content;
	private int code;
	private Metadata headers = new SpellCheckedMetadata();
	
	private String proxyUsername;
	private String proxyPassword;
	private String proxyHost;
	private int proxyPort;
	private String proxyRealm;

	public HtmlUnitResponse(HtmlUnit http, URL url, Proxy proxy, WebPage page, boolean followRedirects) throws IOException {

		// Prepare GET method for HTTP request
		this.url = url;
		this.proxyUsername = proxy.getUsername();
		this.proxyPassword = proxy.getPassword();
		this.proxyHost = proxy.getHost();
		this.proxyPort = proxy.getPort();
		this.proxyRealm = proxy.getRealm();
		
		WebRequest request = new WebRequest(url);
		
		request.setProxyHost(proxyHost);
		request.setProxyPort(proxyPort);
		
		NTCredentials proxyCredentials = new NTCredentials(this.proxyUsername, this.proxyPassword,
		        proxyHost, this.proxyRealm);;
		request.setCredentials(proxyCredentials);
		if ("SOCKS".equalsIgnoreCase(proxy.getType()))
			request.setSocksProxy(true);
		else 
			request.setSocksProxy(false);
		
		if (page.getModifiedTime() > 0) {
			request.setAdditionalHeader("If-Modified-Since",
			        HttpDateFormat.toString(page.getModifiedTime()));
		}

		try {

			BinaryPage binaryPage = http.getClient().getPage(url);
	        WebResponse response = binaryPage.getWebResponse();
	        String charset = response.getContentCharset();
	        code = response.getStatusCode();
	        String contentType = response.getContentType();
	        
	        List<NameValuePair> pairs =  response.getResponseHeaders();
			for (NameValuePair pair : pairs) {
	            headers.set(pair.getName(), pair.getValue());
            }
	
			// Limit download size
	        long contentLength = Long.MAX_VALUE;
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
			
			// always read content. Sometimes content is useful to find a cause for error.
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
				// for codes other than 200 OK, we are fine with empty content
			} finally {
				if (in != null) {
					in.close();
				}
//				request.close();
			}

			StringBuilder fetchTrace = null;
			if (HtmlUnit.LOG.isTraceEnabled()) {
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
				if (contentEncoding != null && HtmlUnit.LOG.isTraceEnabled())
					fetchTrace.append("; Content-Encoding: " + contentEncoding);
				if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
					content = http.processGzipEncoded(content, url);
					if (HtmlUnit.LOG.isTraceEnabled())
						fetchTrace.append("; extracted to " + content.length + " bytes");
				} else if ("deflate".equals(contentEncoding)) {
					content = http.processDeflateEncoded(content, url);
					if (HtmlUnit.LOG.isTraceEnabled())
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
			if (HtmlUnit.LOG.isTraceEnabled()) {
				HtmlUnit.LOG.trace(fetchTrace.toString());
			}
		} finally {
//			get.releaseConnection();
		}
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
