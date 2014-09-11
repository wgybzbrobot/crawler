package com.zxsoft.crawler.web.service.impl;

import org.junit.Test;

import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;

public class TestWebsiteServiceImpl {

	WebsiteServiceImpl service = new WebsiteServiceImpl();

	@Test
	public void testAddListConf() {
		ListConf listConf = new ListConf("test", "dkke", "kdjkfd", false, false, 20, "kjdkf",
		        "kdjkf", "kjdkjf", "kdjkf", "kdjfk", "kjkdjf", 3, "jkjdkf");
		service.add(listConf);
	}

	@Test
	public void testAddDetailConf() {
		DetailConf detailConf = new DetailConf("listurl", "test", "replyNum", "ekdk", "dkfd", "kekd", true,
		        "kdkf", "kdkfdj", "ekdk", "kekd", "kekjd", "ekdjd", "kekjc", "ekjkc", "kejkc",
		        "kejkjc", "kejk", "kejkd");
		service.add(detailConf);
	}
}
