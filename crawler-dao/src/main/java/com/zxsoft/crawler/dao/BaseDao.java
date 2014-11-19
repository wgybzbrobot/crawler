package com.zxsoft.crawler.dao;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings("deprecation")
public abstract class BaseDao {

	protected static final String TABLE_CONF_LIST = "conf_list";
	protected static final String TABLE_CONF_DETAIL = "conf_detail";
	protected static final String TABLE_SEARCH_ENGINE = "search_engine";
	protected static final String TABLE_AUTH = "auth";
	protected static final String TABLE_PROXY = "proxy";
	protected static final String TABLE_WEBSITE = "website";
	
	private static final BeanFactory factory;
	private JdbcTemplate jdbcTemplate;

	public BaseDao() {
		jdbcTemplate = (JdbcTemplate) factory.getBean("jdbcTemplate");
	}
	
	static {
		Resource resource = new ClassPathResource("dao.xml");
		factory = new XmlBeanFactory(resource);
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}
}
