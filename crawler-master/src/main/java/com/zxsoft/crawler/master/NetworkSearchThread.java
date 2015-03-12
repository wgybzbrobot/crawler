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
 * 全网搜索任务管理器。从oracle中读取任务。
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
                if (!CollectionUtils.isEmpty(list)) {
                        for (Map<String, Object> _map : list) {
                                String engineUrl = (String) _map.get(Params.ENGINE_URL);
                                
                                Map<String, Object> map = service.getBasicInfos(engineUrl);
                                int source_id = (Integer)map.get("source_id");
                                int sectionId = (Integer)map.get("sectionId");
                                String comment = (String)map.get("comment");
                                int country_code = (Integer)map.get("region");
                                int province_code = (Integer)map.get("provinceId");
                                int city_code = (Integer)map.get("cityId");
                                
                                Prey prey = new Prey(JobType.NETWORK_SEARCH, engineUrl ,
                                                                (String) _map.get(Params.KEYWORD),  source_id,  sectionId,  comment, 
                                                                 country_code,  province_code,  city_code);
                                prey.setSource_id((Integer) _map.get("ly"));
                                prey.setJobId((Integer)_map.get("jobId"));
                                try {
                                        LOG.info(prey.toString());
                                        Object obj = slaveResource.create(prey);
//                                        LOG.info((String) obj);
                                } catch (Exception e) {
                                        LOG.error("创建从数据库中获取全网搜索的任务失败", e);
                                }
                        }
                }

                /*
                 * 读取任务列表`JHRW_RWLB`（循环读取）
                 */
                while (true) {
                        try {
                                List<Map<String, Object>> tasks = service.getSearchTaskQueue();
                                if (CollectionUtils.isEmpty(tasks)) {
                                        for (Map<String, Object> _map : tasks) {
                                                String engineUrl = (String) _map.get(Params.ENGINE_URL);
                                                Map<String, Object> map = service.getBasicInfos(engineUrl);
                                                int source_id = (Integer)map.get("source_id");
                                                int sectionId = (Integer)map.get("sectionId");
                                                String comment = (String)map.get("comment");
                                                int country_code = (Integer)map.get("region");
                                                int province_code = (Integer)map.get("provinceId");
                                                int city_code = (Integer)map.get("cityId");
                                                
                                                Prey prey = new Prey(JobType.NETWORK_SEARCH, engineUrl ,
                                                                                (String) _map.get(Params.KEYWORD),  source_id,  sectionId,  comment, 
                                                                                 country_code,  province_code,  city_code);
                                                prey.setSource_id((Integer) _map.get("source_id"));
                                                prey.setJobId((Integer)_map.get("jobId"));
                                                try {
                                                        Object obj = slaveResource.create(prey);
        //                                                LOG.info((String) obj);
        //                                                LOG.info(prey.toString());
                                                } catch (Exception e) {
                                                        LOG.error("", e);
                                                }
                                        }
                                }
                        } catch (Exception e) {
                                LOG.error("任务列表`JHRW_RWLB`（循环读取）出错.", e);
                        }

                        try {
                                TimeUnit.SECONDS.sleep(readInterval);
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }
                }
        }

}
