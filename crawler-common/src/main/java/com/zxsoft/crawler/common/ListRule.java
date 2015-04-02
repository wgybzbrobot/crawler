package com.zxsoft.crawler.common;

import java.io.Serializable;

/**
 * 列表页规则
 */
public class ListRule implements Serializable {
    
    private static final long serialVersionUID = -243069564747075338L;
    
//    private String filterurlRegExp;
    private Boolean ajax;
    private String category;
    private String listdom;
    private String linedom;
    private String urldom;
    private String datedom;
    private String updatedom;
    private String synopsisdom;
    private String authordom;
    
    public ListRule() {
        // TODO Auto-generated constructor stub
    }
    
    public ListRule(Boolean ajax, String category,
                    String listdom, String linedom, String urldom,
                    String datedom, String updatedom, String synopsisdom,
                    String authordom) {
        super();
//        this.filterurlRegExp = filterurlRegExp;
        this.ajax = ajax;
        this.category = category;
        this.listdom = listdom;
        this.linedom = linedom;
        this.urldom = urldom;
        this.datedom = datedom;
        this.updatedom = updatedom;
        this.synopsisdom = synopsisdom;
        this.authordom = authordom;
    }
    
//    public String getFilterurlRegExp() {
//        return filterurlRegExp;
//    }
//    public void setFilterurlRegExp(String filterurlRegExp) {
//        this.filterurlRegExp = filterurlRegExp;
//    }
    public Boolean getAjax() {
        return ajax;
    }
    public void setAjax(Boolean ajax) {
        this.ajax = ajax;
    }
    public String getListdom() {
        return listdom;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
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
    public String getUpdatedom() {
        return updatedom;
    }
    public void setUpdatedom(String updatedom) {
        this.updatedom = updatedom;
    }
    public String getSynopsisdom() {
        return synopsisdom;
    }
    public void setSynopsisdom(String synopsisdom) {
        this.synopsisdom = synopsisdom;
    }
    public String getAuthordom() {
        return authordom;
    }
    public void setAuthordom(String authordom) {
        this.authordom = authordom;
    }
    
    
}
