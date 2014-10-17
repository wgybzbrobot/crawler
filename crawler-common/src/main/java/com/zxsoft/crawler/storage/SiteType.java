package com.zxsoft.crawler.storage;


public class SiteType implements java.io.Serializable {

	/**
	 * 
	 */
    private static final long serialVersionUID = 9139932773606914700L;
	private String type;
	private String comment;

	public SiteType() {
	}

	public SiteType(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
