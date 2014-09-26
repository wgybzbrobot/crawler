package com.zxsoft.crawler.web.dao.website;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.zxsoft.crawler.entity.Category;

@Repository
public interface DictDao {

	List<Category> getCategories();
}
