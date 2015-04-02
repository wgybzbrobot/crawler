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

import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.common.JobConfBuilder;

public class OracleDao extends BaseDao {
        private static Logger LOG = LoggerFactory.getLogger(OracleDao.class);

        private static final JdbcTemplate oracleJdbcTemplate;
        static {
                OracleDriver driver  = new OracleDriver();
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
        public List<JobConf> queryTaskExecuteList() {
                List<JobConf> list = oracleJdbcTemplate.query("select a.id, a.gjc, a.ly, b.sourceid , b.zdbs platform"
                        + " from SELECT_TASK_EXECUTE_LIST a, fllb_cjlb b where a.ly = b.id and b.zdbs != 3 ",
                                                new RowMapper<JobConf>() {
                                                        @Override
                                                        public JobConf mapRow(ResultSet rs, int arg1) throws SQLException {
                                                                JobConf jobConf = new JobConf();
                                                                jobConf.setJobId(rs.getInt("id"));
                                                                jobConf.setKeyword(rs.getString("gjc"));
                                                                jobConf.setSectionId(rs.getInt("ly")); // 这不是真的sectionid，这里只是用来保存ly字段
                                                                jobConf.setSource_id(rs.getInt("sourceid"));
                                                                jobConf.setPlatform(rs.getInt("platform"));
                                                                
                                                                return jobConf;
                                                        }
                                                });
             
                return list;
        }

        /**
         * 读取任务列表`JHRW_RWLB`, 被多次读取
         */
        public List<JobConf> queryTaskQueue() {
            List<JobConf> list = oracleJdbcTemplate.query("select a.id, a.gjc, a.ly, b.sourceid, b.zdbs platform from JHRW_RWLB a, fllb_cjlb b "
                        + " where a.ly = b.id and b.zdbs != 3 ",
                                                new RowMapper<JobConf>() {
                                                        @Override
                                                        public JobConf mapRow(ResultSet rs, int arg1) throws SQLException {
                                                            JobConf jobConf = new JobConf();
                                                            jobConf.setJobId(rs.getInt("id"));
                                                            jobConf.setKeyword(rs.getString("gjc"));
                                                            jobConf.setSectionId(rs.getInt("ly"));
                                                            jobConf.setSource_id(rs.getInt("sourceid"));
                                                            jobConf.setPlatform(rs.getInt("platform"));
                                                            
                                                            return jobConf;
                                                        }
                                                });

                return list;
        }

}
