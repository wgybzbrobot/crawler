package com.zxsoft.crawler.storage;

import java.io.Serializable;

/**
 * 资讯新闻详细页配置
 */
public class NewsDetailConf extends SeedConf implements Serializable {

    private static final long serialVersionUID = 1L;

    private String host;
    private String title;
    private String content;
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
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
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
