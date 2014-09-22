package com.zxsoft.crawler.web.dao.website.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.StringUtils;
import org.thinkingcloud.framework.web.utils.HibernateCallbackUtil;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.dao.BaseDao;
import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.web.dao.website.WebsiteDao;

@Service
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

	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	@Transactional
	@Override
    public Page<Website> getWebsites(Website website, int pageNo, int pageSize) {
		
		if (pageNo <= 0) pageNo = 1;
		
		Map<String, String> params = new HashMap<String, String>();
		
		StringBuffer sb = new StringBuffer(" from Website a where 1=1 ");
		if (website != null) {
			if (!StringUtils.isEmpty(website.getSite())) {
				sb.append(" and a.site like :site ");
				params.put("site", "%" + website.getSite().trim() + "%");
			}
			if (!StringUtils.isEmpty(website.getSiteType())) {
				sb.append(" and a.type =:type");
				params.put("type", website.getSiteType().getType());
			}
		}
		
		HibernateCallback<Page<Website>> action = HibernateCallbackUtil.getCallbackWithPage(sb, params, null, pageNo, pageSize);
		
		Page<Website> page =  hibernateTemplate.execute(action);
		
	    return page;
    }

	@Override
    public void addWebsite(Website website) {
	   hibernateTemplate.saveOrUpdate(website);
    }

	@Override
    public void addWebsites(List<Website> websites) {
		hibernateTemplate.saveOrUpdateAll(websites);
    }

	@Override
    public Website getWebsite(String site) {
		return hibernateTemplate.get(Website.class, site);
    }

}
