package com.zxsoft.crawler.util.page;

public class PrevPageNotFoundException extends Exception {

    private static final long serialVersionUID = 2728788057297530022L;

    public PrevPageNotFoundException() {
    	super();
    }
    public PrevPageNotFoundException(String message) {
    	super(message);
    }
    public PrevPageNotFoundException(Throwable cause) {
    	super(cause);
    }
    public PrevPageNotFoundException(String message, Throwable cause) {
    	super(message, cause);
    }
}
