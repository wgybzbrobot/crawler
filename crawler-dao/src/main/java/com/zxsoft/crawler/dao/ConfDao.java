package com.zxsoft.crawler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.zxsoft.crawler.storage.ForumDetailConf;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.storage.NewsDetailConf;

@Component("confDao")
public class ConfDao {

	private JdbcTemplate jdbcTemplate;
	private Logger LOG = LoggerFactory.getLogger(ConfDao.class);

	public ConfDao() {
	}

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
		LOG.info("Getting list config:" + url);
		List<ListConf> list = jdbcTemplate.query("select * from conf_list where url = ?",
		        new Object[] { url }, new RowMapper<ListConf>() {
			        public ListConf mapRow(ResultSet rs, int rowNum) throws SQLException {
				        return new ListConf(rs.getString("comment"), rs.getString("url"), rs
				                .getString("category"), rs.getInt("fetchinterval"), rs
				                .getInt("pageNum"), rs.getString("filterurl"), rs
				                .getString("listdom"), rs.getString("linedom"), rs
				                .getString("urldom"), rs.getString("datedom"), rs
				                .getString("updatedom"), rs.getInt("numThreads"));
			        }
		        });
		ListConf listConf = null;
		if (!CollectionUtils.isEmpty(list)) {
			listConf = list.get(0);
		}
		return listConf;
	}

	/**
	 * 获取论坛详细页配置信息
	 */
	@Cacheable(value = "forumDetailConfCache", key = "#host")
	public ForumDetailConf getForumDetailConf(String host) {
		LOG.info("Getting forum detail config:" + host);
		List<ForumDetailConf> list = jdbcTemplate.query(
		        "select * from forumconf_detail where host = ?", new Object[] { host },
		        new RowMapper<ForumDetailConf>() {
			        public ForumDetailConf mapRow(ResultSet rs, int rowNum) throws SQLException {
				        return new ForumDetailConf(rs.getString("host"), /*rs.getString("comment"),*/
				                rs.getString("replyNum"), rs.getString("forwardNum"), rs
				                        .getString("reviewNum"), rs.getBoolean("fetchorder"), rs
				                        .getString("master"), rs.getString("masterAuthor"), rs
				                        .getString("masterDate"), rs.getString("masterContent"), rs
				                        .getString("reply"), rs.getString("replyAuthor"), rs
				                        .getString("replyDate"), rs.getString("replyContent"), rs
				                        .getString("subReply"), rs.getString("subReplyAuthor"), rs
				                        .getString("subReplyDate"), rs.getString("subReplyContent"));
			        }
		        });
		ForumDetailConf detailConf = null;
		if (!CollectionUtils.isEmpty(list)) {
			detailConf = list.get(0);
		}
		return detailConf;
	}

	/**
	 * 获取新闻资讯详细页配置信息
	 */
	@Cacheable(value = "newsDetailConfCache", key = "#host")
	public NewsDetailConf getNewsDetailConf(String host) {
		LOG.info("Getting news detail config:" + host);
		List<NewsDetailConf> list = jdbcTemplate.query(
		        "select * from newsconf_detail where host = ?", new Object[] { host },
		        new RowMapper<NewsDetailConf>() {
			        public NewsDetailConf mapRow(ResultSet rs, int rowNum) throws SQLException {
				        return new NewsDetailConf(rs.getString("host"), /*rs.getString("title"),*/ rs
				                .getString("content"), rs.getString("sources"), rs
				                .getString("author"), /*rs.getString("releaseDate"),*/ rs
				                .getString("replyNum"), rs.getString("forwardNum"), rs
				                .getString("reviewNum"));
			        }
		        });
		NewsDetailConf detailConf = null;
		if (!CollectionUtils.isEmpty(list)) {
			detailConf = list.get(0);
		}
		return detailConf;
	}

}
