package com.zxsoft.crawler.master.utils;

import java.util.List;

import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.JobConf;

public class DbService {
        
        private static final OracleDao oracleDao = new OracleDao();
        private static final MysqlDao mysqlDao = new MysqlDao();
        
        /**
         * 此方法仅被调用一次,在master启动后被调用
         * @return
         */
        public List<JobConf> getSearchTaskList() {
                List<JobConf> list = oracleDao.queryTaskExecuteList();
                return list;
        }
        
        /**
         *  读取任务列表`JHRW_RWLB`（循环读取）
         * @return
         */
        public List<JobConf> getSearchTaskQueue() {
                List<JobConf> list = oracleDao.queryTaskQueue();
                return list;
        }
        
        public JobConf getBasicInfos(int tid) throws CrawlerException {
                return mysqlDao.getBasicInfos(tid);
        }
        
        public String getLocation(String ip) {
            return oracleDao.getLocation(ip);
        }
        
        public int getLocationCode(String ip) {
            return oracleDao.getLocationCode(ip);
        }
}
