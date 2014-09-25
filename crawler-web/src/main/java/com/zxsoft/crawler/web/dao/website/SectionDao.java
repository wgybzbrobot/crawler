package com.zxsoft.crawler.web.dao.website;

import org.springframework.stereotype.Repository;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Section;

@Repository
public interface SectionDao {

	/**
	 * 获取版块
	 */
	Section getSection(String id);

	/**
	 * 查找版块
	 */
	Page<Section> getSections(Section section, int pageNo, int pageSize);
	
	void saveOrUpdate(Section section);
	
}
