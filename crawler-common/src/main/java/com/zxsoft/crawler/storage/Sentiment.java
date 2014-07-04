package com.zxsoft.crawler.storage;

import java.io.Serializable;
import java.util.Date;

public class Sentiment implements Serializable {

    private static final long serialVersionUID = 2458073063771201518L;
    
    private String title;
    private String url;
    private Date releaseDate;
    private Date fetchDate;

    public Sentiment () { }
    
    public Sentiment(String title, String url, Date releaseDate, Date fetchDate) {
        this.title = title;
        this.url = url;
        this.releaseDate = releaseDate;
        this.fetchDate = fetchDate;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
    
    
    
}
