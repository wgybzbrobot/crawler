package com.zxsoft.crawler.duplicate;

import org.springframework.stereotype.Component;

@Component
public interface DuplicateInspector {

	boolean md5Exist(String md5);
	
	void addMd5(String md5);
}
