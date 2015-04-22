package com.zxsoft.crawler.web.service.crawler;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.JobConf;

@Service
public interface JobService {

	/**
	 * 添加任务
	 * @throws CrawlerException 
	 */
	Map<String, Object> addJob( JobConf jobConf) throws CrawlerException;
	
	/**
	 * 查询任务
	 */
	Page<JobConf> getJobs(String query , int start, int end);

	/**
	 * Get job detail information by job id
	 */
	JobConf getJob(int jobId);

	/**
	 * 更新任务
	 * @param job
	 */
	void updateJob (JobConf job);
	
	void deleteJob(final long jobId);
	
	JobConf querySourceId(int tid) throws CrawlerException;
}
