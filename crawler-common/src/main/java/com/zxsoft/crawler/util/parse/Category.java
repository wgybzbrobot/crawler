package com.zxsoft.crawler.util.parse;

/**
 * URL种子类别
 */
public class Category {

	public static final int LIST_PAGE = 0x1;
	public static final int DETAIL_PAGE = 0x2;
	public static final int REPLY_PAGE = 0x3;
	
	public static final int STATUS_RETRY = -0X1; // 抓取未成功，需要重试
	
}
