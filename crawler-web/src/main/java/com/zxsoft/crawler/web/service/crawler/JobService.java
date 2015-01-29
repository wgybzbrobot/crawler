package com.zxsoft.crawler.web.service.crawler;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.zxsoft.crawler.api.Prey;

public interface JobService {

	/**
	 * 添加网络巡检任务
	 * @param args 参数:版块地址，上次抓取时间 ...
	 */
	Map<String, Object> addInsecptJob( Map<String, Object> args);
	
	/**
	 * 添加全网搜索任务
	 * @param args 参数: 搜索关键字, 搜索引擎url ...
	 */
	Map<String, Object> addSearchJob( Prey prey);
	
	/**
	 * get all jobs 
	 */
	List<Map<String, Object>> jobs();

	/**
	 * get a job by jid
	 * @param cid crawl id
	 * @param jid job id
	 */
	Map<String, Object> job(String cid, String jid);

	/**
	 * @param machineId 机器号
	 */
	Map<String, Object> job(String machineId, String cid, String jid);
	
}
