package com.zxsoft.crawler;

import java.util.List;
import java.util.Map;

import com.zxsoft.crawler.JobStatus.State;

public interface JobManager {

	public static enum JobType {
		NETWORK_SEARCH, NETWORK_INSPECT
	};

	public List<JobStatus> list(String crawlId, State state) throws Exception;

	public JobStatus get(String crawlId, String id) throws Exception;

	public String create(String crawlId, JobType type, Map<String, Object> args) throws Exception;

	public boolean abort(String crawlId, String id) throws Exception;

	public boolean stop(String crawlId, String id) throws Exception;

	Map<String, Object> list();

}
