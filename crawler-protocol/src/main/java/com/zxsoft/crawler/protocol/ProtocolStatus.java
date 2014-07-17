package com.zxsoft.crawler.protocol;

import java.net.URL;

import com.zxsoft.crawler.protocol.ProtocolStatusUtils;

public class ProtocolStatus {
	private URL u;
	private int code;
	private String message;

	public ProtocolStatus() {
	}

	public ProtocolStatus(URL u, int code) {
		this.u = u;
		this.code = code;
	}

	public URL getU() {
		return u;
	}

	public void setU(URL u) {
		this.u = u;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * A convenience method which returns a successful {@link ProtocolStatus}.
	 * 
	 * @return the {@link ProtocolStatus} value for 200 (success).
	 */
	public boolean isSuccess() {
		return code == ProtocolStatusUtils.SUCCESS;
	}
}
