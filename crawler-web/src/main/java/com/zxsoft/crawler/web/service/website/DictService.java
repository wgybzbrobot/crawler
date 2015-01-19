package com.zxsoft.crawler.web.service.website;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zxsoft.crawler.entity.Category;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.Location;
import com.zxsoft.crawler.entity.SiteType;

/**
 * 字典
 */
@Service
public interface DictService {

	List<Category> getCategories();
	
	List<SiteType> getSiteTypes();
	
	List<ConfList> getSearchEngines();
	
	/**
         * 获取网站所在地
         * <p> 省份 -> 城市 -> 区域
         */
        public List<Location> getLocation(int id);
}
