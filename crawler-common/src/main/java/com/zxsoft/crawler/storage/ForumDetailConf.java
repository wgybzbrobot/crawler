package com.zxsoft.crawler.storage;

import java.io.Serializable;

/**
 * 论坛配置
 */
public class ForumDetailConf extends SeedConf implements Serializable {

    private static final long serialVersionUID = -7536047593216572652L;

    private String host;
    private String comment;
	
	private String replyNum;
	private String forwardNum;
	private String reviewNum;
	
//	private String pagebar;	// 翻页条DOM
	private boolean fetchorder; // 抓取顺序，true从最后一页开始抓

	// 主帖模块
	private String master;
	private String masterAuthor;
	private String masterDate;
	private String masterContent;

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

	public ForumDetailConf() {}
	
	public ForumDetailConf(String host, String comment, String replyNum, String forwardNum,
            String reviewNum, /*String pagebar,*/ boolean fetchorder, String master,
            String masterAuthor, String masterDate, String masterContent, String reply,
            String replyAuthor, String replyDate, String replyContent, String subReply,
            String subReplyAuthor, String subReplyDate, String subReplyContent) {
	    super();
	    this.host = host;
	    this.comment = comment;
	    this.replyNum = replyNum;
	    this.forwardNum = forwardNum;
	    this.reviewNum = reviewNum;
//	    this.pagebar = pagebar;
	    this.fetchorder = fetchorder;
	    this.master = master;
	    this.masterAuthor = masterAuthor;
	    this.masterDate = masterDate;
	    this.masterContent = masterContent;
	    this.reply = reply;
	    this.replyAuthor = replyAuthor;
	    this.replyDate = replyDate;
	    this.replyContent = replyContent;
	    this.subReply = subReply;
	    this.subReplyAuthor = subReplyAuthor;
	    this.subReplyDate = subReplyDate;
	    this.subReplyContent = subReplyContent;
    }

	public boolean isFetchorder() {
		return fetchorder;
	}

	public void setFetchorder(boolean fetchorder) {
		this.fetchorder = fetchorder;
	}

//	public String getPagebar() {
//		return pagebar;
//	}
//
//	public void setPagebar(String pagebar) {
//		this.pagebar = pagebar;
//	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}


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

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public String getMasterAuthor() {
		return masterAuthor;
	}

	public void setMasterAuthor(String masterAuthor) {
		this.masterAuthor = masterAuthor;
	}

	public String getMasterContent() {
		return masterContent;
	}

	public void setMasterContent(String masterContent) {
		this.masterContent = masterContent;
	}

	public String getMasterDate() {
		return masterDate;
	}

	public void setMasterDate(String masterDate) {
		this.masterDate = masterDate;
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
