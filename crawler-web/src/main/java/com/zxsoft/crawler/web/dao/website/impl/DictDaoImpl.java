package com.zxsoft.crawler.web.dao.website.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.thinkingcloud.framework.web.utils.HibernateCallbackUtil;

import com.zxsoft.crawler.entity.Category;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.Location;
import com.zxsoft.crawler.entity.SiteType;
import com.zxsoft.crawler.web.dao.website.DictDao;

@Repository
public class DictDaoImpl implements DictDao {

        @Autowired
        private HibernateTemplate hibernateTemplate;

        @Override
        public List<Category> getCategories() {
                return hibernateTemplate.find("from Category");
        }

        @Override
        public List<SiteType> getSiteTypes() {
                return hibernateTemplate.find("from SiteType");
        }

        @Override
        public List<ConfList> getSearchEngines() {
                return hibernateTemplate.find("from ConfList a where a.category = 'search'");
        }

        @Override
        public List<Location> getLocationsByPid(int pid) {
                String hql = "from Location a where a.pid = ? ";
                List<Location> locations = hibernateTemplate.find(hql, pid);
                return locations;
        }

}
