package com.zxsoft.crawler.parse;

public class PluginRuntimeException extends Exception {

	private static final long serialVersionUID = 1804140401686748145L;

	public PluginRuntimeException(Throwable cause) {
		super(cause);
	}

	public PluginRuntimeException(String message) {
		super(message);
	}
}
