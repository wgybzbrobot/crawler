package com.zxsoft.crawler.master.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.mysql.jdbc.Driver;
import com.zxisl.commons.cache.ObjectCache;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;

public class MysqlDao extends BaseDao {
        private static Logger LOG = LoggerFactory.getLogger(MysqlDao.class);
        protected static final String TABLE_CONF_LIST = "conf_list";
        protected static final int TIMEOUT = 360;
        private static final JdbcTemplate mysqlJdbcTemplate;

        static {
                Driver driver = null;
                try {
                        driver = new Driver();
                } catch (SQLException e) {
                        e.printStackTrace();
                }
                Properties prop = new Properties();
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                InputStream stream = loader.getResourceAsStream("mysql.properties");
                try {
                        prop.load(stream);
                } catch (IOException e1) {
                       LOG.error(e1.getMessage(), e1);
                }
                String url = prop.getProperty("db.url");
                String username = prop.getProperty("db.username");
                String password = prop.getProperty("db.password");
                LOG.info("mysql database address: " + url);
                SimpleDriverDataSource dataSource = new SimpleDriverDataSource(driver, url, username, password);
                mysqlJdbcTemplate = new JdbcTemplate(dataSource);
        }

        public JdbcTemplate getMysqlJdbcTemplate() {
                return mysqlJdbcTemplate;
        }
        
        /**
         * 通过注释取得搜索引擎url
         * @param comment
         * @return
         */
        public String getSearchEngineUrl(String comment) {
                if (StringUtils.isEmpty(comment)) {
                        return null;
                }
                ObjectCache objectCache = ObjectCache.get("ListConf", TIMEOUT);
                String url = "";
                if (objectCache.getObject(comment) != null) {
                        LOG.debug("Find search engine url in Cache");
                        url =  (String) objectCache.getObject(comment);
                } else {
                        // if not found in cache, get ListConf from database
                        LOG.debug("Do not find ListConf in Cache, will get search engine url from database.");
                        LOG.debug("Getting search engine url:" + comment);
                        List<String> list = mysqlJdbcTemplate.query("select * from conf_list where comment = ?",
                                new Object[] { comment }, new RowMapper<String>() {
                                        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                                                return  rs.getString("url");
                                        }
                                });
                        if (CollectionUtils.isEmpty(list) || StringUtils.isEmpty(list.get(0))) {
                                LOG.error("在表<" + TABLE_CONF_LIST + ">没有找到comment为<" + comment + ">的记录.");
                                return null;
                        } else {
                                url = list.get(0);
                                objectCache.setObject(comment, url);
                        }
                }
                return url;
        }

}
