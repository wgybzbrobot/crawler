package com.zxsoft.crawler.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.mysql.jdbc.Driver;

@SuppressWarnings("deprecation")
public abstract class BaseDao {

	protected static final String TABLE_CONF_LIST = "conf_list";
	protected static final String TABLE_CONF_DETAIL = "conf_detail";
	protected static final String TABLE_SEARCH_ENGINE = "search_engine";
	protected static final String TABLE_AUTH = "auth";
	protected static final String TABLE_PROXY = "proxy";
	protected static final String TABLE_WEBSITE = "website";
	
	private static final JdbcTemplate jdbcTemplate;

	static {
		Driver driver = null;
        try {
	        driver = new Driver();
        } catch (SQLException e) {
	        e.printStackTrace();
        }
        Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();           
		InputStream stream = loader.getResourceAsStream("db.properties");
		try {
	        prop.load(stream);
        } catch (IOException e1) {
	        e1.printStackTrace();
        }
		String url = prop.getProperty("db.url");
		String username = prop.getProperty("db.username");
		String password = prop.getProperty("db.password");
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource(driver, url, username, password);
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}
