package com.zxsoft.crawler.util.page;

public class NextPageNotFoundException extends Exception {

    private static final long serialVersionUID = 2728788057297530022L;

    public NextPageNotFoundException() {
    	super();
    }
    public NextPageNotFoundException(String message) {
    	super(message);
    }
    public NextPageNotFoundException(Throwable cause) {
    	super(cause);
    }
    public NextPageNotFoundException(String message, Throwable cause) {
    	super(message, cause);
    }
}
