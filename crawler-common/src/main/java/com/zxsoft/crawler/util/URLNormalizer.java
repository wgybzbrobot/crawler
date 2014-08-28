package com.zxsoft.crawler.util;

import java.net.URLDecoder;

import org.thinkingcloud.framework.util.StringUtils;

public class URLNormalizer {

    public static String normalizeUrl(String url) {
        if (url.indexOf("#") != -1) {
            url = url.substring(0, url.indexOf("#"));
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.indexOf("/"));
        }
        return url;
    }
    
    public static String normalize(String url) {
    	if (StringUtils.isEmpty(url)) {
    		return url;
    	}
    	if (!url.startsWith("http")) {
    		url = "http://" + url;
    	}
    	if (url.matches(".*(\\n|\\t|\\s).*")) {
    		url = url.replaceAll("(\\n|\\t|\\s)", "");
    	}
    	return url;
    }
    

}
