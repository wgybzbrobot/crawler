package com.zxsoft.crawler.web.model;



import org.hibernate.validator.constraints.NotEmpty;

public class ForumDetailConf {
	
//	@NotEmpty
//	private String comment;
	
//	@NotEmpty
//	@Pattern(regexp="^(https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
	private String host; // get through testUrl
	
	@NotEmpty
	private String replyNum;

	private String reviewNum;

	private String forwardNum;
//	@NotEmpty
//	private String pagebar;
	
	private boolean fetchorder;
	
	@NotEmpty
	private String master;
	@NotEmpty
	private String masterAuthor;
	@NotEmpty
	private String masterDate;
	@NotEmpty
	private String masterContent;

	@NotEmpty
	private String reply;
	@NotEmpty
	private String replyAuthor;
	@NotEmpty
	private String replyDate;
	@NotEmpty
	private String replyContent;

	private String subReply;
	private String subReplyAuthor;
	private String subReplyDate;
	private String subReplyContent;
	
	
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
	public String getMasterAuthor() {
		return masterAuthor;
	}
	public void setMasterAuthor(String masterAuthor) {
		this.masterAuthor = masterAuthor;
	}
	public String getMasterDate() {
		return masterDate;
	}
	public void setMasterDate(String masterDate) {
		this.masterDate = masterDate;
	}
	public String getMasterContent() {
		return masterContent;
	}
	public void setMasterContent(String masterContent) {
		this.masterContent = masterContent;
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
