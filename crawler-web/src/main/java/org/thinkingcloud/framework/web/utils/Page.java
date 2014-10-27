package org.thinkingcloud.framework.web.utils;

import java.util.List;


public class Page<E> {

	public static final int DEFAULT_PAGE_SIZE = 50;
	
	private List<E> res;
	private int count = 0;
	
	public List<E> getRes() {
		return res;
	}
	public Page(){
		
	}
	public Page(int count, List<E> res) {
		super();
		this.count = count;
		this.res = res;
	}
	public void setRes(List<E> res) {
		this.res = res;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
	
}
