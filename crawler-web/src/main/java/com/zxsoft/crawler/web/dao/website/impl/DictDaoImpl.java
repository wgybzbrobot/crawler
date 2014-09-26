package com.zxsoft.crawler.web.dao.website.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.thinkingcloud.framework.web.utils.HibernateCallbackUtil;

import com.zxsoft.crawler.entity.Category;
import com.zxsoft.crawler.web.dao.website.DictDao;

@Repository
public class DictDaoImpl implements DictDao {

	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	@Override
    public List<Category> getCategories() {
		return hibernateTemplate.find("from Category");
    }

	
	
}
