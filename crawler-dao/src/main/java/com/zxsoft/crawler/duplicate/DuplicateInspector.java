package com.zxsoft.crawler.duplicate;

import org.springframework.stereotype.Component;

@Component
public interface DuplicateInspector {

	boolean md5Exist(String md5);
}
