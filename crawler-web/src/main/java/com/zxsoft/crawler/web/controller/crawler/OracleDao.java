package com.zxsoft.crawler.web.controller.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import oracle.jdbc.driver.OracleDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.zxisl.commons.utils.CollectionUtils;

public class OracleDao {
        private static Logger LOG = LoggerFactory.getLogger(OracleDao.class);

        private static final JdbcTemplate oracleJdbcTemplate;
        static {
                OracleDriver driver = null;
                driver = new OracleDriver();
                Properties prop = new Properties();
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                InputStream stream = loader.getResourceAsStream("oracle.properties");
                try {
                        prop.load(stream);
                } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                }
                String url = prop.getProperty("db.url");
                String username = prop.getProperty("db.username");
                String password = prop.getProperty("db.password");
                LOG.info("oracle database address: " + url);
                SimpleDriverDataSource dataSource = new SimpleDriverDataSource(driver, url, username, password);
                oracleJdbcTemplate = new JdbcTemplate(dataSource);
        }

        public JdbcTemplate getOracleJdbcTemplate() {
                return oracleJdbcTemplate;
        }

        /**
         * 查询sourceid, platform
         * 
         * @return
         */
        public Map<String, Object> querySourceId(int tid) {
            Map<String, Integer> map = new  HashMap<String, Integer>();
            
                List<Map<String, Object>> list = oracleJdbcTemplate.query("select sourceid ,zdbs platform from  fllb_cjlb  where id = ? ",
                                                new Object[]{tid},
                                                new RowMapper<Map<String, Object>>() {
                                                        @Override
                                                        public Map<String, Object> mapRow(ResultSet rs, int arg1) throws SQLException {
                                                                Map<String, Object> map = new HashMap<String, Object>();
                                                                map.put("source_id", rs.getInt("sourceid"));
                                                                map.put("platform", rs.getInt("platform"));
                                                                return map;
                                                        }
                                                });
             if (CollectionUtils.isEmpty(list)) {
                     LOG.error("在oracle数据库fllb_cjlb中没有找到记录,id=" + tid);
                     return null;
             }
//             int source_id = (Integer)list.get(0).get("source_id");
             return list.get(0);
        }

}
