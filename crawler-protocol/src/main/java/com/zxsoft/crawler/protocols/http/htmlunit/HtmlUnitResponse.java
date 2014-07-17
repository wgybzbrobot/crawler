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
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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
	private String charset;
	private String proxyUsername;
	private String proxyPassword;
	private String proxyHost;
	private int proxyPort;
	private String proxyRealm;

	public HtmlUnitResponse(HtmlUnit http, URL url, Proxy proxy, boolean followRedirects) throws IOException {

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
		
		try {
			HtmlPage htmlPage = http.getClient().getPage(request);
			System.out.println(htmlPage.asXml());
	        WebResponse response = htmlPage.getWebResponse();
	        charset = response.getContentCharset();
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
//				request.close();
			}

			// Extract gzip, x-gzip and deflate content
			if (content != null) {
				// check if we have to uncompress it
				String contentEncoding = headers.get(Response.CONTENT_ENCODING);
				if (contentEncoding != null && HtmlUnit.LOG.isTraceEnabled())
				if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
					content = http.processGzipEncoded(content, url);
				} else if ("deflate".equals(contentEncoding)) {
					content = http.processDeflateEncoded(content, url);
				}
			}

		} finally {
//			get.releaseConnection();
			 http.getClient().closeAllWindows();
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
