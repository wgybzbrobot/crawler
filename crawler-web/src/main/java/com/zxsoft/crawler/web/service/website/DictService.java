package com.zxsoft.crawler.web.service.website;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zxsoft.crawler.entity.Category;

/**
 * 字典
 */
@Service
public interface DictService {

	List<Category> getCategories();
}
