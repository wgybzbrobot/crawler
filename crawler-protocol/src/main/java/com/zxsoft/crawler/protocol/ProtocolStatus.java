package com.zxsoft.crawler.protocol;

public class ProtocolStatus {

	public enum STATUS_CODE {
		SUCCESS, INVALID_URL, FAILED, GONE, NOTFOUND, RETRY, TIMEOUT,
		ROBOTS_DENIED, MOVED, TEMP_MOVED, EXCEPTION, ACCESS_DENIED, NOTMODIFIED,
		CONNECTION_RESET
	}

	private String u;
	private STATUS_CODE code;
	private String message;

	public String toString () {
	    return u + "\t" + code.name() + "\t" + message;
	}
	
	
	public ProtocolStatus() {
	}

	public ProtocolStatus(STATUS_CODE code) {
		this.code = code;
	}

	public ProtocolStatus(String u) {
		this.u = u;
	}

	public ProtocolStatus(String u, STATUS_CODE code) {
		this.u = u;
		this.code = code;
	}
	
	public ProtocolStatus(String u, STATUS_CODE code, String message) {
		this.u = u;
		this.code = code;
		this.message = message;
	}

	public STATUS_CODE getCode() {
		return code;
	}

	public void setCode(STATUS_CODE code) {
		this.code = code;
	}

	public String getU() {
		return u;
	}

	public void setU(String u) {
		this.u = u;
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
		return code == STATUS_CODE.SUCCESS;
	}
}
