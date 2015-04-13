package org.thinkingcloud.framework.web.utils;

import java.util.List;


public class Page<E> {

	public static final int DEFAULT_PAGE_SIZE = 20;
	
	private List<E> res;
	private long count = 0;
	
	public List<E> getRes() {
		return res;
	}
	public Page(){
		
	}
	public Page(long count, List<E> res) {
		super();
		this.count = count;
		this.res = res;
	}
	public void setRes(List<E> res) {
		this.res = res;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	
	
}
