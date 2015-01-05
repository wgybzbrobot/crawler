package com.zxsoft.crawler.web.service.website.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxsoft.crawler.entity.Section;
import com.zxsoft.crawler.web.dao.website.SectionDao;
import com.zxsoft.crawler.web.service.website.SectionService;

@Service
public class SectionServiceImpl implements SectionService {

        @Autowired
        private SectionDao sectionDao;

        @Override
        public Section getSection(String sectionId) {
                return sectionDao.getSection(sectionId);
        }

        @Override
        public Page<Section> getSections(Section section, int pageNo, int pageSize) {
                return sectionDao.getSections(section, pageNo, pageSize);
        }

        /**
	 * 新增或修改版块
	 */
        @Override
        public void saveOrUpdate(Section section) {

                String url = section.getUrl();
                if (url.endsWith("/")) {
                        url = url.substring(0, url.lastIndexOf("/"));
                }
                section.setUrl(url);

                sectionDao.saveOrUpdate(section);
        }

        @Override
        public void delete(String id) {
                sectionDao.delete(id);
        }

        @Override
        public void delete(Section section) {
                sectionDao.delete(section);
        }

        @Override
        public Section getSectionByUrl(String url) {
                Section section = new Section(url);
                Page<Section> page = sectionDao.getSections(section, 1, 10);
                List<Section> sections = page.getRes();
                if (CollectionUtils.isEmpty(sections))
                        return null;
                return sections.get(0);
        }

}
