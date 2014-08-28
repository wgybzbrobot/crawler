package com.zxsoft.crawler;

import java.util.HashMap;
import java.util.Map;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.JobStatus.State;

public class AdminResource extends ServerResource {
  private static final Logger LOG = LoggerFactory.getLogger(AdminResource.class);

  public static final String PATH = "admin";
  public static final String DESCR = "爬虫管理";

  @Get("json")
  public Object execute() throws Exception {
//    String cmd = (String)getRequestAttributes().get(Params.CMD);
    String cmd = getQuery().getFirstValue(Params.CMD, true);
    
    if ("status".equalsIgnoreCase(cmd)) {
      // status
      Map<String,Object> res = new HashMap<String,Object>();
      res.put("started", CrawlerApp.started);
      Map<String,Object> jobs = new HashMap<String,Object>();      
      jobs.put("all", CrawlerApp.jobMgr.list(null, State.ANY));
      jobs.put("running", CrawlerApp.jobMgr.list(null, State.RUNNING));
      res.put("jobs", jobs);
      res.put("confs", CrawlerApp.confMgr.list());
      return res;
    } else if ("stop".equalsIgnoreCase(cmd)) {
      // stop
      if (CrawlerApp.server.canStop()) {
        Thread t = new Thread() {
          public void run() {
            try {
              Thread.sleep(1000);
              CrawlerApp.server.stop(false);
              LOG.info("Service stopped.");
            } catch (Exception e) {
              LOG.error("Error stopping", e);
            };
          }
        };
        t.setDaemon(true);
        t.start();
        LOG.info("Service shutting down...");
        return "stopping";
      } else {
        LOG.info("Command 'stop' denied due to unfinished jobs");
        return "can't stop now";
      }
    } else {
      return "Unknown command " + cmd;
    }
  }
}
