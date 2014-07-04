package com.zxsoft.crawler.storage;

import java.io.Serializable;
import java.util.Date;

public class Seed implements Serializable , Cloneable{

    private static final long serialVersionUID = -8522683434376824825L;

    private int type; // 1.列表页、2.详细页首页、3.详细页回复页
    private String url;//1.列表页URL、2.详细页首页URL、3.详细页回复页URL
    private String indexUrl; // 种子首页
    private String mainUrl; // 主帖URL
    private String title;
    private Date releasedate;
    
    private boolean lose; // 标记该种子是否是丢失的种子
    private int fetchinterval;
    private Date lastfetchtime;
    private int remain;
    
    private Date limitDate; // 保存上次抓取时间
    
    @Override
	public Seed clone() throws CloneNotSupportedException {
		return (Seed) super.clone();
	}
    
    public Seed() {
    	
    }
    
    public Seed(String url) {
    	this.url = url;
    }
    
    public Seed(String url, int remain) {
    	super();
    	this.url = url;
    	this.remain = remain;
    }
    
	public Seed(String url, int remain, int type) {
	    super();
	    this.url = url;
	    this.remain = remain;
	    this.type = type;
    }
	
	public Seed(String url, int fetchinterval, int remain, int type) {
	    super();
	    this.url = url;
	    this.fetchinterval = fetchinterval;
	    this.remain = remain;
	    this.type = type;
    }

	public Seed(String url, String indexUrl, int remain, int type, String title, Date releasedate, String mainUrl, Date limitDate) {
	    super();
	    this.url = url;
	    this.indexUrl = indexUrl;
	    this.remain = remain;
	    this.type = type;
	    this.title = title;
	    this.releasedate = releasedate;
	    this.mainUrl = mainUrl;
	    this.limitDate = limitDate;
    }

	
	public Seed(String url, int type, String indexUrl, int fetchinterval, int remain, boolean lose) {
	    this.url = url;
	    this.type = type;
	    this.indexUrl = indexUrl;
	    this.fetchinterval = fetchinterval;
	    this.remain = remain;
	    this.lose = false;
    }

	public Date getLimitDate() {
		return limitDate;
	}

	public void setLimitDate(Date limitDate) {
		this.limitDate = limitDate;
	}

	public Date getLastfetchtime() {
		return lastfetchtime;
	}

	public void setLastfetchtime(Date lastfetchtime) {
		this.lastfetchtime = lastfetchtime;
	}

	public boolean isLose() {
		return lose;
	}

	public void setLose(boolean lose) {
		this.lose = lose;
	}

	public String getIndexUrl() {
		return indexUrl;
	}

	public void setIndexUrl(String indexUrl) {
		this.indexUrl = indexUrl;
	}

	public int getFetchinterval() {
		return fetchinterval;
	}

	public void setFetchinterval(int fetchinterval) {
		this.fetchinterval = fetchinterval;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getReleasedate() {
		return releasedate;
	}

	public void setReleasedate(Date releasedate) {
		this.releasedate = releasedate;
	}

	public String getMainUrl() {
		return mainUrl;
	}

	public void setMainUrl(String mainUrl) {
		this.mainUrl = mainUrl;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getRemain() {
		return remain;
	}
	public void setRemain(int remain) {
		this.remain = remain;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

    
    
}
