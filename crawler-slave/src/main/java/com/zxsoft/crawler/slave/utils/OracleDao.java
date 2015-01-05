package com.zxsoft.crawler.slave.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import oracle.jdbc.driver.OracleDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.IPUtil;

/**
 * 访问oracle数据库
 * @author xiayun
 *
 */
public class OracleDao {

        private static Logger LOG = LoggerFactory.getLogger(OracleDao.class);

        private static final String TABLE_JHRW_RWLB = "JHRW_RWLB";
        private static final String TABLE_JHRW_RWZX = "JHRW_RWZX";
        private static final JdbcTemplate oracleJdbcTemplate;
        private static String machineId;

        static {
                OracleDriver driver = new OracleDriver();
                Properties prop = new Properties();
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                InputStream stream = loader.getResourceAsStream("oracle.properties");
                try {
                        prop.load(stream);
                } catch (IOException e) {
                        LOG.error("加载oracle.properties失败", e);
                }
                String url = prop.getProperty("db.url");
                String username = prop.getProperty("db.username");
                String password = prop.getProperty("db.password");
                LOG.info("oracle database address: " + url);
                SimpleDriverDataSource dataSource = new SimpleDriverDataSource(driver, url, username, password);
                oracleJdbcTemplate = new JdbcTemplate(dataSource);

                List<String> ips = IPUtil.getIPv4();
                if (!CollectionUtils.isEmpty(ips)) {
                        String str = ips.get(0);
                        machineId = str.replaceAll("\\.", "");
                        LOG.info(machineId);
                }
        }

        public JdbcTemplate getOracleJdbcTemplate() {
                return oracleJdbcTemplate;
        }

        public enum Status {
                NOT_KNOWN, FAILURE, SUCCESS
        }

        /**
         * 更改任务执行状态
         * <p>
         * 如果执行成功，将任务执行表`JHRW_RWZX`中对应任务记录ZT字段置为２，ZSZT置为２<br/>
         * 如果执行失败，将任务执行表`JHRW_RWZX`中对应任务记录ZT字段置为２，ZSZT置为1.
         * 
         * @param id
         *                任务在表JHRW_RWZX中的id
         * @param status
         *                任务状态
         * @return
         */
        public int updateTaskExecuteStatus(int id, Status status) {
                int num = oracleJdbcTemplate.update("update " + TABLE_JHRW_RWZX + " set ZT=?, ZSZT=? where id=?", 2, status.ordinal(), id);
                return num;
        }

        /**
         * 将任务执行表`JHRW_RWZX`中机器号为自身的且ZT字段置为２，ZSZT置为１．（执行一次）
         * @return
         */
        public int updateTaskExecuteStatus() {
                int num = oracleJdbcTemplate.update("update " + TABLE_JHRW_RWZX + " set ZT=2, ZSZT=1 where jqh=?", machineId);
                return num;
        }

        /**
         * 将任务列表`JHRW_RWLB`中对应任务记录删除
         * 
         * @param id
         */
        public int deleteTaskListById(int id) {
                int i = oracleJdbcTemplate.update("delete " + TABLE_JHRW_RWLB + " where id=?", id);
                return i;
        }

        /**
         * 将任务执行表`JHRW_RWZX`中对应任务记录的机器号字段置为本机器
         * 
         * @param id
         * @return
         */
        public int updateMachineFlagTaskById(int id) {
                int i = oracleJdbcTemplate.update("update " + TABLE_JHRW_RWZX + " set jqh=? where id=?", machineId, id);
                return i;
        }

}
