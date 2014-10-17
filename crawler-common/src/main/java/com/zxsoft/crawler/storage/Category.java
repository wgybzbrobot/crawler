package com.zxsoft.crawler.storage;

import java.io.Serializable;

/**
 * @author xiayun
 */
public class Category implements Serializable {

	/**
	 * 
	 */
    private static final long serialVersionUID = 5751299099568350598L;
	private String id;
	private String comment;
	
	public Category() {}
	
	public Category(String id, String comment) {
		this.id = id;
		this.comment = comment;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	
}
