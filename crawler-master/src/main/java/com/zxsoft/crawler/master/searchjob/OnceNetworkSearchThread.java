package com.zxsoft.crawler.master.searchjob;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.master.SlaveResource;
import com.zxsoft.crawler.master.utils.DbService;

/**
 * 全网搜索任务管理器。从oracle中读取任务。
 *
 */
public final class OnceNetworkSearchThread extends Thread {

    private static Logger LOG = LoggerFactory.getLogger(OnceNetworkSearchThread.class);

    public OnceNetworkSearchThread() {
        setName("OnceNetworkSearchJob");
    }

    @Override
    public void run() {
        /*
         * 读取视图`SELECT_TASK_EXECUTE_LIST`（读取一次），全部读取
         */
        DbService service = new DbService();

        List<JobConf> jobConfs = service.getSearchTaskList();

        if (CollectionUtils.isEmpty(jobConfs)) {
            LOG.info("No task in SELECT_TASK_EXECUTE_LIST, exit.");
            return;
        }

        Iterator<JobConf> iter = jobConfs.iterator();

        int i = 0;
        SlaveResource slaveResource = new SlaveResource();
        while (iter.hasNext()) {
            try {
                i++;
                JobConf jobConf = iter.next();
                iter.remove();

                JobConf jc = service.getBasicInfos(jobConf.getSectionId());
                jobConf.setSectionId(0);
                jobConf.merge(jc, jobConf);

                LOG.debug(jobConf.toString());
//                Object obj = slaveResource.create(jobConf);
//                 LOG.debug((String) obj);
            } catch (Exception e) {
                LOG.error("create search job failed from SELECT_TASK_EXECUTE_LIST.", e);
            }

            if (i % 100 == 0)
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {

                }
        }
        LOG.info("Finish read job from SELECT_TASK_EXECUTE_LIST.");
    }

    public static void main(String[] args) {
        new OnceNetworkSearchThread().start();
    }
}
