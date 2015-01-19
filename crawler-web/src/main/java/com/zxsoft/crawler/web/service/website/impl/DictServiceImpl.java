package com.zxsoft.crawler.web.service.website.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zxsoft.crawler.entity.Category;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.Location;
import com.zxsoft.crawler.entity.SiteType;
import com.zxsoft.crawler.web.dao.website.DictDao;
import com.zxsoft.crawler.web.service.website.DictService;

@Service
public class DictServiceImpl implements DictService {

        @Autowired
        private DictDao dictDao;

        @Override
        public List<Category> getCategories() {
                return dictDao.getCategories();
        }

        @Override
        public List<SiteType> getSiteTypes() {
                return dictDao.getSiteTypes();
        }

        @Override
        public List<ConfList> getSearchEngines() {
                return dictDao.getSearchEngines();
        }

        @Override
        public List<Location> getLocation(int id) {
                List<Location> list = dictDao.getLocationsByPid(id);
                return list;
        }

}
