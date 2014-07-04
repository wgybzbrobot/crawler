package com.zxsoft.crawler.storage;

import java.io.Serializable;
import java.util.Date;

/** 
 * 帖子URL，帖子标题，发帖时间，帖子内容，发帖人，
 * 包含图片、音频、视频URL地址
 * */
public class Forum implements Serializable{

	private static final long serialVersionUID = -892224199136821165L;

	/**
	 * 主帖url
	 */
	private String url;
	/**
	 * 主帖标题
	 */
	private String title;
	/**
	 * 发布时间
	 */
	private Date releasedate;
	private Date fetchdate;
	/**
	 * 帖子内容
	 */
	private String content;
	/**
	 * 帖子作者
	 */
	private String author;
	
	/**
	 * 跟帖量
	 */
	private int replyNum;
	private int reviewNum;
	
	/**
	 * 帖子内容中的图片url
	 */
	private String imgUrl;
	private String audioUrl;
	private String videoUrl;
	
	public Forum() {}
	
	public Forum(String title, String url, Date releaseDate, Date fetchDate) {
	    this.title = title;
	    this.url = url;
	    this.releasedate = releaseDate;
	    this.fetchdate = fetchDate;
    }
	
	public Forum(String title, String url ) {
		this.title = title;
		this.url = url;
	}
	
	public Forum(String title, String url, Date fetchDate) {
		this.title = title;
		this.url = url;
		this.fetchdate = fetchDate;
	}

	public Forum(String url, String title, Date releasedate, String content,
			String author, int replyNum, int reviewNum, String imgUrl,
			String audioUrl, String videoUrl) {
		super();
		this.url = url;
		this.title = title;
		this.releasedate = releasedate;
		this.content = content;
		this.author = author;
		this.replyNum = replyNum;
		this.reviewNum = reviewNum;
		this.imgUrl = imgUrl;
		this.audioUrl = audioUrl;
		this.videoUrl = videoUrl;
	}

	public Date getFetchdate() {
		return fetchdate;
	}

	public void setFetchdate(Date fetchDate) {
		this.fetchdate = fetchDate;
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

	public Date getReleasedate() {
		return releasedate;
	}

	public void setReleasedate(Date releasedate) {
		this.releasedate = releasedate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getReplyNum() {
		return replyNum;
	}

	public void setReplyNum(int replyNum) {
		this.replyNum = replyNum;
	}

	public int getReviewNum() {
		return reviewNum;
	}

	public void setReviewNum(int reviewNum) {
		this.reviewNum = reviewNum;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getAudioUrl() {
		return audioUrl;
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String toString() {
	    StringBuilder sb = new StringBuilder(); 
	    sb.append("currentUrl:" + url);
	    sb.append(" title: " + title);
	    sb.append(" date: " + releasedate);
	    sb.append(" content: " + content);
	    sb.append(" author: " + author);
	    sb.append(" replyNum: " + replyNum);
	    sb.append(" reviewNum: " + reviewNum);

	    return sb.toString();
	}
}