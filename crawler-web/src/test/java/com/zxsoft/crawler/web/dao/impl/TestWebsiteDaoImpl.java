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

import com.zxsoft.crawler.entity.SiteType;
import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.web.dao.website.WebsiteDao;
import com.zxsoft.crawler.web.dao.website.impl.WebsiteDaoImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class TestWebsiteDaoImpl {

	@Test
	public void testGetListConf() {
		String url = "http://bbs.anhuinews.com/forum-319-1.html";
		WebsiteDaoImpl daoImpl = new WebsiteDaoImpl();
		ListConf listConf = daoImpl.getListConf(url);
		Assert.notNull(listConf);
	}
	
	@Resource
	private WebsiteDao websiteDaoImpl;
	
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
		String base64 = Base64.encodeBase64String("http://www.tencent.com".getBytes());
		Website website = new Website("http://www.tencent.com", siteType,"Tencent",  base64);
		websiteDaoImpl.addWebsite(website);
	}
	
}
