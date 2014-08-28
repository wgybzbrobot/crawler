package com.zxsoft.crawler.auth;

public class LoginException extends Exception {

    private static final long serialVersionUID = 6189626974635420620L;

    public LoginException() {
		super();
	}
	public LoginException(String message) {
		super(message);
	}
	public LoginException(Throwable cause) {
		super(cause);
	}
}
