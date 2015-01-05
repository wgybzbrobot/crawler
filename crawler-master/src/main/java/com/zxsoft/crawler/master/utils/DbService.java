package com.zxsoft.crawler.master.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zxisl.commons.utils.StringUtils;

public class DbService {
        
        private static final OracleDao oracleDao = new OracleDao();
        private static final MysqlDao mysqlDao = new MysqlDao();
        
        /**
         * 此方法仅被调用一次,在master启动后被调用
         * @return
         */
        public List<Map<String, Object>> getSearchTaskList() {
                List<Map<String, Object>> list = oracleDao.queryTaskExecuteList();
                if (StringUtils.isEmpty(list)) {
                     return null;   
                }
                
                List<Map<String, Object>> task = new ArrayList<Map<String,Object>>();
                
                for (Map<String, Object> map : list) {
                        String comment = (String)map.get("engineComment");
                        String url = mysqlDao.getSearchEngineUrl(comment);
                        if (StringUtils.isEmpty(url))
                                continue;
                        map.put("engineUrl", url);
                        task.add(map);
                }
                
                return task;
        }
        
        /**
         * 此方法仅被调用一次,在master启动后被调用
         * @return
         */
        public List<Map<String, Object>> getSearchTaskQueue() {
                List<Map<String, Object>> list = oracleDao.queryTaskQueue();
                if (StringUtils.isEmpty(list)) {
                        return null;   
                }
                
                List<Map<String, Object>> tasks = new ArrayList<Map<String,Object>>();
                
                for (Map<String, Object> map : list) {
                        String comment = (String)map.get("engineComment");
                        String url = mysqlDao.getSearchEngineUrl(comment);
                        if (StringUtils.isEmpty(url))
                                continue;
                        map.put("engineUrl", url);
                        tasks.add(map);
                }
                
                return tasks;
        }
        
        
        
}
