package com.zxsoft.crawler.storage;


public class Section implements java.io.Serializable {

	/**
	 * 
	 */
    private static final long serialVersionUID = -7399722190855313390L;
    private String id;
	private String url;
	private String comment;
	private String status;

	public Section() {
	}

	public Section(String url, String comment) {
		this.url = url;
		this.comment = comment;
	}

	public Section(String url) {
	    this.url = url;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
