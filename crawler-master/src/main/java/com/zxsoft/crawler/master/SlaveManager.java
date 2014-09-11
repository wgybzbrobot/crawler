package com.zxsoft.crawler.master;

import java.util.List;
import java.util.Map;

public interface SlaveManager {
  
  public static enum JobType {NETWORK_SEARCH, NETWORK_INSPECT};

  public List<SlaveStatus> list() throws Exception;
  
  /**
   * 创建任务
   * @param slaveId 对应slave rest服务地址
   */
  public String create(Map<String, Object> map) throws Exception;
  
  public boolean abort(String slaveId, String crawlId, String id) throws Exception;
  
  public boolean stop(String slaveId, String crawlId, String id) throws Exception;
}
