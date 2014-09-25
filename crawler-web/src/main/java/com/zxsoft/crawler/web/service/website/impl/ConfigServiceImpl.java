package com.zxsoft.crawler.web.service.website.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.entity.ConfDetailId;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.Section;
import com.zxsoft.crawler.web.dao.website.ConfigDao;
import com.zxsoft.crawler.web.dao.website.SectionDao;
import com.zxsoft.crawler.web.service.website.ConfigService;

@Service
public class ConfigServiceImpl implements ConfigService {

	@Autowired
	private ConfigDao configDaoImpl;
	
	@Autowired
	private SectionDao sectionDao;
	
	@Override
	public Map<String, Object> getConfig(String sectionId) {
		
		Section section = sectionDao.getSection(sectionId);
		String url = section.getUrl();
		
		ConfList confList = configDaoImpl.getConfList(url);
		List<ConfDetail> confDetails = configDaoImpl.getConfDetails(url);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("confList", confList);
		map.put("confDetails", confDetails);
		
		return map;
	}

	@Override
	public void add(ConfList listConf) {
		
		String url = listConf.getUrl();
		if (url.endsWith("/")) {
			url = url.substring(0, url.lastIndexOf("/"));
		}
		listConf.setUrl(url);
		configDaoImpl.addListConf(listConf);
		
	}

	@Transactional
	@Override
	public void add(ConfDetail detailConf, String oldHost) { 
		
		String host = detailConf.getId().getHost();
		String[] strs = host.split("\\s+");
		StringBuilder sb = new StringBuilder();
		for (String str : strs) {
			if (str.endsWith("/")) {
				str = str.substring(0, host.lastIndexOf("/"));
			}
			sb.append(str + " ");
        }
		host = sb.toString();
		detailConf.getId().setHost(host);
		
		configDaoImpl.addDetailConf(detailConf);

		/**
		 * 检测键值host是否变化, 如果有则删除
		 */
		if (!detailConf.getId().getHost().equals(oldHost)) {
			ConfDetailId id = new ConfDetailId(detailConf.getId().getListurl(), oldHost);
			configDaoImpl.deleteConfDetail(id);
		}
		
	}
}
