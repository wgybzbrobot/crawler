package com.zxsoft.crawler.master.utils;

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

public class OracleDao extends BaseDao {
        private static Logger LOG = LoggerFactory.getLogger(OracleDao.class);

        private static final String VIEW_SELECT_TASK_EXECUTE_LIST = "SELECT_TASK_EXECUTE_LIST";
        private static final String TABLE_JHRW_RWLB = "JHRW_RWLB";
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
         * 读取视图`SELECT_TASK_EXECUTE_LIST`（读取一次），全部读取
         * 
         * @return
         */
        public List<Map<String, Object>> queryTaskExecuteList() {
                List<Map<String, Object>> list = oracleJdbcTemplate.query("select id, gjc, zdmc from " + VIEW_SELECT_TASK_EXECUTE_LIST,
                                                new RowMapper<Map<String, Object>>() {
                                                        @Override
                                                        public Map<String, Object> mapRow(ResultSet rs, int arg1) throws SQLException {
                                                                Map<String, Object> map = new HashMap<String, Object>();
                                                                map.put("networkSearchTaskId", rs.getString("id"));
                                                                map.put("keyword", rs.getString("gjc"));
                                                                map.put("engineComment", rs.getString("zdmc"));
                                                                return map;
                                                        }
                                                });
                return list;
        }

        /**
         * 读取任务列表`JHRW_RWLB`, 被多次读取
         * 
         * @return
         */
        public List<Map<String, Object>> queryTaskQueue() {
                List<Map<String, Object>> list = oracleJdbcTemplate.query("select gjc, zdmc from " + TABLE_JHRW_RWLB,
                                                new RowMapper<Map<String, Object>>() {
                                                        @Override
                                                        public Map<String, Object> mapRow(ResultSet rs, int arg1) throws SQLException {
                                                                Map<String, Object> map = new HashMap<String, Object>();
                                                                map.put("keyword", rs.getString("gjc"));
                                                                map.put("engineComment", rs.getString("zdmc"));
                                                                return map;
                                                        }
                                                });
                return list;
        }

}
