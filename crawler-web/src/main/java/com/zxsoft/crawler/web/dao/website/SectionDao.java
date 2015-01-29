package com.zxsoft.crawler.web.dao.website;

import org.springframework.stereotype.Repository;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Section;

@Repository
public interface SectionDao {

	/**
	 * 获取版块
	 */
	Section getSection(Integer id);

	/**
	 * 版块的搜索接口
         */
	Page<Section> getSections(Section section, int pageNo, int pageSize);
	

	void delete(Integer id);

	void saveOrUpdate(Section section);

        void delete(Section section);
	
}
