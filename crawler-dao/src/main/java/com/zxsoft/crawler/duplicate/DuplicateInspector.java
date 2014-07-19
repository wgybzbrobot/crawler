package com.zxsoft.crawler.duplicate;

import org.springframework.stereotype.Service;

@Service
public interface DuplicateInspector {

	boolean md5Exist(String md5);
}
