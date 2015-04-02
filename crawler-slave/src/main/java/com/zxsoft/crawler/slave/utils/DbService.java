package com.zxsoft.crawler.slave.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.slave.utils.OracleDao.Status;

/**
 * 处理数据库数据
 * 
 * @author xiayun
 *
 */
public class DbService {
        private static Logger LOG = LoggerFactory.getLogger(DbService.class);
        private static final OracleDao oracleDao = new OracleDao();

        /**
         * 更新从数据库表JHRW_RWZX中获取的全网搜索任务执行状态
         */
        public void updateExecuteTaskStatus(int id, Status status) {
                int num = oracleDao.updateTaskExecuteStatus(id, status);
                if (num == -1) {
                        LOG.error("更新JHRW_RWZX全网搜索任务执行状态失败, id=" + id);
                } else {
                        LOG.debug("更新JHRW_RWZX全网搜索任务执行状态成功, id=" + id);
                }
        }
        /**
         * 将任务执行表`JHRW_RWZX`中机器号为自身的且ZT字段置为２，ZSZT置为１．（执行一次）
         */
        public void updateExecuteTaskStatus() {
                int num = oracleDao.updateTaskExecuteStatus();
                if (num == -1) {
                        LOG.error("将任务执行表`JHRW_RWZX`中机器号为自身的且ZT字段置为２，ZSZT置为１失败");
                } else {
                        LOG.debug("将任务执行表`JHRW_RWZX`中机器号为自身的且ZT字段置为２，ZSZT置为１");
                }
        }

        /**
         * 将任务列表`JHRW_RWLB`中对应任务记录删除
         * 
         * @param id
         */
        public void deleteTaskById(int id) {
                int num = oracleDao.deleteTaskListById(id);
                if (num == -1) {
                        LOG.error("删除任务列表JHRW_RWLB中对应任务记录失败, id=" + id);
                } else {
                        LOG.debug("删除任务列表JHRW_RWLB中对应任务记录成功, id=" + id);
                }
        }

        /**
         * 将任务执行表`JHRW_RWZX`中对应任务记录的机器号字段置为本机器
         * 
         * @param id
         */
        public void updateMachineFlagTaskById(int id) {
                int num = oracleDao.updateMachineFlagTaskById(id);
                if (num == -1) {
                        LOG.error("将任务执行表`JHRW_RWZX`中对应任务记录的机器号字段置为本机器失败, id=" + id);
                } else {
                        LOG.debug("将任务执行表`JHRW_RWZX`中对应任务记录的机器号字段置为本机器成功, id=" + id);
                }
        }

}
