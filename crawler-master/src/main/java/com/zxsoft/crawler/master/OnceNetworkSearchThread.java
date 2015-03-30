package com.zxsoft.crawler.master;

import java.util.Map;
import java.util.Queue;

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
 */
public final class OnceNetworkSearchThread extends Thread {

    private static Logger LOG = LoggerFactory
                    .getLogger(OnceNetworkSearchThread.class);

    public OnceNetworkSearchThread() {
        setName("OnceNetworkSearchJob");
    }

    @Override
    public void run() {
        /*
         * 读取视图`SELECT_TASK_EXECUTE_LIST`（读取一次），全部读取
         */
        SlaveResource slaveResource = new SlaveResource();
        DbService service = new DbService();
        Queue<Map<String, Object>> queue = service.getSearchTaskList();
        // Queue<Map<String, Object>> queue = list.;
        if (!CollectionUtils.isEmpty(queue)) {
            // for (Map<String, Object> _map : list) {
            Map<String, Object> _map = null;
            int i = 0;
            while (null != (_map = queue.poll())) {
                try {
                    i++;
                    String engineUrl = (String) _map.get(Params.ENGINE_URL);

                    Map<String, Object> map = service.getBasicInfos(engineUrl);
                    int source_id = (Integer) map.get("source_id");
                    int sectionId = (Integer) map.get("sectionId");
                    String comment = (String) map.get("comment");
                    String source_name = comment;
                    int country_code = (Integer) map.get("region");
                    int province_code = (Integer) map.get("provinceId");
                    int city_code = (Integer) map.get("cityId");
                    int platform = (Integer) _map.get("platform");

                    Prey prey = new Prey(JobType.NETWORK_SEARCH, engineUrl,
                                    (String) _map.get(Params.KEYWORD),
                                    platform, source_id, source_name,
                                    sectionId, comment, country_code,
                                    province_code, city_code);
                    prey.setSource_id((Integer) _map.get("source_id"));
                    prey.setJobId((Integer) _map.get("jobId"));

                    LOG.info(prey.toString());
                    Object obj = slaveResource.create(prey);
                    // LOG.info((String) obj);
                } catch (Exception e) {
                    LOG.error("create search job failed from oracle db.", e);
                }

                if (i % 100 == 0)
                    try {
                        Thread.sleep(10000L);
                    } catch (InterruptedException e) {

                    }
            }
            LOG.info("Finish read job from SELECT_TASK_EXECUTE_LIST.");
        }
    }

}
