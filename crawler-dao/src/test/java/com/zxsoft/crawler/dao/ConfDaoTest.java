package com.zxsoft.crawler.dao;

import org.junit.Test;
import org.springframework.util.Assert;

import com.zxsoft.crawler.storage.ListConf;

public class ConfDaoTest {

	@Test
	public void testGetListConf() {
		ConfDao confDao = new ConfDao();
		ListConf listConf = confDao.getListConf("http://bbs.tianya.cn/list-free-1.shtml");
		Assert.notNull(listConf);
		Assert.isTrue("forum".equals(listConf.getCategory()));
	}
}
