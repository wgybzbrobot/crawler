package com.zxsoft.crawler.web.service.website.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zxsoft.crawler.entity.Category;
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

}
