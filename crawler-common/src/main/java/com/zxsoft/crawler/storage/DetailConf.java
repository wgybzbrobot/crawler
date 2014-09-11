package com.zxsoft.crawler.storage;

import java.io.Serializable;

/**
 * 详细页配置
 */
public class DetailConf implements Serializable {

    private static final long serialVersionUID = -7536047593216572652L;
    private String listUrl;
    private String host;
	private String replyNum;
	private String reviewNum;
	private String forwardNum;
	private String sources;
	private boolean fetchorder; // 抓取顺序，true从最后一页开始抓

	// 主帖模块
	private String master;
	private String author;
	private String date;
	private String content;

	// 回复
	private String reply;
	private String replyAuthor;
	private String replyDate;
	private String replyContent;

	// 子回复
	private String subReply;
	private String subReplyAuthor;
	private String subReplyDate;
	private String subReplyContent;

	
	
	public DetailConf(String listUrl, String host, String replyNum, String reviewNum, String forwardNum,
            String sources, boolean fetchorder, String master, String author, String date,
            String content, String reply, String replyAuthor, String replyDate,
            String replyContent, String subReply, String subReplyAuthor, String subReplyDate,
            String subReplyContent) {
	    super();
	    this.listUrl = listUrl;
	    this.host = host;
	    this.replyNum = replyNum;
	    this.reviewNum = reviewNum;
	    this.forwardNum = forwardNum;
	    this.sources = sources;
	    this.fetchorder = fetchorder;
	    this.master = master;
	    this.author = author;
	    this.date = date;
	    this.content = content;
	    this.reply = reply;
	    this.replyAuthor = replyAuthor;
	    this.replyDate = replyDate;
	    this.replyContent = replyContent;
	    this.subReply = subReply;
	    this.subReplyAuthor = subReplyAuthor;
	    this.subReplyDate = subReplyDate;
	    this.subReplyContent = subReplyContent;
    }
	
	private String testUrl;
	
	public String getListUrl() {
		return listUrl;
	}

	public void setListUrl(String listUrl) {
		this.listUrl = listUrl;
	}

	public String getTestUrl() {
		return testUrl;
	}

	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}

	public DetailConf() {}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getReplyNum() {
		return replyNum;
	}

	public void setReplyNum(String replyNum) {
		this.replyNum = replyNum;
	}

	public String getReviewNum() {
		return reviewNum;
	}

	public void setReviewNum(String reviewNum) {
		this.reviewNum = reviewNum;
	}

	public String getForwardNum() {
		return forwardNum;
	}

	public void setForwardNum(String forwardNum) {
		this.forwardNum = forwardNum;
	}

	public String getSources() {
		return sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}

	public boolean isFetchorder() {
		return fetchorder;
	}

	public void setFetchorder(boolean fetchorder) {
		this.fetchorder = fetchorder;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getReplyAuthor() {
		return replyAuthor;
	}

	public void setReplyAuthor(String replyAuthor) {
		this.replyAuthor = replyAuthor;
	}

	public String getReplyDate() {
		return replyDate;
	}

	public void setReplyDate(String replyDate) {
		this.replyDate = replyDate;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public String getSubReply() {
		return subReply;
	}

	public void setSubReply(String subReply) {
		this.subReply = subReply;
	}

	public String getSubReplyAuthor() {
		return subReplyAuthor;
	}

	public void setSubReplyAuthor(String subReplyAuthor) {
		this.subReplyAuthor = subReplyAuthor;
	}

	public String getSubReplyDate() {
		return subReplyDate;
	}

	public void setSubReplyDate(String subReplyDate) {
		this.subReplyDate = subReplyDate;
	}

	public String getSubReplyContent() {
		return subReplyContent;
	}

	public void setSubReplyContent(String subReplyContent) {
		this.subReplyContent = subReplyContent;
	}

	
}
