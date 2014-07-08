package com.zxsoft.crawler.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.zxsoft.crawler.storage.ForumDetailConf;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.storage.NewsDetailConf;

@Component("confDao")
public class ConfDao {

	private Logger LOG = LoggerFactory.getLogger(ConfDao.class);
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public ConfDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * 获取列表配置信息
	 * 
	 * @param url
	 * @return
	 */
	@Cacheable(value = "listConfCache", key = "#url")
	public ListConf getListConf(String url) {
		ListConf listConf = jdbcTemplate.queryForObject("select * from conf_list where url = ?",
		        new Object[] { url }, ListConf.class);
		return listConf;
	}

	/**
	 * 获取论坛详细页配置信息
	 */
	@Cacheable(value = "forumDetailConf", key = "#host")
	public ForumDetailConf getForumDetailConf(String host) {
		ForumDetailConf detailConf = jdbcTemplate.queryForObject(
		        "select * from forumconf_detail where host = ?", new Object[] { host },
		        ForumDetailConf.class);
		return detailConf;
	}

	/**
	 * 获取新闻资讯详细页配置信息
	 */
	public NewsDetailConf getNewsDetailConf(String host) {
		NewsDetailConf detailConf = jdbcTemplate.queryForObject(
		        "select * from newsconf_detail where host = ?", new Object[] { host },
		        NewsDetailConf.class);
		return detailConf;
	}

}
