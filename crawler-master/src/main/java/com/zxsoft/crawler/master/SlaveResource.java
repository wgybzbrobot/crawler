//package com.zxsoft.crawler.master;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.restlet.resource.Get;
//import org.restlet.resource.Post;
//import org.restlet.resource.ServerResource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.zxsoft.crawler.api.JobCode;
//import com.zxsoft.crawler.common.JobConf;
//
//public class SlaveResource extends ServerResource {
//
//    private static Logger LOG = LoggerFactory.getLogger(SlaveResource.class);
//
//    @Get("json")
//    public Object retrieve() throws Exception {
//
//        List<SlaveStatus> list = MasterApp.slaveMgr.list();
//
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("slavestatus", list);
//
//        return map;
//
//    }
//
//    @Post("json")
//    public Object create(JobConf jobConf) throws Exception {
//        LOG.info("Create Job: " + jobConf.toString());
//        JobCode code = MasterApp.slaveMgr.create(jobConf);
//        return code;
//    }
//
//}
