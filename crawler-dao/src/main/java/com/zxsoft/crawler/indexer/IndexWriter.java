package com.zxsoft.crawler.indexer;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zxsoft.crawler.storage.RecordInfo;

@Service
public interface IndexWriter {

	void write(RecordInfo info);
	
	void write(List<RecordInfo> recordInfos);
}
