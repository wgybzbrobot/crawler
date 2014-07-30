package com.zxsoft.crawler.store;

public class OutputException extends Exception {

    private static final long serialVersionUID = 3778962257802903576L;

    public OutputException() {}
    
    public OutputException(String message) {
    	super(message);
    }

    public OutputException(Throwable cause) {
    	super(cause);
    }
    
    public OutputException(String message, Throwable cause) {
    	super(message, cause);
    }
}
