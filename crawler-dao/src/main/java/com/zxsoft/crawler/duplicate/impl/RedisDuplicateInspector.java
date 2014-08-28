package com.zxsoft.crawler.duplicate.impl;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.zxsoft.crawler.duplicate.DuplicateInspector;

//@Component
public class RedisDuplicateInspector implements DuplicateInspector {

//	private RestTemplate restTemplate;
	
	public boolean md5Exist(String md5) {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public void addMd5(String md5) {
	    // TODO Auto-generated method stub
	    
    }

}
