package com.zxsoft.crawler.master;

import java.util.List;
import com.zxsoft.crawler.api.Prey;

public interface SlaveManager {
  
  public List<SlaveStatus> list() throws Exception;
  
  /**
   * 创建任务
   */
  public String create(Prey prey) throws Exception;
  
  public boolean abort(String slaveId, String crawlId, String id) throws Exception;
  
  public boolean stop(String slaveId, String crawlId, String id) throws Exception;
}
