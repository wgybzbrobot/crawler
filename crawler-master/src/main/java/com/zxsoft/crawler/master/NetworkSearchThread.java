package com.zxsoft.crawler.master;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxsoft.crawler.master.SlaveManager.JobType;
import com.zxsoft.crawler.master.utils.DbService;

/**
 * 全网搜索任务管理器
 * 
 * @author xiayun
 *
 */
public final class NetworkSearchThread implements Runnable {

        private static Logger LOG = LoggerFactory.getLogger(NetworkSearchThread.class);
        
        /**
         * 读取周期 10秒
         */
        private static final int READ_INTERVAL = 10;

        @Override
        public void run() {
                /*
                 * 读取视图`SELECT_TASK_EXECUTE_LIST`（读取一次），全部读取
                 */
                SlaveResource slaveResource = new SlaveResource();
                DbService service = new DbService();
                List<Map<String, Object>> list = service .getSearchTaskList();
                if (CollectionUtils.isEmpty(list)) {
                        for (Map<String, Object> args : list) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("jobType", JobType.NETWORK_SEARCH.toString());
                                map.put("args", args);
                                try {
                                        Object obj = slaveResource.create(map);
                                        LOG.info((String)obj);
                                } catch (Exception e) {
                                        LOG.error("", e);
                                }
                        }
                }
                
                /*
                 * 读取任务列表`JHRW_RWLB`（循环读取）
                 */
                while (true) {
                        
                        List<Map<String, Object>> tasks = service .getSearchTaskQueue();
                        if (CollectionUtils.isEmpty(tasks)) {
                                for (Map<String, Object> args : tasks) {
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("jobType", JobType.NETWORK_SEARCH.toString());
                                        map.put("args", args);
                                        try {
                                                Object obj = slaveResource.create(map);
                                                LOG.info((String)obj);
                                        } catch (Exception e) {
                                                LOG.error("", e);
                                        }
                                }
                        }
                        
                        try {
                                TimeUnit.SECONDS.sleep(READ_INTERVAL);
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }
                }
        }

}
