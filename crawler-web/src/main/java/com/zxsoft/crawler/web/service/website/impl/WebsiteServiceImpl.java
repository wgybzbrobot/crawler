package com.zxsoft.crawler.web.service.website.impl;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Auth;
import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.web.dao.website.WebsiteDao;
import com.zxsoft.crawler.web.service.website.WebsiteService;

@Service
public class WebsiteServiceImpl implements WebsiteService {

	@Autowired
	private WebsiteDao websiteDao;


	@Override
    public Page<Website> getWebsite(Website website, int pageNo, int pageSize) {
		
	    return websiteDao.getWebsites(website, pageNo, pageSize);
    }

	@Override
    public void addWebsite(Website website) {
	    websiteDao.addWebsite(website);
    }
	
	public static void main(String[] args) {
		 String base64 = new String(Base64.encodeBase64("http://www.baidu.com/s?wd=%s".getBytes()));
		 System.out.println(base64);
    }

	@Override
    public void save(Website website) {
	    websiteDao.addWebsite(website);
    }

	@Override
    public Website getWebsite(String id) {
	    return websiteDao.getWebsite(id);
    }
	
	@Override
	public List<Auth> getAuths(String id) {
		return websiteDao.getAuths(id);
	}

	@Override
    public void saveAuth(Auth auth) {
	    websiteDao.addAuth(auth);
    }

	@Override
    public Auth getAuth(String id) {
	    return websiteDao.getAuth(id);
    }

	@Override
    public void deleteAuth(String id) {
		websiteDao.deleteAuth(id);
    }

}
