package com.zxsoft.crawler.entity;

// Generated 2014-9-19 17:19:57 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * Account generated by hbm2java
 */
@Entity
@Table(name = "account", catalog = "crawler")
public class Account implements java.io.Serializable {

	/**
	 * 
	 */
    private static final long serialVersionUID = 5502242494979474026L;
	private String id;
	private String username;
	private String password;
	private Set<Section> sections = new HashSet<Section>(0);

	public Account() {
	}

	public Account(String id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}

	public Account(String id, String username, String password, Set<Section> sections) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.sections = sections;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, length = 100)
	@GenericGenerator(name = "generator", strategy = "uuid.hex")
	@GeneratedValue(generator = "generator")
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "username", nullable = false, length = 100)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password", nullable = false, length = 100)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
	public Set<Section> getSections() {
		return this.sections;
	}

	public void setSections(Set<Section> sections) {
		this.sections = sections;
	}

}
