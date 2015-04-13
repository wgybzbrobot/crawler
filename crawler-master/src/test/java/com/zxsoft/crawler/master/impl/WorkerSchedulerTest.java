package com.zxsoft.crawler.master.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zxsoft.crawler.common.WorkerConf;

public class WorkerSchedulerTest {

    @Test
    public void test() throws Exception {
        WorkerConf worker = new WorkerConf(321, "192.168.3.321:8989", 100);
        WorkerScheduler.addWorker(worker);

        worker = new WorkerConf(323, "192.168.3.323:8989", 100);
        WorkerScheduler.addWorker(worker);

        worker = new WorkerConf(324, "192.168.3.323:8989", 20);
        WorkerScheduler.addWorker(worker);
        
        assertTrue(WorkerScheduler.getWorkers().size() == 2);

        assertTrue(WorkerScheduler.getWorker().getRunningCount() == 21);
        
    }
}
