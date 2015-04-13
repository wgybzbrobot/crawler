package com.zxsoft.crawler.urlbase;

import java.util.List;

public class Page<E> {

    private int count;
    
    private List<E> res;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<E> getRes() {
        return res;
    }

    public void setRes(List<E> res) {
        this.res = res;
    }
    
    
    
}
