package com.zxsoft.crawler.master;

import java.util.List;

import com.zxsoft.crawler.api.JobCode;
import com.zxsoft.crawler.common.JobConf;

public interface SlaveManager {
  
  public List<SlaveStatus> list() throws Exception;
  
  /**
   * 创建任务
   */
  public JobCode create(JobConf jobConf) throws Exception;
  
}
