package com.zxsoft.crawler.dao;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseDao {

	protected static final String TABLE_CONF_LIST = "conf_list";
	protected static final String TABLE_CONF_DETAIL = "conf_detail";
	protected static final String TABLE_SEARCH_ENGINE = "search_engine";
	protected static final String TABLE_AUTH = "auth";
	protected static final String TABLE_PROXY = "proxy";
	protected static final String TABLE_WEBSITE = "website";
	
	protected static JdbcTemplate jdbcTemplate;

	static {
		Resource resource = new ClassPathResource("dao.xml");
		BeanFactory factory = new XmlBeanFactory(resource);
		jdbcTemplate = (JdbcTemplate) factory.getBean("jdbcTemplate");
	}
}
