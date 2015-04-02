package com.zxsoft.crawler.store;

import java.util.List;
import com.zxsoft.crawler.storage.RecordInfo;

public interface Output {

	int write(RecordInfo info);
	
	/**
	 * @return number of output size
	 * @throws OutputException 
	 */
	int write(List<RecordInfo> recordInfos) ;
}
