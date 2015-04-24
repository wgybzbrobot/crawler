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
	Map<String, Object> addJob(Integer reptileId, JobConf jobConf) throws CrawlerException;
	
	/**
	 * 更新任务
	 * @param job
	 */
	void addOrUpdate(Integer reptileId, JobConf job) ;
	
	/**
	 * 查询任务
	 * @param reptileId 
	 */
	Page<JobConf> getJobs(Integer reptileId, String query , int start, int end);

	/**
	 * Get job detail information by job id
	 */
	JobConf getJob(Integer reptileId, long jobId);

	/**
	 * 更新任务
	 * @param job
	 */
	void updateJob (Integer reptileId, JobConf job);
	
	void deleteJob(Integer reptileId, final long jobId);
	
	JobConf querySourceId(int tid) throws CrawlerException;
}
