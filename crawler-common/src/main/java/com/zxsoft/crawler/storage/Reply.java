package com.zxsoft.crawler.storage;

import java.io.Serializable;
import java.util.Date;

/**
 * 论坛帖子回复
 */
public class Reply implements Serializable {

	private static final long serialVersionUID = 4943944090289512619L;
	
	private String id;
	/**
	 * 当该回复是“子回复”时 parentId才有值，值为“父回复”
	 */
	private String parentId; 
	/**
	 * 主帖URL
	 */
	private String mainUrl;
	/**
	 * 回复人帐号
	 */
	private String authorAccount;
	/**
	 * 回复内容
	 */
	private String content;
	
	private String videoUrl;
	private String imgUrl;
	private String audioUrl;
	/**
	 * 回复主题标题， 
	 * Note：暂时不存
	 */
	private String title;
	/**
	 * 回复时间
	 */
	private Date releasedate;
	/**
	 * 用户归属地
	 */
	private String address;
	
	/**
	 * MD5 unique id: calculate by mainUrl, author, content
	 */
	private String md5;
	/**
	 * 当前url
	 */
	private String currentUrl;
	
	/**
	 * 来源网站名
	 */
//	private String webName;
	/**
	 * 监测时间
	 */
//	private Date detectDate;

	
	
	public Reply() {}
	
	public Reply(String mainUrl, String currentUrl, String parentId) {
		this.mainUrl = mainUrl;
		this.currentUrl = currentUrl;
		this.parentId = parentId;
	}
	
	
	public Reply(String id, String parentId, String mainUrl,
			String authorAccount, String content, String videoUrl,
			String imgUrl, String audioUrl, String title, Date releasedate,
			String address, String md5, String currentUrl) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.mainUrl = mainUrl;
		this.authorAccount = authorAccount;
		this.content = content;
		this.videoUrl = videoUrl;
		this.imgUrl = imgUrl;
		this.audioUrl = audioUrl;
		this.title = title;
		this.releasedate = releasedate;
		this.address = address;
		this.md5 = md5;
		this.currentUrl = currentUrl;
	}




	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getMainUrl() {
		return mainUrl;
	}
	public void setMainUrl(String mainUrl) {
		this.mainUrl = mainUrl;
	}
	public String getAuthorAccount() {
		return authorAccount;
	}
	public void setAuthorAccount(String authorAccount) {
		this.authorAccount = authorAccount;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getCurrentUrl() {
		return currentUrl;
	}
	public void setCurrentUrl(String currentUrl) {
		this.currentUrl = currentUrl;
	}
	
	
	
}
