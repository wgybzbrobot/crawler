package com.zxsoft.crawler.dao;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.util.Assert;

import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;

public class ConfDaoTest {

//	@Test
//	public void testGetListConf() throws InterruptedException {
//		ConfDao confDao = new ConfDao();
//		ListConf listConf = confDao.getListConf("http://bbs.tianya.cn/list-free-1.shtml");
//		Assert.notNull(listConf);
//		Assert.isTrue("forum".equals(listConf.getCategory()));
//		System.out.println(listConf.getComment());
//		
//		 listConf = confDao.getListConf("http://bbs.tianya.cn/list-free-1.shtml");
//		 System.out.println(listConf.getComment());
//		 
//		 TimeUnit.SECONDS.sleep(10);
//		 listConf = confDao.getListConf("http://bbs.tianya.cn/list-free-1.shtml");
//		 System.out.println(listConf.getComment());
//	}
//	
//	@Test
//	public void testGetDetailConf() {
//	        ConfDao confDao = new ConfDao();
//	        DetailConf detailConf = confDao.getDetailConf("http://ah.anhuinews.com/shdg/wx", "http://ah.anhuinews.com");
//	        Assert.notNull(detailConf);
//	}
}
