package com.zxsoft.crawler.master;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.api.Prey;
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
         * 读取周期
         */
        private int readInterval;

        public NetworkSearchThread(int readInterval) {
                this.readInterval = readInterval;
        }

        @Override
        public void run() {
                /*
                 * 读取视图`SELECT_TASK_EXECUTE_LIST`（读取一次），全部读取
                 */
                SlaveResource slaveResource = new SlaveResource();
                DbService service = new DbService();
                List<Map<String, Object>> list = service.getSearchTaskList();
                if (CollectionUtils.isEmpty(list)) {
                        for (Map<String, Object> args : list) {
                                Prey prey = new Prey(JobType.NETWORK_SEARCH, (String) args.get(Params.ENGINE_URL),
                                                                (String) args.get(Params.KEYWORD));
                                try {
                                        Object obj = slaveResource.create(prey);
                                        LOG.info((String) obj);
                                } catch (Exception e) {
                                        LOG.error("创建从数据库中获取全网搜索的任务失败", e);
                                }
                        }
                }

                /*
                 * 读取任务列表`JHRW_RWLB`（循环读取）
                 */
                while (true) {

                        List<Map<String, Object>> tasks = service.getSearchTaskQueue();
                        if (CollectionUtils.isEmpty(tasks)) {
                                for (Map<String, Object> args : tasks) {
                                        Prey prey = new Prey(JobType.NETWORK_SEARCH, (String) args.get(Params.ENGINE_URL),
                                                                        (String) args.get(Params.KEYWORD));
                                        try {
                                                Object obj = slaveResource.create(prey);
                                                LOG.info((String) obj);
                                        } catch (Exception e) {
                                                LOG.error("", e);
                                        }
                                }
                        }

                        try {
                                TimeUnit.SECONDS.sleep(readInterval);
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }
                }
        }

}
