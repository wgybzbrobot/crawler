package com.zxsoft.crawler.web.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.web.service.WebsiteService;
import com.zxsoft.framework.utils.Page;

@Service
public class WebsiteServiceImpl implements WebsiteService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Page<ListConf> getListConfs(final int pageNo, final int pageSize,
			ListConf params) {
		String resultSql = "SELECT * FROM conf_list WHERE 1=1 ";
		String countSql = "SELECT count(*) FROM conf_list WHERE 1=1 ";
		StringBuilder paramSb = new StringBuilder();
		if (!StringUtils.isEmpty(params.getComment())) {
			paramSb.append(" and comment like '%" + params.getComment() + "%'");
		}
		
		int count = jdbcTemplate.queryForInt(countSql + paramSb.toString());
		
		List<ListConf> res = jdbcTemplate.query(resultSql + paramSb.toString() + " limit " + (pageNo -1) + "," + pageSize, new RowMapper<ListConf>() {
			public ListConf mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new ListConf(rs.getString("comment"), rs.getString("url"), rs
				        .getString("category"), rs.getBoolean("auth"), rs.getBoolean("ajax"), rs.getInt("fetchinterval"),
				        /*rs.getInt("pageNum"),*/ rs.getString("filterurl"), rs.getString("listdom"),
				        rs.getString("linedom"), rs.getString("urldom"), rs.getString("datedom"),
				        rs.getString("updatedom"), rs.getInt("numThreads"), rs.getString("synopsis"));
			}
		});
		
		Page<ListConf> page = new Page<ListConf>(count, res);
		return page;
	}

	@Override
	public void add(ListConf listConf, DetailConf detailConf) {
		// TODO Auto-generated method stub

	}

}
