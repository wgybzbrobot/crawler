package com.zxsoft.crawler.api.impl;

import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.master.MasterPath;

public class ErrorHandler {

    private static Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);

    private static String master;

    public ErrorHandler() {
    }

    public static void setMaster(String masterLoc) {
        master = "http://" + masterLoc + "master/" + MasterPath.JOB_RESOURCE_PATH;
    }
    
//    public ErrorHandler(String masterLoc) {
//        if (!masterLoc.endsWith("/")) {
//            masterLoc = masterLoc + "/";
//        }
//        master = "http://" + masterLoc + "master/" + MasterPath.JOB_RESOURCE_PATH;
//    }

    public static void handle(CrawlerException e, JobConf job) {
//        LOG.debug("CrawlerException code:" + e.code(), e);
        if (e.code() == ErrorCode.NETWORK_ERROR.code) {
            if (master != null) {
                LOG.debug("Network error, commit job to master for retry");
                job.setRecurrence(false);
                job.setRetry(job.getRetry() + 1);
                job.setPrevFetchTime(System.currentTimeMillis() - job.getFetchinterval() * 60 * 1000L);
                ClientResource cli = new ClientResource(master);
                JacksonRepresentation<JobConf> jr = new JacksonRepresentation<JobConf>(job);
                try {
                    cli.post(jr);
                } catch (ResourceException re) {
                    LOG.warn("Cannot connect to master," + re.getMessage());
                }
            }
        } else if (e.code() == ErrorCode.CONF_ERROR.code) {
            LOG.error("Conf error: " + e.getMessage());
        }

    }
}
