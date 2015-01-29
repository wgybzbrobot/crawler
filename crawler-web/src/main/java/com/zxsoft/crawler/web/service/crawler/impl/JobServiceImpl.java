package com.zxsoft.crawler.web.service.crawler.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.api.Machine;
import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.api.Prey;
import com.zxsoft.crawler.master.MasterPath;
import com.zxsoft.crawler.web.service.crawler.JobService;

/**
 * 创建<b>网络巡检任务</b>和<b>全网搜索任务</b>
 */
public class JobServiceImpl extends SimpleCrawlerServiceImpl implements JobService {

        private static Logger LOG = LoggerFactory.getLogger(JobServiceImpl.class);
        
	@Override
	public Map<String, Object> addInsecptJob(Map<String, Object> args) {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put(Params.JOB_TYPE, JobType.NETWORK_INSPECT);
		map.put(Params.ARGS, args);
		try {
	        Thread t = new Thread() {
	        	@Override
	        	public void run() {
	        		ClientResource cli = new ClientResource(CRAWLER_MASTER + MasterPath.SLAVE_RESOURCE_PATH);
	        		try {
	        		        Representation r = cli.put(map);
	        		        LOG.info(r.getText());
	        		} catch (Exception e) {
	        		        e.printStackTrace();
	        		} finally {
	        		        cli.release();
	        		}
	        	};
	        };
	        t.start();
	        t.join(10000);
        } catch (InterruptedException e) {
	        e.printStackTrace();
        }
		return null;
	}

	@Override
	public Map<String, Object> addSearchJob(final Prey prey) {
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("code", 20000);
	        map.put("msg", "create search job success");
	        map.put("uri", prey.getEngineUrl());
//		try {
//			Thread t = new Thread() {
//				@Override
//				public void run() {
					ClientResource cli = new ClientResource(CRAWLER_MASTER + MasterPath.SLAVE_RESOURCE_PATH);
					try {
					        cli.put(prey);
					} catch (Exception e) {
					        map.put("code", 21001);
                                                map.put("msg", "create search job failed, " + e.getMessage());
                                        }finally {
					        cli.release();
					}
//				}
//			};
//			t.start();
//			t.join(10000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//                        e.printStackTrace();
//                }
		return map;
	}

	/**
	 * 获取所有Job的状态
	 */
	@Override
	public List<Map<String, Object>> jobs() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		ClientResource cli = new ClientResource(CRAWLER_MASTER);
		Representation r = cli.get();
		String json = "";
		try {
			json = r.getText();
		} catch (IOException e) {
			json = "[code:50001, msg:'" + e.getMessage() + "']";
		}
		cli.release();
		return list;
	}

	/**
	 * @param cid
	 *            crawl id
	 * @param jid
	 *            job id
	 */
	@Override
	public Map<String, Object> job(String cid, String jid) {
		String json = "";
		Machine target = new Machine();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> job = new Gson().fromJson(json, Map.class);
		map.put("machine", target);
		map.put("job", job);
		return map;
	}

	@Override
	public Map<String, Object> job(String machineId, String cid, String jid) {
		String json = "";
		Machine target = new Machine();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> job = new Gson().fromJson(json, Map.class);
		map.put("machine", target);
		map.put("job", job);
		return map;
	}

}
