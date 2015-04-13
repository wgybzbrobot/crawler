package com.zxsoft.crawler.master;

import java.util.List;

import com.zxsoft.crawler.api.JobCode;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.common.WorkerConf;
import com.zxsoft.crawler.urlbase.Page;

public interface SlaveManager {
  
//  public List<SlaveStatus> list() throws Exception;
  
  /*
   * worker
   */
  public List<WorkerConf>  getWorkers();
  public void workerTick(WorkerConf worker);
  
  /*
   * job
   */
  /**
   * 创建任务
   */
  public JobCode create(JobConf jobConf) throws Exception;
  public Page<JobConf> getJobs(String query, int start, int end);
  public void deleteJob(int jobId) throws CrawlerException;
  
  
  
}
