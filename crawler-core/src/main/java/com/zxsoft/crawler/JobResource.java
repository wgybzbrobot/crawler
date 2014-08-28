/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.zxsoft.crawler;

import java.util.Map;

import org.restlet.data.Form;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.zxsoft.crawler.JobManager.JobType;
import com.zxsoft.crawler.JobStatus.State;

public class JobResource extends ServerResource {
  public static final String PATH = "jobs";
  public static final String DESCR = "任务管理";
  
  @Get("json")
  public Object retrieve() throws Exception {
    String cid = null;
    String jid = null;
    String cmd = null;
    Form form = getQuery();
//    cid = (String)getRequestAttributes().get(Params.CRAWL_ID);
//    jid = (String)getRequestAttributes().get(Params.JOB_ID);
//    cmd = (String)getRequestAttributes().get(Params.CMD);
    cid = getQuery().getFirstValue(Params.CRAWL_ID, true);
    jid = getQuery().getFirstValue(Params.JOB_ID, true);
    cmd = getQuery().getFirstValue(Params.CMD, true);
    if (form != null) {
      String v = form.getFirstValue(Params.CRAWL_ID);
      if (v != null) cid = v;
      v = form.getFirstValue(Params.JOB_ID);
      if (v != null) jid = v;
      v = form.getFirstValue(Params.CMD);
      if (v != null) cmd = v;
    }
    if (jid == null) {
      return CrawlerApp.jobMgr.list(cid, State.ANY);
    } else {
      // handle stop / abort / get
      if (cmd == null) {
        return CrawlerApp.jobMgr.get(cid, jid);
      }
      if (cmd.equals(Params.JOB_CMD_STOP)) {
        return CrawlerApp.jobMgr.stop(cid, jid);
      } else if (cmd.equals(Params.JOB_CMD_ABORT)) {
        return CrawlerApp.jobMgr.abort(cid, jid);
      } else if (cmd.equals(Params.JOB_CMD_GET)) {
        return CrawlerApp.jobMgr.get(cid, jid);
      } else {
        throw new Exception("Unknown command: " + cmd);
      }
    }
  }
  
  /**
   * 创建新任务接口参数：
   * <ol>
   * <li>crawlId	爬虫编号(唯一标识)</li>
   * <li>jobType	任务类型</li>
   * <li>url	网址</li>
   * <li>urlType	网址类型, 不同类型的网址走不同的代理</li>
   * <li>prevFetchTime		上次抓取时间,可选配置</li>
   * </ol>
   */
  @Put("json")
  @SuppressWarnings("unchecked")
  public Object create(Map<String,Object> args) throws Exception {
    String cid = (String)args.get(Params.CRAWL_ID);
    String typeString = (String)args.get(Params.JOB_TYPE);
    JobType jobType = JobType.valueOf(typeString.toUpperCase());
    
    Object map = args.get(Params.ARGS);
    Map<String,Object> cmdArgs = null;
    if(map instanceof Map<?,?>)
      cmdArgs = (Map<String,Object>)map;
    
    String jobId = CrawlerApp.jobMgr.create(cid, jobType, cmdArgs);
    return jobId;
  }
}
