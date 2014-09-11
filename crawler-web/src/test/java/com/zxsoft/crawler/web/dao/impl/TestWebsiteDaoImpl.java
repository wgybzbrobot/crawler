package com.zxsoft.crawler.web.dao.impl;

import org.junit.Test;
import org.thinkingcloud.framework.util.Assert;

import com.zxsoft.crawler.storage.ListConf;

public class TestWebsiteDaoImpl {

	@Test
	public void testGetListConf() {
		String url = "http://bbs.anhuinews.com/forum-319-1.html";
		WebsiteDaoImpl daoImpl = new WebsiteDaoImpl();
		ListConf listConf = daoImpl.getListConf(url);
		Assert.notNull(listConf);
	}
}
