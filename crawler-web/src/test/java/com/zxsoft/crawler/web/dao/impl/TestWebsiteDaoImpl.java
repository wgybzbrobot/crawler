package com.zxsoft.crawler.web.dao.impl;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thinkingcloud.framework.util.Assert;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.SiteType;
import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.web.dao.website.ConfigDao;
import com.zxsoft.crawler.web.dao.website.WebsiteDao;
import com.zxsoft.crawler.web.dao.website.impl.ConfigDaoImpl;
import com.zxsoft.crawler.web.dao.website.impl.WebsiteDaoImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class TestWebsiteDaoImpl {

	@Resource
	private ConfigDao configDao;
	
	@Resource
	private WebsiteDao websiteDaoImpl;
	
	@Test
	public void testGetListConf() {
		String url = "http://bbs.anhuinews.com/forum-319-1.html";
		ConfList listConf = configDao.getListConf(url);
		Assert.notNull(listConf);
	}
	
	private static Logger LOG = LoggerFactory.getLogger(TestWebsiteDaoImpl.class);
	
	@Test
	public void testGetWebsites() {
		Website website = new Website();
		Page<Website> page = websiteDaoImpl.getWebsites(null, 1, 10);
		
		Assert.notNull(page);
		Assert.isTrue(page.getCount() > 0);
		Assert.notEmpty(page.getRes());
	}
	
	@Test
	public void testSaveWebsite() {
		SiteType siteType = new SiteType("001");
		Website website = new Website("http://www.tencent.com", siteType,"Tencent");
		websiteDaoImpl.addWebsite(website);
	}
	
}
