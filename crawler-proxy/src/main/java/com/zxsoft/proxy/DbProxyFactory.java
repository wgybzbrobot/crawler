package com.zxsoft.proxy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/**
 * Read Proxy from database.
 */
@Component
public class DbProxyFactory extends ProxyFactory {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Proxy> getProxies(String type) {
		
		List<Proxy> proxies = jdbcTemplate.query("", new RowMapper<Proxy>() {

			public Proxy mapRow(ResultSet rs, int rowNum) throws SQLException {
	            // TODO Auto-generated method stub
	            return null;
            }
			
		});
		
		return proxies;
	}

}
