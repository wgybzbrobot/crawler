package com.zxsoft.crawler.web.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.thinkingcloud.framework.util.CollectionUtils;

import com.zxsoft.crawler.dao.BaseDao;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.web.dao.WebsiteDao;

public class WebsiteDaoImpl extends BaseDao implements WebsiteDao {

	@Override
	public ListConf getListConf(String url) {
		String sql = "SELECT * FROM " + TABLE_CONF_LIST + " WHERE url = ? ";
		List<ListConf> list = jdbcTemplate.query(sql, new Object[] { url },
		        new RowMapper<ListConf>() {
			        public ListConf mapRow(ResultSet rs, int rowNum) throws SQLException {
				        return new ListConf(rs.getString("comment"), rs.getString("url"), rs
				                .getString("category"), rs.getBoolean("auth"), rs
				                .getBoolean("ajax"), rs.getInt("fetchinterval"), rs
				                .getString("filterurl"), rs.getString("listdom"), rs
				                .getString("linedom"), rs.getString("urldom"), rs
				                .getString("datedom"), rs.getString("updatedom"), rs
				                .getInt("numThreads"), rs.getString("synopsisdom"));
			        }
		        });

		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	@Override
	public DetailConf getDetailConf(String listUrl, String host) {
		String sql = "SELECT * FROM " + TABLE_CONF_DETAIL + " WHERE listurl = ? AND host = ? ";
		List<DetailConf> list = jdbcTemplate.query(sql, new Object[] { listUrl, host },
		        new RowMapper<DetailConf>() {
			        public DetailConf mapRow(ResultSet rs, int rowNum) throws SQLException {
				        return new DetailConf(rs.getString("listurl"), rs.getString("host"), rs.getString("replyNum"), rs
				                .getString("reviewNum"), rs.getString("forwardNum"), rs
				                .getString("sources"), rs.getBoolean("fetchorder"), rs
				                .getString("master"), rs.getString("author"), rs.getString("date"),
				                rs.getString("content"), rs.getString("reply"), rs
				                        .getString("replyAuthor"), rs.getString("replyDate"), rs
				                        .getString("replyContent"), rs.getString("subReply"), rs
				                        .getString("subReplyAuthor"), rs.getString("subReplyDate"),
				                rs.getString("subReplyContent"));
			        }
		        });

		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

}
