package com.zxsoft.proxy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.thinkingcloud.framework.cache.ObjectCache;
import org.thinkingcloud.framework.util.CollectionUtils;

import com.zxsoft.crawler.dao.BaseDao;

/**
 * Read Proxy from database.
 */
public class DbProxyFactory extends BaseDao implements ProxyFactory {

	private static Logger LOG = LoggerFactory.getLogger(DbProxyFactory.class);
	
	@SuppressWarnings("unchecked")
    public List<Proxy> getProxies(String type) {

		ObjectCache objectCache = ObjectCache.get("proxies");
		List<Proxy> proxies = (List<Proxy>) objectCache.getObject(type);

		if (!CollectionUtils.isEmpty(proxies)) {
			return proxies;
		} else {
			proxies = jdbcTemplate.query("select * from " + TABLE_PROXY + " where type = ?",
			        new Object[] { type },
			        new RowMapper<Proxy>() {
				        public Proxy mapRow(ResultSet rs, int rowNum) throws SQLException {
					        return new Proxy(rs.getString("ip"), rs
					                .getInt("port"), rs.getString("type"));
				        }
			        });
			if (!CollectionUtils.isEmpty(proxies)) {
				objectCache.setObject(type, proxies);
			} else {
				LOG.error("没有类型为<" + type + ">的代理");
			}
			
			return proxies;
		}
	}

}
