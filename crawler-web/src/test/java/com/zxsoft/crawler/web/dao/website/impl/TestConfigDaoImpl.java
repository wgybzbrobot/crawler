//package com.zxsoft.crawler.web.dao.website.impl;
//
//import java.util.List;
//
//import javax.annotation.Resource;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.zxisl.commons.utils.Assert;
//import com.zxsoft.crawler.entity.ConfDetail;
//import com.zxsoft.crawler.entity.ConfDetailId;
//
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath:applicationContext.xml")
//public class TestConfigDaoImpl {
//	
//	@Resource
//	private ConfigDaoImpl configDao;
//	
//	@Test
//	public void testGetConfDetails() {
//		List<ConfDetail> confDetails = configDao.getConfDetails("http://roll.news.sina.com.cn/s/channel.php");
//		Assert.notEmpty(confDetails);
//		for (ConfDetail confDetail : confDetails) {
//	        System.out.println(confDetail.getId().getHost());
//        }
//	}
//
//}
