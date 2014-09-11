package com.zxsoft.crawler.web.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.thinkingcloud.framework.util.StringUtils;

import com.zxsoft.crawler.dao.BaseDao;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.web.dao.WebsiteDao;
import com.zxsoft.crawler.web.dao.impl.WebsiteDaoImpl;
import com.zxsoft.crawler.web.service.WebsiteService;
import com.zxsoft.framework.utils.Page;

public class WebsiteServiceImpl extends BaseDao implements WebsiteService {

	@Override
	public Page<ListConf> getListConfs(final int pageNo, final int pageSize, ListConf params) {
		String resultSql = "SELECT * FROM " + TABLE_CONF_LIST + " WHERE 1=1 ";
		String countSql = "SELECT count(*) FROM " + TABLE_CONF_LIST + " WHERE 1=1 ";
		StringBuilder paramSb = new StringBuilder();
		if (!StringUtils.isEmpty(params.getComment())) {
			paramSb.append(" and comment like '%" + params.getComment() + "%'");
		}

		int count = jdbcTemplate.queryForInt(countSql + paramSb.toString());

		List<ListConf> res = jdbcTemplate.query(resultSql + paramSb.toString() + " limit "
		        + (pageNo - 1) + "," + pageSize, new RowMapper<ListConf>() {
			public ListConf mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new ListConf(rs.getString("comment"), rs.getString("url"), rs
				        .getString("category"), rs.getBoolean("auth"), rs.getBoolean("ajax"), rs
				        .getInt("fetchinterval"),
				/* rs.getInt("pageNum"), */rs.getString("filterurl"), rs.getString("listdom"), rs
				        .getString("linedom"), rs.getString("urldom"), rs.getString("datedom"), rs
				        .getString("updatedom"), rs.getInt("numThreads"), rs
				        .getString("synopsisdom"));
			}
		});

		Page<ListConf> page = new Page<ListConf>(count, res);
		return page;
	}

	@Override
	public Page<DetailConf> getDetailConfs(int pageNo, int pageSize, DetailConf params) {
		String resultSql = "SELECT * FROM " + TABLE_CONF_DETAIL + " WHERE 1=1 ";
		String countSql = "SELECT count(*) FROM " + TABLE_CONF_DETAIL + " WHERE 1=1 ";
		StringBuilder paramSb = new StringBuilder();
		if (!StringUtils.isEmpty(params.getHost())) {
			paramSb.append(" and host like '%" + params.getHost() + "%'");
		}

		int count = jdbcTemplate.queryForInt(countSql + paramSb.toString());

		List<DetailConf> res = jdbcTemplate.query(resultSql + paramSb.toString() + " limit "
		        + (pageNo - 1) + "," + pageSize, new RowMapper<DetailConf>() {
			public DetailConf mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new DetailConf(rs.getString("listurl"), rs.getString("host"), rs.getString("replyNum"), rs
				        .getString("reviewNum"), rs.getString("forwardNum"), rs
				        .getString("sources"), rs.getBoolean("fetchOrder"), rs.getString("master"),
				        rs.getString("author"), rs.getString("date"), rs.getString("content"), rs
				                .getString("reply"), rs.getString("replyAuthor"), rs
				                .getString("replyDate"), rs.getString("replyContent"), rs
				                .getString("subReply"), rs.getString("subReplyAuthor"), rs
				                .getString("subReplyDate"), rs.getString("subReplyContent"));
			}
		});

		Page<DetailConf> page = new Page<DetailConf>(count, res);
		return page;
	}

	@Override
	public void add(ListConf listConf) {
		String url = listConf.getUrl();
		if (url.endsWith("/")) {
			url = url.substring(0, url.lastIndexOf("/"));
		}
		listConf.setUrl(url);
		
		if (listConfExist(listConf.getUrl())) {
			jdbcTemplate.update("delete from " + TABLE_CONF_LIST + " where url = ?", new Object[] {url});
		}
		
		jdbcTemplate
		        .update("insert into "
		                + TABLE_CONF_LIST
		                + "(comment, url, category, auth, ajax, numThreads, fetchinterval, filterurl, listdom, linedom, urldom, datedom, updatedom, synopsisdom) "
		                + "values (?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?,?,?)",
		                new Object[] { listConf.getComment(), listConf.getUrl(),
		                        listConf.getCategory(), listConf.isAuth(), listConf.isAjax(),
		                        listConf.getNumThreads(), listConf.getFetchinterval(),
		                        listConf.getFilterurl(), listConf.getListdom(),
		                        listConf.getLinedom(), listConf.getUrldom(), listConf.getDatedom(),
		                        listConf.getUpdatedom(), listConf.getSynopsisdom() });

	}

	@Override
	public void add(DetailConf detailConf) {
		String host = detailConf.getHost();
		if (host.endsWith("/")) {
			host = host.substring(0, host.lastIndexOf("/"));
		}
		detailConf.setHost(host);
		
		String listUrl = detailConf.getListUrl();
		if (listUrl.endsWith("/")) {
			listUrl = listUrl.substring(0, listUrl.lastIndexOf("/"));
		}
		detailConf.setListUrl(listUrl);
		
		if (detailConfExist(detailConf.getListUrl(), detailConf.getHost())) {
			jdbcTemplate.update("delete from " + TABLE_CONF_DETAIL + " where listurl = ? and host = ?", new Object[] {listUrl, host});
		}
		
		jdbcTemplate
		        .update("insert into "
		                + TABLE_CONF_DETAIL
		                + "(listurl, host, replyNum, reviewNum, forwardNum, sources, fetchOrder, master, author, date, content, reply, replyAuthor, replyDate, replyContent, subReply, subReplyAuthor, subReplyDate, subReplyContent) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ",
		                new Object[] { detailConf.getListUrl(), detailConf.getHost(), detailConf.getReplyNum(),
		                        detailConf.getReviewNum(), detailConf.getForwardNum(),
		                        detailConf.getSources(), detailConf.isFetchorder(),
		                        detailConf.getMaster(), detailConf.getAuthor(),
		                        detailConf.getDate(), detailConf.getContent(),
		                        detailConf.getReply(), detailConf.getReplyAuthor(),
		                        detailConf.getReplyDate(), detailConf.getReplyContent(),
		                        detailConf.getSubReply(), detailConf.getSubReplyAuthor(),
		                        detailConf.getSubReplyDate(), detailConf.getSubReplyContent() });

	}

	WebsiteDao websiteDao = new WebsiteDaoImpl();
	
	@Override
    public boolean listConfExist(String url) {
		if (StringUtils.isEmpty(url)) {
			return false;
		}
		
		if (url.endsWith("/")) {
			url = url.substring(0, url.lastIndexOf("/"));
		}
		
		if (websiteDao.getListConf(url) != null)
			return true;
	    return false;
    }

	@Override
    public boolean detailConfExist(String listUrl, String host) {
		if (StringUtils.isEmpty(host)) {
			return false;
		}
		
		if (host.endsWith("/")) {
			host = host.substring(0, host.lastIndexOf("/"));
		}
		
	    if (websiteDao.getDetailConf(listUrl, host) != null)
	    	return true;
	    return false;
    }

}
