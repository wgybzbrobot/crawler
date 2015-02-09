package com.zxsoft.crawler.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import oracle.jdbc.driver.OracleDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

public class LocationDao {

        private static Logger LOG = LoggerFactory.getLogger(LocationDao.class);
        
        private static final JdbcTemplate oracleJdbcTemplate;

        static {
                OracleDriver driver = null;
                driver = new OracleDriver();
                Properties prop = new Properties();
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                try {
                        InputStream stream = loader.getResourceAsStream("location.properties");
                        prop.load(stream);
                } catch (Exception e) {
                        LOG.error("Load location.properties failed.", e);
                }
                String url = prop.getProperty("db.url");
                String username = prop.getProperty("db.username");
                String password = prop.getProperty("db.password");
                LOG.info("oracle database address for location: " + url);
                SimpleDriverDataSource dataSource = new SimpleDriverDataSource(driver, url, username, password);
                oracleJdbcTemplate = new JdbcTemplate(dataSource);
        }
        
        public static JdbcTemplate getJdbcTemplate() {
                return oracleJdbcTemplate;
        }
}
