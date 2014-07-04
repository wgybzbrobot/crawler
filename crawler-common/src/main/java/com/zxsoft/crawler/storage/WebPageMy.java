package com.zxsoft.crawler.storage;

import org.jsoup.nodes.Document;


/**
 * contain web page info.
 */
public class WebPageMy {

    private Document document;
    private String host;
    private int status;
    
    private ListConf listConf;
    private Seed seed;
    
    public WebPageMy() {}
    
	public Seed getSeed() {
		return seed;
	}

	public void setSeed(Seed seed) {
		this.seed = seed;
	}

    public ListConf getListConf() {
		return listConf;
	}

	public void setListConf(ListConf listConf) {
		this.listConf = listConf;
	}

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
}
