package com.zxsoft.crawler.slave.utils;

import java.util.List;

import oracle.jdbc.driver.OracleDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.IPUtil;
import com.zxsoft.crawler.api.SlaveServer;

/**
 * 访问oracle数据库
 * 
 * @author xiayun
 *
 */
public class OracleDao {

    private static Logger LOG = LoggerFactory.getLogger(OracleDao.class);

    private static final String TABLE_JHRW_RWLB = "JHRW_RWLB";
    private static final String TABLE_JHRW_RWZX = "JHRW_RWZX";
    private final JdbcTemplate oracleJdbcTemplate;

    public OracleDao(String url, String username, String passwd) {
        LOG.info("oracle database address: " + url);
        OracleDriver driver = new OracleDriver();
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource(driver, url,
                        username, passwd);
        oracleJdbcTemplate = new JdbcTemplate(dataSource);
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
     *            任务在表JHRW_RWZX中的id
     * @param status
     *            任务状态
     * @return
     */
    public int updateTaskExecuteStatus(long id, Status status) {
        int num = oracleJdbcTemplate.update("update " + TABLE_JHRW_RWZX
                        + " set ZT=?, ZSZT=? where id=?", 2, status.ordinal(), id);
        return num;
    }

    /**
     * 将任务执行表`JHRW_RWZX`中机器号为自身的且ZT字段置为２，ZSZT置为１．（执行一次）
     * 
     * @return
     */
    public int updateTaskExecuteStatus() {
        int num = oracleJdbcTemplate.update("update " + TABLE_JHRW_RWZX
                        + " set ZT=2, ZSZT=1 where jqh=?", SlaveServer.getMachineId());
        return num;
    }

    /**
     * 将任务列表`JHRW_RWLB`中对应任务记录删除
     * 
     * @param id
     */
    public int deleteTaskListById(long id) {
        int i = oracleJdbcTemplate
                        .update("delete " + TABLE_JHRW_RWLB + " where id=?", id);
        return i;
    }

    /**
     * 将任务执行表`JHRW_RWZX`中对应任务记录的机器号字段置为本机器
     * 
     * @param id
     * @return
     */
    public int updateMachineFlagTaskById(long id) {
        int i = oracleJdbcTemplate.update("update " + TABLE_JHRW_RWZX
                        + " set jqh=? where id=?", SlaveServer.getMachineId(), id);
        return i;
    }

}
