package com.zxsoft.crawler.master.searchjob;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.master.SlaveResource;
import com.zxsoft.crawler.master.utils.DbService;

/**
 * 全网搜索任务管理器。从oracle中读取任务。
 * 
 * @author xiayun
 *
 */
public final class RecurNetworkSearchThread extends Thread {

    private static Logger LOG = LoggerFactory.getLogger(RecurNetworkSearchThread.class);

    /**
     * 读取周期
     */
    private final int readInterval;

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
        try {
            while (true) {
                List<JobConf> tasks = service.getSearchTaskQueue();
                if (!CollectionUtils.isEmpty(tasks)) {
                    Iterator<JobConf> iter = tasks.iterator();
                    while (iter.hasNext()) {
                        JobConf jobConf = iter.next();
                        iter.remove();

                        try {
                            JobConf jc = service.getBasicInfos(jobConf.getSectionId());
                            jobConf.setSectionId(0);
                            jobConf.merge(jc, jobConf);
                            LOG.debug(jobConf.toString());
                            // Object obj = slaveResource.create(prey);
                            // LOG.info((String) obj);
                        } catch (CrawlerException ce) {
                            LOG.error("create search job failed from JHRW_RWLB.", ce);
                        }
                    }
                }

                try {
                    TimeUnit.SECONDS.sleep(readInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            LOG.error("create search job failed from JHRW_RWLB.", e);
        }
    }

    public static void main(String[] args) {
        new RecurNetworkSearchThread(30000).start();
    }
}
