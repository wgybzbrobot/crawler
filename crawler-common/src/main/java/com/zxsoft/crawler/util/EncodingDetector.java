package com.zxsoft.crawler.util;


public class EncodingDetector {

	 public static String parseCharacterEncoding(String contentType) {
		    int start = contentType.indexOf("charset=");
		    if (start < 0)
		      return null;
		    String encoding = contentType.substring(start + 8);
		    int end = encoding.indexOf(';');
		    if (end >= 0)
		      encoding = encoding.substring(0, end);
		    encoding = encoding.trim();
		    if ((encoding.length() > 2) && (encoding.startsWith("\""))
		      && (encoding.endsWith("\"")))
		      encoding = encoding.substring(1, encoding.length() - 1);
		    return (encoding.trim());

		  }
}
