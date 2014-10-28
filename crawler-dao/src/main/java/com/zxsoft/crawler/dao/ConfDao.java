package com.zxsoft.crawler.dao;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.thinkingcloud.framework.cache.ObjectCache;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.NetUtils;

import com.zxsoft.crawler.storage.Account;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.storage.Section;

public class ConfDao extends BaseDao {

	private Logger LOG = LoggerFactory.getLogger(ConfDao.class);

	public ConfDao() {
	}

	/**
	 * 获取登录帐号
	 */
	@SuppressWarnings("unchecked")
	public List<Account> getAccounts(String host) {
		ObjectCache objectCache = ObjectCache.get("Account");
		if (objectCache.getObject(host) != null) {
			return (List<Account>) objectCache.getObject(host);
		} else {
			List<Account> list = jdbcTemplate.query("select * from " + TABLE_AUTH
			        + " where host = ?", new Object[] { host }, new RowMapper<Account>() {
				@Override
				public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
					return new Account(rs.getString("host"), rs.getString("username"), rs
					        .getString("password"));
				}
			});
			objectCache.setObject(host, list);
			return list;
		}
	}

	/**
	 * @return 搜索引擎url
	 */
/*	public String getSearchEngine(String id) {
		ObjectCache objectCache = ObjectCache.get("SearchEngine");
		if (objectCache.getObject(id) != null) {
			return (String) objectCache.getObject(id);
		} else {
			List<String> list = jdbcTemplate.query("select url from " + TABLE_SEARCH_ENGINE
			        + " where id = ?", new Object[] { id }, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString("url");
				}
			});

			if (CollectionUtils.isEmpty(list)) {
				LOG.error("在表<" + TABLE_SEARCH_ENGINE + ">没有找到id为" + id + "的搜索引擎url值.");
				return "";
			}
			
			objectCache.setObject(id, list.get(0));
			return list.get(0);
		}
	}*/

	/**
	 * 获取列表配置信息
	 * 
	 * @param url
	 * @return
	 */
	public ListConf getListConf(String url) {
		ObjectCache objectCache = ObjectCache.get("ListConf");

		if (objectCache.getObject(url) != null) {
			return (ListConf) objectCache.getObject(url);
			// if not foudn in cache, get ListConf from database
		} else {
			LOG.info("Getting list config:" + url);
			List<ListConf> list = jdbcTemplate.query("select * from conf_list where url = ?",
			        new Object[] { url }, new RowMapper<ListConf>() {
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
			ListConf listConf = null;
			if (CollectionUtils.isEmpty(list)) {
				LOG.error("在表<" + TABLE_CONF_LIST + ">没有找到url为" + url + "的记录.");
				return null;
			} else {
				listConf = list.get(0);
				objectCache.setObject(url, listConf);
			}
			
			return listConf;
		}
	}

	public ListConf getListConfByCategory(final String category) {

		ObjectCache objectCache = ObjectCache.get("ListConf");

		if (objectCache.getObject(category) != null) {
			return (ListConf) objectCache.getObject(category);
		} else {
			LOG.debug("Getting list config by category:" + category);
			List<ListConf> list = jdbcTemplate.query("select * from conf_list where category = ?",
			        new Object[] { category }, new RowMapper<ListConf>() {
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
			ListConf listConf = null;
			if (CollectionUtils.isEmpty(list)) {
				LOG.error("");
				return null;
			}

			listConf = list.get(0);
			objectCache.setObject(category, listConf);

			return listConf;
		}
	}

	/**
	 * 获取全网搜索的所有搜索引擎query url.
	 */
	public List<ListConf> getListConfsOfNetworkSearch() {

		LOG.debug("Getting list config of NetworkSearch.");
		List<ListConf> list = jdbcTemplate.query(
		        "select * from conf_list where category = 'search'", new RowMapper<ListConf>() {
			        public ListConf mapRow(ResultSet rs, int rowNum) throws SQLException {
				        return new ListConf(rs.getString("comment"), rs.getString("url"), rs
				                .getString("category"), rs.getBoolean("auth"), rs
				                .getBoolean("ajax"), rs.getInt("fetchinterval"), rs
				                .getString("filterurl"), rs.getString("listdom"), rs
				                .getString("linedom"), rs.getString("urldom"), rs
				                .getString("datedom"), rs.getString("updatedom"), rs
				                .getInt("numThreads"), rs.getString("synopsis"));
			        }
		        });

		return list;
	}

	
	/**
	 * @param listUrl 列表页的url
	 * @param host 详细页的Host
	 */
	public DetailConf getDetailConf(String listUrl, String host) {
		ObjectCache objectCache = ObjectCache.get("DetailConf");

		if (objectCache.getObject(host) != null) {
			return (DetailConf) objectCache.getObject(host);
		} else {
			LOG.debug("Getting detail configuration:" + host);
			List<DetailConf> list = jdbcTemplate.query("select * from conf_detail where listurl = ? and host like ?",
			        new Object[] { listUrl, "%" + host + "%"}, new RowMapper<DetailConf>() {
				        public DetailConf mapRow(ResultSet rs, int rowNum) throws SQLException {
					        DetailConf detailConf = new DetailConf();
					        detailConf.setListUrl(rs.getString("listurl"));
					        detailConf.setHost(rs.getString("host"));
					        detailConf.setReplyNum(rs.getString("replyNum"));
					        detailConf.setForwardNum(rs.getString("forwardNum"));
					        detailConf.setReviewNum(rs.getString("reviewNum"));
					        detailConf.setSources(rs.getString("sources"));
					        detailConf.setFetchorder(rs.getBoolean("fetchorder"));
					        detailConf.setMaster(rs.getString("master"));
					        detailConf.setAuthor(rs.getString("author"));
					        detailConf.setDate(rs.getString("date"));
					        detailConf.setContent(rs.getString("content"));
					        detailConf.setReply(rs.getString("reply"));
					        detailConf.setReplyAuthor(rs.getString("replyAuthor"));
					        detailConf.setReplyDate(rs.getString("replyDate"));
					        detailConf.setReplyContent(rs.getString("replyContent"));
					        detailConf.setSubReply(rs.getString("subReply"));
					        detailConf.setSubReplyAuthor(rs.getString("subReplyAuthor"));
					        detailConf.setSubReplyDate(rs.getString("subReplyDate"));
					        detailConf.setSubReplyContent(rs.getString("subReplyContent"));
					        return detailConf;
				        }
			        });
			DetailConf detailConf = null;
			if (!CollectionUtils.isEmpty(list)) {
				detailConf = list.get(0);
			}

			objectCache.setObject(host, detailConf);
			return detailConf;
		}
	}
	
	public DetailConf getDetailConf(String host) {
		ObjectCache objectCache = ObjectCache.get("DetailConf");

		if (objectCache.getObject(host) != null) {
			return (DetailConf) objectCache.getObject(host);
		} else {
			LOG.debug("Getting detail configuration:" + host);
			List<DetailConf> list = jdbcTemplate.query("select * from conf_detail where host like ?",
			        new Object[] { "%" + host + "%"}, new RowMapper<DetailConf>() {
				        public DetailConf mapRow(ResultSet rs, int rowNum) throws SQLException {
					        DetailConf detailConf = new DetailConf();
					        detailConf.setListUrl(rs.getString("listurl"));
					        detailConf.setHost(rs.getString("host"));
					        detailConf.setReplyNum(rs.getString("replyNum"));
					        detailConf.setForwardNum(rs.getString("forwardNum"));
					        detailConf.setReviewNum(rs.getString("reviewNum"));
					        detailConf.setSources(rs.getString("sources"));
					        detailConf.setFetchorder(rs.getBoolean("fetchorder"));
					        detailConf.setMaster(rs.getString("master"));
					        detailConf.setAuthor(rs.getString("author"));
					        detailConf.setDate(rs.getString("date"));
					        detailConf.setContent(rs.getString("content"));
					        detailConf.setReply(rs.getString("reply"));
					        detailConf.setReplyAuthor(rs.getString("replyAuthor"));
					        detailConf.setReplyDate(rs.getString("replyDate"));
					        detailConf.setReplyContent(rs.getString("replyContent"));
					        detailConf.setSubReply(rs.getString("subReply"));
					        detailConf.setSubReplyAuthor(rs.getString("subReplyAuthor"));
					        detailConf.setSubReplyDate(rs.getString("subReplyDate"));
					        detailConf.setSubReplyContent(rs.getString("subReplyContent"));
					        return detailConf;
				        }
			        });
			DetailConf detailConf = null;
			if (!CollectionUtils.isEmpty(list)) {
				detailConf = list.get(0);
			}

			objectCache.setObject(host, detailConf);
			return detailConf;
		}
	}

	
	public String getWebsite(URL url) {
		String host = NetUtils.getHost(url);
		
		// 先查询ConfDetail
		DetailConf detailConf = getDetailConf(host);
		String urlstr = url.toExternalForm();
		if (detailConf != null) {
			urlstr = detailConf.getListUrl();
		}
		// 再查询ConfList
		ListConf listConf = getListConf(urlstr);
		
		if (listConf == null) {
			LOG.warn("没有找到ListConf:" + url.toExternalForm());
		}
		
		return "";
	}

//	public Section getSection(String indexUrl) {
//		
//	    return null;
//    }
}
