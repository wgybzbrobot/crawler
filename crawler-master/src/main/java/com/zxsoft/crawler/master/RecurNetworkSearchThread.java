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
public final class RecurNetworkSearchThread extends Thread {

    private static Logger LOG = LoggerFactory
                    .getLogger(RecurNetworkSearchThread.class);

    /**
     * 读取周期
     */
    private int readInterval;

    public RecurNetworkSearchThread(int readInterval) {
        this.readInterval = readInterval;
        setName("RecurNetworkSearchJob");
    }

    @Override
    public void run() {
        SlaveResource slaveResource = new SlaveResource();
        DbService service = new DbService();

        /*
         * 读取任务列表`JHRW_RWLB`（循环读取）
         */
        while (true) {
            try {
                List<Map<String, Object>> tasks = service.getSearchTaskQueue();
                if (CollectionUtils.isEmpty(tasks)) {
                    for (Map<String, Object> _map : tasks) {
                        String engineUrl = (String) _map.get(Params.ENGINE_URL);
                        LOG.debug("Read Job from JHRW_RWLB, engineUrl:"
                                        + engineUrl);
                        Map<String, Object> map = service
                                        .getBasicInfos(engineUrl);
                        int source_id = (Integer) map.get("source_id");
                        int sectionId = (Integer) map.get("sectionId");
                        String comment = (String) map.get("comment");
                        String source_name = comment;
                        int country_code = (Integer) map.get("region");
                        int province_code = (Integer) map.get("provinceId");
                        int city_code = (Integer) map.get("cityId");
                        int platform = (Integer) map.get("platform");

                        Prey prey = new Prey(JobType.NETWORK_SEARCH, engineUrl,
                                        (String) _map.get(Params.KEYWORD),
                                        platform, source_id, source_name,
                                        sectionId, comment, country_code,
                                        province_code, city_code);
                        prey.setSource_id((Integer) _map.get("source_id"));
                        prey.setJobId((Integer) _map.get("jobId"));
                        LOG.debug("jobId:" + prey.getJobId());
                        try {
                            Object obj = slaveResource.create(prey);
                            // LOG.info((String) obj);
                            // LOG.info(prey.toString());
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
