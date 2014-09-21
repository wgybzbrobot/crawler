package com.zxsoft.crawler.web.service.website.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Section;
import com.zxsoft.crawler.web.dao.website.SectionDao;
import com.zxsoft.crawler.web.service.website.SectionService;

@Service
public class SectionServiceImpl implements SectionService {

	@Autowired
	private SectionDao sectionDaoImpl;
	
	@Override
	public Page<Section> getSections(Section section, int pageNo, int pageSize) {
		return sectionDaoImpl.getSections(section, pageNo, pageSize);
	}

	@Override
    public void saveOrUpdate(Section section) {
	    sectionDaoImpl.saveOrUpdate(section);
    }

}
