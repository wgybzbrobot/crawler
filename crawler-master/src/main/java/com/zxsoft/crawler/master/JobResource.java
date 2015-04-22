package com.zxsoft.crawler.master;

import org.restlet.data.Form;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.api.JobCode;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.urlbase.Page;

public class JobResource extends ServerResource {

    private static Logger LOG = LoggerFactory.getLogger(JobResource.class);


    /**
     * 创建任务
     * @param jobConf
     * @return
     * @throws Exception
     */
    @Post("json")
    public Object createJob(JobConf jobConf) throws Exception {
        LOG.debug("Create Job: " + jobConf.toString());
        JobCode code = MasterApp.slaveMgr.create(jobConf);
        return code;
    }
    
    /**
     * 获取或删除队列中的任务.
     * <p>获取任务
     * 参数：operation=get&query=foo&start=0&end=10
     * <p>删除任务
     * 参数：operation=delete&jobId=1
     * @return
     * @throws Exception
     */
    @Get("json")
    public Object jobs() throws Exception {
        
//        String operation  = (String)getRequestAttributes().get("operation");
        Form form = getQuery();
        if (form == null) throw new Exception("No parameter specified");

        String operation = form.getFirstValue("operation");
        
        if ("delete".equals(operation )) {
            Integer jobId = Integer.valueOf(form.getFirstValue("jobId"));
            if (jobId == null || jobId == 0) {
                throw new CrawlerException(ErrorCode.CONF_ERROR, "Missing jobId");
              }
            MasterApp.slaveMgr.deleteJob(jobId);
            return "";
        } else if ("get".equals(operation)) {
            String query = form.getFirstValue("query");
            Integer start = Integer.valueOf(form.getFirstValue("start",true, "0"));
            Integer end = Integer.valueOf(form.getFirstValue("end",true, "10"));
            Page<JobConf> jobs = MasterApp.slaveMgr.getJobs(query, start, end);
            return jobs;
        } else {
            throw new Exception("No operation specified");
        }
        
    }

    /**
     * 更新任务
     * @param jobConf
     * @return
     * @throws Exception
     */
    @Put("json")
    public Object update(JobConf jobConf) throws Exception {
        LOG.info("Create Job: " + jobConf.toString());
        JobCode code = MasterApp.slaveMgr.create(jobConf);
        return code;
    }

}
