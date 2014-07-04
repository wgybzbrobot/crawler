package com.zxsoft.crawler.storage;

import java.io.Serializable;
import java.util.Date;

public class News implements Serializable {

    private static final long serialVersionUID = -3371911356029728862L;
    
    private String url;
    private String title;
    private String content;
    /**
     * 来源 
     */
    private String sources;
    private String img;
    private String audio;
    private String video;
    private String author;
    private Date releaseDate;
    private Date fetchDate;
    
    private int replyNum;
    private int forwardNum;
    private int reviewNum;
    
    
    public News(String url) {
        super();
        this.url = url;
    }
    
    public News(String url, Date releaseDate, Date fetchDate) {
        this.url = url;
        this.releaseDate = releaseDate;
        this.fetchDate = fetchDate;
    }
    
    public News(String title, String url, Date releaseDate, Date fetchDate) {
        this.title = title;
        this.url = url;
        this.releaseDate = releaseDate;
        this.fetchDate = fetchDate;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
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
    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public String getAudio() {
        return audio;
    }
    public void setAudio(String audio) {
        this.audio = audio;
    }
    public String getVideo() {
        return video;
    }
    public void setVideo(String video) {
        this.video = video;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public Date getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
    public Date getFetchDate() {
        return fetchDate;
    }
    public void setFetchDate(Date fetchDate) {
        this.fetchDate = fetchDate;
    }
    public int getReplyNum() {
        return replyNum;
    }
    public void setReplyNum(int replyNum) {
        this.replyNum = replyNum;
    }
    public int getForwardNum() {
        return forwardNum;
    }
    public void setForwardNum(int forwardNum) {
        this.forwardNum = forwardNum;
    }
    public int getReviewNum() {
        return reviewNum;
    }
    public void setReviewNum(int reviewNum) {
        this.reviewNum = reviewNum;
    }
    

}
