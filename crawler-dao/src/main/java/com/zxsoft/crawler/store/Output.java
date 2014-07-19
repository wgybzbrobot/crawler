package com.zxsoft.crawler.store;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zxsoft.crawler.storage.RecordInfo;

@Service
public interface Output {

	void write(RecordInfo info);
	
	void write(List<RecordInfo> recordInfos);
}
