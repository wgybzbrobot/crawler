package com.zxsoft.crawler.protocols.http;

public class CookieNotFoundException extends Exception {

	/**
	 * 
	 */
    private static final long serialVersionUID = -5176665604484092949L;

	public CookieNotFoundException() {
		super();
	}
	
	public CookieNotFoundException(String msg) {
		super(msg);
	}
}
