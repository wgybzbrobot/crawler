package com.zxsoft.crawler.master.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
         * @param ly ==> tid
         * 
         * @return
         */
        public String getSearchEngineUrl(int ly) {
                if (StringUtils.isEmpty(ly)) {
                        return null;
                }
                ObjectCache objectCache = ObjectCache.get("ListConf", TIMEOUT);
                String url = "";
                if (objectCache.getObject(String.valueOf(ly)) != null) {
                        LOG.debug("Find search engine url in Cache");
                        url =  (String) objectCache.getObject(String.valueOf(ly));
                } else {
                        // if not found in cache, get ListConf from database
                        LOG.debug("Do not find ListConf in Cache, will get search engine url from database.");
                        LOG.debug("Getting search engine url:" + ly);
                        List<String> list = mysqlJdbcTemplate.query("select a.* from conf_list a, website b, section c where b.id = c.site and c.url = a.url and b.tid = ?",
                                new Object[] { ly }, new RowMapper<String>() {
                                        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                                                return  rs.getString("url");
                                        }
                                });
                        if (CollectionUtils.isEmpty(list) || StringUtils.isEmpty(list.get(0))) {
//                                LOG.error("在表<website>没有找到来源为<" + ly + ">的记录.");
                                return null;
                        } else {
                                url = list.get(0);
                                LOG.info("找到来源 " + ly + "的网站" + url);
                                objectCache.setObject(String.valueOf(ly), url);
                        }
                }
                return url;
        }

        public Map<String, Object> getBasicInfos(String engineUrl) {
                List<Map<String, Object>> list = mysqlJdbcTemplate.query("select a.comment,  a.region, a.tid, a.provinceId, a.cityId, a.areaId, b.id from website a, section b"
                                                + "  where b.url = ?  and a.id=b.site ",
                                                new Object[] { engineUrl }, new RowMapper<Map<String, Object>>() {
                                                        public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                                                                Map<String, Object> map = new HashMap<String,Object>();
                                                                map.put("source_id", rs.getInt("tid"));
                                                                map.put("provinceId", rs.getInt("provinceId"));
                                                                map.put("cityId", rs.getInt("cityId"));
                                                                map.put("areaId", rs.getInt("areaId"));
                                                                map.put("sectionId", rs.getInt("id"));
                                                                map.put("region", rs.getInt("region"));
                                                                map.put("comment", rs.getString("comment"));
                                                                return  map;
                                                        }
                                                });
                return CollectionUtils.isEmpty(list) ? null : list.get(0);
        }
}
