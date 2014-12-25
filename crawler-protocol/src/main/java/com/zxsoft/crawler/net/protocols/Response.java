package com.zxsoft.crawler.net.protocols;

// JDK imports
import java.net.URL;

import org.apache.tika.metadata.Metadata;
// Nutch imports
import com.zxsoft.crawler.metadata.HttpHeaders;
//import com.zxsoft.crawler.metadata.Metadata;

public class Response extends HttpHeaders {

	/** Returns the URL used to retrieve this response. */
	public URL url;

	/** Returns the response code. */
	public int code;

	/** Returns the value of a named header. */
	public String getHeader(String name) {
		return headers.get(name);
	}

	/** Returns all the headers. */
	public Metadata headers;

	/** Returns the full content of the response. */
	public byte[] content;

	public String charset;

	public Response(URL url, int code, Metadata headers, byte[] content, String charset) {
		super();
		this.url = url;
		this.code = code;
		this.headers = headers;
		this.content = content;
		this.charset = charset;
	}

}
