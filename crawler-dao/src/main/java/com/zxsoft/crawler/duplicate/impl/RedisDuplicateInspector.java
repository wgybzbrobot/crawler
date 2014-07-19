package com.zxsoft.crawler.duplicate.impl;

import org.springframework.stereotype.Component;

import com.zxsoft.crawler.duplicate.DuplicateInspector;

@Component
public class RedisDuplicateInspector implements DuplicateInspector {

	public boolean md5Exist(String md5) {
	    // TODO Auto-generated method stub
	    return false;
    }

}
