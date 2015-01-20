package com.zxsoft.crawler.web.service.website.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.code.Code;
import com.zxsoft.crawler.entity.Auth;
import com.zxsoft.crawler.entity.Location;
import com.zxsoft.crawler.entity.Section;
import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.web.dao.website.DictDao;
import com.zxsoft.crawler.web.dao.website.WebsiteDao;
import com.zxsoft.crawler.web.service.website.SectionService;
import com.zxsoft.crawler.web.service.website.WebsiteService;

@Service
public class WebsiteServiceImpl implements WebsiteService {

        @Autowired
        private WebsiteDao websiteDao;
        
        @Autowired
        private DictDao dictDao;

        @Override
        public Page<Website> getWebsite(Website website, int pageNo, int pageSize) {

                return websiteDao.getWebsites(website, pageNo, pageSize);
        }

        @Override
        public void addWebsite(Website website) {
                websiteDao.addWebsite(website);
        }

        @Override
        public int save(Website website) {
                Website site = new Website();
                site.setSite(website.getSite());
                Page<Website> page = websiteDao.getWebsites(site, 1, 1);
                if (StringUtils.isEmpty(website.getId()) && page != null && page.getRes() != null && page.getRes().size() > 0){
                        return Code.RECORD_EXIST;
                }
                
                websiteDao.addWebsite(website);
                
                return Code.SAVE_SUCCESS;
        }

        @Override
        public Website getWebsite(Integer id) {
                return websiteDao.getWebsite(id);
        }

        @Autowired
        private SectionService sectionService;
        
        @Override
        @Transactional
        public void deleteWebsite(Integer id) {
                Website website = websiteDao.getWebsite(id);
                Set<Section> sections = website.getSections();
                if (!CollectionUtils.isEmpty(sections)) {
                        for (Section section : sections) {
                                sectionService.delete(section);
                        }
                        
                }
                websiteDao.deleteWebsite(website);
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
