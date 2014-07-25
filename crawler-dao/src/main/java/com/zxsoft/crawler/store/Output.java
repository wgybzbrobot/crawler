package com.zxsoft.crawler.store;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.zxsoft.crawler.storage.RecordInfo;

@Service
@Scope("prototype")
public interface Output {

	void write(RecordInfo info);
	
	/**
	 * @return the number of recordinfo
	 */
	int write(List<RecordInfo> recordInfos);
}
