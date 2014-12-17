package com.zxsoft.crawler.web.service.crawler.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.zxsoft.crawler.api.Machine;
import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.master.MasterPath;
import com.zxsoft.crawler.storage.WebPage.JOB_TYPE;
import com.zxsoft.crawler.web.service.crawler.JobService;

/**
 * 创建<b>网络巡检任务</b>和<b>全网搜索任务</b>
 */
public class JobServiceImpl extends SimpleCrawlerServiceImpl implements JobService {

        private static Logger LOG = LoggerFactory.getLogger(JobServiceImpl.class);
        
	@Override
	public Map<String, Object> addInsecptJob(Map<String, Object> args) {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put(Params.JOB_TYPE, JOB_TYPE.NETWORK_INSPECT);
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
	public Map<String, Object> addSearchJob(Map<String, Object> args) {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put(Params.JOB_TYPE, JOB_TYPE.NETWORK_SEARCH);
		map.put(Params.ARGS, args);
		try {
			Thread t = new Thread() {
				@Override
				public void run() {
					ClientResource cli = new ClientResource(CRAWLER_MASTER + MasterPath.SLAVE_RESOURCE_PATH);
					cli.put(map);
					cli.release();
				}
			};
			t.start();
			t.join(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
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
