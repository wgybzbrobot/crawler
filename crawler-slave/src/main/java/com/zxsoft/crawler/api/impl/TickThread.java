package com.zxsoft.crawler.api.impl;

import java.util.concurrent.TimeUnit;

import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.api.SlaveApp;
import com.zxsoft.crawler.api.SlaveServer;
import com.zxsoft.crawler.common.WorkerConf;
import com.zxsoft.crawler.master.MasterPath;

public class TickThread extends Thread {

    private static Logger LOG = LoggerFactory.getLogger(TickThread.class);
    
    private final String masterWorkerLoc;
    private final int machineId;
    private final String hostPort;
    private final long tickTimeMs;
    
    public TickThread(String masterHost, int machineId, String hostPort, long tickTimeMs) {
        if (!masterHost.endsWith("/")) {
            masterHost = masterHost + "/";
        }
        this.masterWorkerLoc = "http://" + masterHost + "master/" + MasterPath.WORKER_RESOURCE_PATH;
        this.machineId = machineId;
        this.hostPort = hostPort;
        this.tickTimeMs = tickTimeMs;
        setName("Worker-Tick-Thread");
    }
    
    
    @Override
    public void run() {
        ClientResource cli = new ClientResource(masterWorkerLoc);
        while (SlaveServer.isRunning()) {
            JacksonRepresentation<WorkerConf> jr = null;
            int count = SlaveApp.jobMgr.getRunningCount();
            WorkerConf worker = new WorkerConf(machineId, hostPort, count);
            jr = new JacksonRepresentation<WorkerConf>(worker);
            try {
                cli.post(jr);
            } catch (Exception e) {
                LOG.error("连接主控失败", e);
                continue;
            }/* finally {
                cli.release();
            }*/
            LOG.debug("Connect to master success.");
            try {
                TimeUnit.MILLISECONDS.sleep(tickTimeMs);
            } catch (InterruptedException e) {
                LOG.error("Catch InterruptedException", e);
            }
        }
    }
}
