package com.zxsoft.crawler.web.model;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 资讯新闻详细页配置
 */
public class NewsDetailConf implements Serializable {

    private static final long serialVersionUID = 1L;

    private String host;
    @NotEmpty
    private String content;
    @NotEmpty
    private String sources;
    private String author;
    
    private String releaseDate;
    private String replyNum;
    private String forwardNum;
    private String reviewNum;
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getSources() {
        return sources;
    }
    public void setSources(String sources) {
        this.sources = sources;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    public String getReplyNum() {
        return replyNum;
    }
    public void setReplyNum(String replyNum) {
        this.replyNum = replyNum;
    }
    public String getForwardNum() {
        return forwardNum;
    }
    public void setForwardNum(String forwardNum) {
        this.forwardNum = forwardNum;
    }
    public String getReviewNum() {
        return reviewNum;
    }
    public void setReviewNum(String reviewNum) {
        this.reviewNum = reviewNum;
    }
    
    
    
}
