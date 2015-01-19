package com.zxsoft.crawler.web.dao.website;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.zxsoft.crawler.entity.Category;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.Location;
import com.zxsoft.crawler.entity.SiteType;

@Repository
public interface DictDao {

	List<Category> getCategories();

	List<SiteType> getSiteTypes();
	
	List<ConfList> getSearchEngines();
	
	List<Location> getLocationsByPid(int pid);
}
