//package com.zxsoft.crawler.util;
//
//import org.thinkingcloud.framework.util.StringUtils;
//
//import com.ibm.icu.text.CharsetDetector;
//import com.ibm.icu.text.CharsetMatch;
//
//public class EncodingDetector {
//
//	private static final String DEFAULT_ENCODE = "GB18030";
//	
//	public static String parseCharacterEncoding(String contentType, byte[] content) {
//		if (StringUtils.isEmpty(contentType)) {
//		        
//			return getEncode(content);
//		}
//
//		int start = contentType.indexOf("charset=");
//		if (start < 0)
//			return getEncode(content);
//		
//		String encoding = contentType.substring(start + 8);
//		int end = encoding.indexOf(';');
//		if (end >= 0)
//			encoding = encoding.substring(0, end);
//		encoding = encoding.trim();
//		if ((encoding.length() > 2) && (encoding.startsWith("\""))
//		        && (encoding.endsWith("\"")))
//			encoding = encoding.substring(1, encoding.length() - 1);
//		return (encoding.trim());
//
//	}
//	
//	public static String getEncode(byte[] data){
//		   CharsetDetector detector = new CharsetDetector();
//		   detector.setText(data);
//		   CharsetMatch match = detector.detect();
//		   return match.getName();
//		}
//}
