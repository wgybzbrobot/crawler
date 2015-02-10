package com.zxsoft.crawler.dao;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.zxisl.commons.cache.ObjectCache;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.NetUtils;
import com.zxsoft.crawler.storage.Account;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;

public class ConfDao extends BaseDao {

	private Logger LOG = LoggerFactory.getLogger(ConfDao.class);
	private static final int TIMEOUT = 60;

	/**
	 * 获取登录帐号
	 */
	@SuppressWarnings("unchecked")
//	public List<Account> getAccounts(String host) {
//		ObjectCache objectCache = ObjectCache.get("Account");
//		if (objectCache.getObject(host) != null) {
//			return (List<Account>) objectCache.getObject(host);
//		} else {
//			List<Account> list = getJdbcTemplate().query("select * from " + TABLE_AUTH
//			        + " where host = ?", new Object[] { host }, new RowMapper<Account>() {
//				@Override
//				public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
//					return new Account(rs.getString("host"), rs.getString("username"), rs
//					        .getString("password"));
//				}
//			});
//			objectCache.setObject(host, list);
//			return list;
//		}
//	}

	/**
	 * 获取列表配置信息
	 * @param url
	 */
	public ListConf getListConf(String url) {
		ObjectCache objectCache = ObjectCache.get("ListConf", TIMEOUT);

		if (objectCache.getObject(url) != null) {
		        LOG.debug("Find ListConf in Cache");
			return (ListConf) objectCache.getObject(url);
		} else {
		        // if not foudn in cache, get ListConf from database
		        LOG.debug("Do not find ListConf in Cache, will get ListConf from database.");
			LOG.debug("Getting list config:" + url);
			List<ListConf> list = getJdbcTemplate().query("select * from conf_list where url = ?",
			        new Object[] { url }, new RowMapper<ListConf>() {
				        public ListConf mapRow(ResultSet rs, int rowNum) throws SQLException {
					        return new ListConf(rs.getString("comment"), rs.getString("url"), rs
					                .getString("category"), rs.getBoolean("auth"), rs
					                .getBoolean("ajax"), rs.getInt("fetchinterval"), rs
					                .getString("filterurl"), rs.getString("listdom"), rs
					                .getString("linedom"), rs.getString("authordom"), rs.getString("urldom"), rs
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

//	public ListConf getListConfByCategory(final String category) {
//
//		ObjectCache objectCache = ObjectCache.get("ListConf", TIMEOUT);
//
//		if (objectCache.getObject(category) != null) {
//			return (ListConf) objectCache.getObject(category);
//		} else {
//			LOG.debug("Getting list config by category:" + category);
//			List<ListConf> list = getJdbcTemplate().query("select * from conf_list where category = ?",
//			        new Object[] { category }, new RowMapper<ListConf>() {
//				        public ListConf mapRow(ResultSet rs, int rowNum) throws SQLException {
//					        return new ListConf(rs.getString("comment"), rs.getString("url"), rs
//					                .getString("category"), rs.getBoolean("auth"), rs
//					                .getBoolean("ajax"), rs.getInt("fetchinterval"), rs
//					                .getString("filterurl"), rs.getString("listdom"), rs
//					                .getString("linedom"), rs.getString("urldom"), rs
//					                .getString("datedom"), rs.getString("updatedom"), rs
//					                .getInt("numThreads"), rs.getString("synopsisdom"));
//				        }
//			        });
//			ListConf listConf = null;
//			if (CollectionUtils.isEmpty(list)) {
//				LOG.error("");
//				return null;
//			}
//
//			listConf = list.get(0);
//			objectCache.setObject(category, listConf);
//
//			return listConf;
//		}
//	}

	/**
	 * 获取全网搜索的所有搜索引擎query url.
	 */
//	public List<ListConf> getListConfsOfNetworkSearch() {
//
//		LOG.debug("Getting list config of NetworkSearch.");
//		List<ListConf> list = getJdbcTemplate().query(
//		        "select * from conf_list where category = 'search'", new RowMapper<ListConf>() {
//			        public ListConf mapRow(ResultSet rs, int rowNum) throws SQLException {
//				        return new ListConf(rs.getString("comment"), rs.getString("url"), rs
//				                .getString("category"), rs.getBoolean("auth"), rs
//				                .getBoolean("ajax"), rs.getInt("fetchinterval"), rs
//				                .getString("filterurl"), rs.getString("listdom"), rs
//				                .getString("linedom"), rs.getString("urldom"), rs
//				                .getString("datedom"), rs.getString("updatedom"), rs
//				                .getInt("numThreads"), rs.getString("synopsis"));
//			        }
//		        });
//
//		return list;
//	}

	
	/**
	 * @param listUrl 列表页的url
	 * @param host 详细页的Host
	 */
	public DetailConf getDetailConf(String listUrl, String host) {
		ObjectCache objectCache = ObjectCache.get("DetailConf", TIMEOUT);

		if (objectCache.getObject(host) != null) {
			return (DetailConf) objectCache.getObject(listUrl + host);
		} else {
			LOG.debug("Getting detail configuration:" + host);
			List<DetailConf> list = getJdbcTemplate().query("select * from " + TABLE_CONF_DETAIL + " where listurl = ? and host like ?",
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
					        detailConf.setAjax(rs.getBoolean("ajax"));
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
			} else {
			        LOG.debug("Do not find DetailConf database.");
			        return null;
			}

			objectCache.setObject(listUrl + host, detailConf);
			return detailConf;
		}
	}
	
//	public DetailConf getDetailConf(String host) {
//		ObjectCache objectCache = ObjectCache.get("DetailConf", TIMEOUT);
//
//		if (objectCache.getObject(host) != null) {
//			return (DetailConf) objectCache.getObject(host);
//		} else {
//			LOG.debug("Getting detail configuration:" + host);
//			List<DetailConf> list = getJdbcTemplate().query("select * from conf_detail where host like ?",
//			        new Object[] { "%" + host + "%"}, new RowMapper<DetailConf>() {
//				        public DetailConf mapRow(ResultSet rs, int rowNum) throws SQLException {
//					        DetailConf detailConf = new DetailConf();
//					        detailConf.setListUrl(rs.getString("listurl"));
//					        detailConf.setHost(rs.getString("host"));
//					        detailConf.setReplyNum(rs.getString("replyNum"));
//					        detailConf.setForwardNum(rs.getString("forwardNum"));
//					        detailConf.setReviewNum(rs.getString("reviewNum"));
//					        detailConf.setSources(rs.getString("sources"));
//					        detailConf.setFetchorder(rs.getBoolean("fetchorder"));
//					        detailConf.setAjax(rs.getBoolean("ajax"));
//					        detailConf.setMaster(rs.getString("master"));
//					        detailConf.setAuthor(rs.getString("author"));
//					        detailConf.setDate(rs.getString("date"));
//					        detailConf.setContent(rs.getString("content"));
//					        detailConf.setReply(rs.getString("reply"));
//					        detailConf.setReplyAuthor(rs.getString("replyAuthor"));
//					        detailConf.setReplyDate(rs.getString("replyDate"));
//					        detailConf.setReplyContent(rs.getString("replyContent"));
//					        detailConf.setSubReply(rs.getString("subReply"));
//					        detailConf.setSubReplyAuthor(rs.getString("subReplyAuthor"));
//					        detailConf.setSubReplyDate(rs.getString("subReplyDate"));
//					        detailConf.setSubReplyContent(rs.getString("subReplyContent"));
//					        return detailConf;
//				        }
//			        });
//			DetailConf detailConf = null;
//			if (!CollectionUtils.isEmpty(list)) {
//				detailConf = list.get(0);
//			}
//
//			objectCache.setObject(host, detailConf);
//			return detailConf;
//		}
//	}

	
//	public String getWebsite(URL url) {
//		String host = NetUtils.getHost(url);
//		
//		// 先查询ConfDetail
//		DetailConf detailConf = getDetailConf(host);
//		String urlstr = url.toExternalForm();
//		if (detailConf != null) {
//			urlstr = detailConf.getListUrl();
//		}
//		// 再查询ConfList
//		ListConf listConf = getListConf(urlstr);
//		
//		if (listConf == null) {
//			LOG.warn("没有找到ListConf:" + url.toExternalForm());
//		}
//		
//		return "";
//	}

}
