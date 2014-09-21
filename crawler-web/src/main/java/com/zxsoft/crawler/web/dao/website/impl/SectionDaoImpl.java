package com.zxsoft.crawler.web.dao.website.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.thinkingcloud.framework.util.StringUtils;
import org.thinkingcloud.framework.web.utils.HibernateCallbackUtil;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Section;
import com.zxsoft.crawler.web.dao.website.SectionDao;

@Repository
public class SectionDaoImpl implements SectionDao {

	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	
	@Override
	public Page<Section> getSections(Section section, int pageNo, int pageSize) {
		if (pageNo <= 0) pageNo = 1;
		
		Map<String, String> params = new HashMap<String, String>();
		
		StringBuffer sb = new StringBuffer(" from Section a where 1=1 ");
		if (section != null) {
			if (!StringUtils.isEmpty(section.getUrl())) {
				sb.append(" and a.url like :url ");
				params.put("url", "%" + section.getUrl().trim() + "%");
			}
			if (!StringUtils.isEmpty(section.getCategory())) {
				sb.append(" and a.category =:category ");
				params.put("category", section.getCategory());
			}
			if (section.getWebsite() != null) {
				sb.append(" and a.website.site =:site");
				params.put("site", section.getWebsite().getSite());
			}
		}
		
		HibernateCallback<Page<Section>> action = HibernateCallbackUtil.getCallbackWithPage(sb, params, null, pageNo, pageSize);
		
		Page<Section> page =  hibernateTemplate.execute(action);
		
	    return page;
	}


	@Override
    public void saveOrUpdate(Section section) {
	    hibernateTemplate.saveOrUpdate(section);
    }

}
