package com.zxsoft.crawler.storage;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.Region;

/**
 * 所有"列表页"配置
 * <p>包括‘资讯新闻’、‘论坛’等
 */
public class ListConf implements Serializable{

    private static final long serialVersionUID = 3603496298063039907L;
    @NotEmpty
    private String comment;
    @NotEmpty
	@Pattern(regexp="^(https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
    private String url;		// 种子url地址
    private String category;	// 类型：news, forum, ...
    private boolean ajax;	// 是否ajax请求
    @Min(0)
    private int fetchinterval;
//    @Min(1)
//    private int pageNum;  // 翻页数
    private String filterurl;
    @NotEmpty
    private String listdom;
    @NotEmpty
    private String linedom; // 列表页面中一行的DOM
    @NotEmpty
    private String urldom; // 一行信息的url DOM
    private String datedom; // 发布时间DOM
    private String updatedom; // 论坛类最近更新时间DOM
    private int numThreads;
	
    public ListConf() {}
    
	public ListConf(String comment, String url, String category, boolean ajax, int fetchinterval, /*int pageNum,*/
            String filterurl, String listdom, String linedom, String urldom, String datedom,
            String updatedom, int numThreads) {
	    super();
	    this.comment = comment;
	    this.url = url;
	    this.category = category;
	    this.ajax = ajax;
	    this.fetchinterval = fetchinterval;
//	    this.pageNum = pageNum;
	    this.filterurl = filterurl;
	    this.listdom = listdom;
	    this.linedom = linedom;
	    this.urldom = urldom;
	    this.datedom = datedom;
	    this.updatedom = updatedom;
	    this.numThreads = numThreads;
    }
	
	public boolean isAjax() {
		return ajax;
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public int getNumThreads() {
		return numThreads;
	}
	public String getUpdatedom() {
		return updatedom;
	}
	public void setUpdatedom(String updatedom) {
		this.updatedom = updatedom;
	}
	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}
	public int getFetchinterval() {
		return fetchinterval;
	}
	public void setFetchinterval(int fetchinterval) {
		this.fetchinterval = fetchinterval;
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
	public String getUrldom() {
        return urldom;
    }
    public void setUrldom(String urldom) {
        this.urldom = urldom;
    }
//    public boolean isAjax() {
//        return ajax;
//    }
//    public void setAjax(boolean ajax) {
//        this.ajax = ajax;
//    }
  
   /* public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}*/
	public String getLinedom() {
        return linedom;
    }
    public void setLinedom(String linedom) {
        this.linedom = linedom;
    }
    public String getDatedom() {
        return datedom;
    }
    public void setDatedom(String datedom) {
        this.datedom = datedom;
    }
//    public String getFetchurl() {
//        return fetchurl;
//    }
//    public void setFetchurl(String fetchurl) {
//        this.fetchurl = fetchurl;
//    }
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

//	public String getPage() {
//        return page;
//    }
//    public void setPage(String page) {
//        this.page = page;
//    }
    

}
