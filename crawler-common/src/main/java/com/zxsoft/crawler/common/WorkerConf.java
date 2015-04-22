package com.zxsoft.crawler.common;

import java.io.Serializable;

/**
 * Meta data of worker
 */
public class WorkerConf implements Serializable, Comparable<WorkerConf> {

    /**
      * 
      */
    private static final long serialVersionUID = 8554692687576939572L;

    private int workerId;
    private String hostPort;
    private int runningCount;
    
    /**
     * 状态更新时间
     */
    private long update; 

    //
    // 可添加worker所在机器的系统环境及jvm环境，
    // 用于master加权分配任务。

    @Override
    public int compareTo(WorkerConf c) {
        if (hostPort.equals(c.getHostPort()))
            return 0;
        else if (runningCount > c.getRunningCount())
            return -1;
        else
            return 1;
    }

    @Override
    public boolean equals(Object obj) {
        WorkerConf conf = (WorkerConf) obj;
        return this.hostPort.equals(conf.getHostPort());
    }

    public WorkerConf() {
        update = System.currentTimeMillis();
    }

    public WorkerConf(int workerId, String hostPort) {
        super();
        this.workerId = workerId;
        this.hostPort = hostPort;
        update = System.currentTimeMillis();
    }

    public WorkerConf(int workerId, String hostPort, int runningCount) {
        super();
        this.workerId = workerId;
        this.hostPort = hostPort;
        this.runningCount = runningCount;
        update = System.currentTimeMillis();
    }

    public long getUpdate() {
        return update;
    }

    public void setUpdate(long update) {
        this.update = update;
    }

    public String describe() {
        return hostPort;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    public int getRunningCount() {
        return runningCount;
    }

    public void setRunningCount(int runningCount) {
        this.runningCount = runningCount;
    }

}
