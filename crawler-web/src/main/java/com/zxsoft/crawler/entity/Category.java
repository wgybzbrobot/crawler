package com.zxsoft.crawler.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author xiayun
 */
@Entity
@Table (name = "category", catalog = "crawler")
public class Category implements Serializable {

	/**
	 * 
	 */
    private static final long serialVersionUID = 5751299099568350598L;
	private String id;
	private String comment;
	private	Set<Section> sections = new HashSet<Section>(0);
	
	public Category() {}
	
	public Category(String id, String comment) {
		this.id = id;
		this.comment = comment;
	}

	public Category(String id, String comment, Set<Section> sections) {
	    super();
	    this.id = id;
	    this.comment = comment;
	    this.sections = sections;
    }

	@Id
	@Column(name = "id", unique = true, nullable = false, length = 45)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column (name = "comment", nullable = false, length = 100)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
	public Set<Section> getSections() {
		return sections;
	}

	public void setSections(Set<Section> sections) {
		this.sections = sections;
	}
	
	
}
