package com.zxsoft.proxy;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;
import org.thinkingcloud.framework.cache.ObjectCache;

import com.zxsoft.crawler.dao.BaseDao;
import com.zxsoft.crawler.util.Utils;

public class ProxyRandom extends BaseDao {

	private static Logger LOG = LoggerFactory.getLogger(ProxyRandom.class);

	private ProxyFactory proxyFactory;

	// default file-configured proxy
	public ProxyRandom() {
		proxyFactory = new LocalProxyFactory();
	}

	public ProxyRandom(ProxyFactory proxyFactory) {
		this.proxyFactory = proxyFactory;
	}

	public Proxy random(String url) {
		
		String host = "";
		try {
			host = Utils.getHost(url);
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		String type = getWebSiteType(host);

		List<Proxy> proxies = proxyFactory.getProxies(type);

		if (CollectionUtils.isEmpty(proxies))
			return null;

		int i = (int) (Math.random() * proxies.size());
		return proxies.get(i);
	}

	private String getWebSiteType(String host) {
		ObjectCache objectCache = ObjectCache.get("WebSiteType");

		if (objectCache.getObject(host) != null) {
			return (String) objectCache.getObject(host);
		} else {
			Log.debug("Getting Website Type of Host:" + host);
			List<String> list = jdbcTemplate.query("select type from " + TABLE_WEBSITE
			        + " where host = ? ", new Object[] {host}, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString("type");
				}
			});

			if (!org.thinkingcloud.framework.util.CollectionUtils.isEmpty(list)) {
				objectCache.setObject(host, list.get(0));
				return list.get(0);
			} else {
				LOG.error("在表<" + TABLE_WEBSITE + ">没有找到host为" + host + "的type值.");
				return "";
			}
		}
	}

}
