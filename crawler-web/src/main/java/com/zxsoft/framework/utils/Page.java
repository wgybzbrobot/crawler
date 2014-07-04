package com.zxsoft.framework.utils;

import java.util.List;


public class Page {

	private List res;
	private int count = 0;
	
	public List getRes() {
		return res;
	}
	public Page(){
		
	}
	public Page(int count, List res) {
		super();
		this.count = count;
		this.res = res;
	}
	public void setRes(List res) {
		this.res = res;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
	
}
