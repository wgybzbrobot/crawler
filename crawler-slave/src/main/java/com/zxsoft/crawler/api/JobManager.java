package com.zxsoft.crawler.api;

import java.util.List;
import java.util.Map;

import com.zxsoft.crawler.api.JobStatus.State;
import com.zxsoft.crawler.common.JobConf;

public interface JobManager {

        public List<JobStatus> list(String crawlId, State state) throws Exception;

        public JobStatus get(String crawlId, String id) throws Exception;

        public JobCode create(JobConf jobConf) throws Exception;

        public boolean abort(String crawlId, String id) throws Exception;

        public boolean stop(String crawlId, String id) throws Exception;

        Map<String, Object> list();

        int getRunningCount();

}
