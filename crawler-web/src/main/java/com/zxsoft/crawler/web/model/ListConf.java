package com.zxsoft.crawler.web.model;


import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

public class ListConf {
	
	@NotEmpty
	private String comment;
	
	@NotEmpty
	@Pattern(regexp="^(https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
	private String url;
	
	private String category;
	
	private boolean ajax;
	@Min(0)
	private int fetchinterval;
	@Min(1)
	private int pageNum = 1;
	
//	private String fetchurl;
	private String filterurl;
	
	@NotEmpty
	private String listdom;
	@NotEmpty
	private String linedom;
	@NotEmpty
	private String urldom;
	private String datedom;
	private String updatedatedom; // 论坛类最近更新时间DOM
	private String numThreads;
	
	public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Comment: " + comment);
        sb.append(", url: " + url);
        sb.append(", category: " + category);
        sb.append(", ajax: " + ajax);
        sb.append(", fetchinterval: " + fetchinterval);
        sb.append(", pageNum: " + pageNum);
//        sb.append(", fetchurl: " + fetchurl);
        sb.append(", filterurl: " + filterurl);
        sb.append(", listdom: " + listdom);
        sb.append(", linedom: " + linedom);
        return sb.toString();
    }
	

	public String getNumThreads() {
		return numThreads;
	}


	public void setNumThreads(String numThreads) {
		this.numThreads = numThreads;
	}


	public String getUpdatedatedom() {
		return updatedatedom;
	}

	public void setUpdatedatedom(String updatedatedom) {
		this.updatedatedom = updatedatedom;
	}


	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
    public boolean isAjax() {
		return ajax;
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public int getFetchinterval() {
		return fetchinterval;
	}

	public void setFetchinterval(int fetchinterval) {
		this.fetchinterval = fetchinterval;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

/*	public String getFetchurl() {
		return fetchurl;
	}

	public void setFetchurl(String fetchurl) {
		this.fetchurl = fetchurl;
	}*/

	public String getFilterurl() {
		return filterurl;
	}

	public void setFilterurl(String filterurl) {
		this.filterurl = filterurl;
	}

	public String getListdom() {
		return listdom;
	}

	public void setListdom(String listdom) {
		this.listdom = listdom;
	}

	public String getLinedom() {
		return linedom;
	}

	public void setLinedom(String linedom) {
		this.linedom = linedom;
	}

	public String getUrldom() {
		return urldom;
	}

	public void setUrldom(String urldom) {
		this.urldom = urldom;
	}

	public String getDatedom() {
		return datedom;
	}

	public void setDatedom(String datedom) {
		this.datedom = datedom;
	}

}
