package com.zxsoft.crawler.parse;

/**
 * 保存在列表页获取到的数据
 */
public class ExtInfo {

    private long timestamp;
    
    private long update;
    
    private String author;
    
    private String homeUrl;
    
    private String identify_md5;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getUpdate() {
        return update;
    }

    public void setUpdate(long update) {
        this.update = update;
    }

    public String getAuthor() {
        return author;
    }

    public String getIdentify_md5() {
        return identify_md5;
    }

    public void setIdentify_md5(String identify_md5) {
        this.identify_md5 = identify_md5;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getHomeUrl() {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public ExtInfo () {
        
    }
    
    public ExtInfo(long timestamp, long update, String author, String homeUrl) {
        super();
        this.timestamp = timestamp;
        this.update = update;
        this.author = author;
        this.homeUrl = homeUrl;
    }
    
    
}
