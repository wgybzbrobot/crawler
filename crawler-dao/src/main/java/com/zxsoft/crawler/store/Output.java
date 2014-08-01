package com.zxsoft.crawler.store;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.zxsoft.crawler.storage.RecordInfo;

@Service
@Scope("prototype")
public interface Output {

	void write(RecordInfo info) throws OutputException;
	
	/**
<<<<<<< HEAD
	 * @return number of output size
=======
	 * @return the number of recordinfo
>>>>>>> e50669d800cb412e26486b3fe372c22383cbeaff
	 * @throws OutputException 
	 */
	int write(List<RecordInfo> recordInfos) throws OutputException;
}
