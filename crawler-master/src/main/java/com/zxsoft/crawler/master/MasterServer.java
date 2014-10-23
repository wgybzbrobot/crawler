package com.zxsoft.crawler.master;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.CollectionUtils;

import redis.clients.jedis.Jedis;

import com.google.gson.Gson;
import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.master.impl.RAMSlaveManager;
import com.zxsoft.crawler.master.impl.RedisPreyFrontier;
import com.zxsoft.crawler.util.CrawlerConfiguration;

/**
 *
 */
public class MasterServer {
	private static final Logger LOG = LoggerFactory.getLogger(MasterServer.class);

	private Component component;
	private MasterApp app;
	private int port;
	private boolean running;

	public MasterServer(int port) {
		this.port = port;
		// Create a new Component.
		component = new Component();

		// Add a new HTTP server listening on port 8182.
		component.getServers().add(Protocol.HTTP, port);

		// Attach the application.
		app = new MasterApp();

		component.getDefaultHost().attach("/master", app);
		
		component.getContext().getParameters().add("maxThreads", "1000");
		
		MasterApp.server = this;

	}

	public boolean isRunning() {
		return running;
	}
	private static final String URLBASE = "urlbase";
	
	public void start() throws Exception {
		LOG.info("Starting CrawlerServer on port " + port + "...");
		component.start();
		LOG.info("Started CrawlerServer on port " + port);
		running = true;
		MasterApp.started = System.currentTimeMillis();
		
		SlaveManager slaveManager = new RAMSlaveManager();
		slaveManager.list();
		
		
		Configuration conf = CrawlerConfiguration.create();
		long heartbeat = conf.getLong("heartbeat", 1 * 60 * 1000); // default is 3 min
		new HeartBeatThread(heartbeat).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				Jedis jedis = new Jedis("localhost", 6379);
				while (true) {
					Set<String> strs = jedis.zrevrange(URLBASE, 0, 0);
					if (CollectionUtils.isEmpty(strs)) {
						LOG.warn("No records in redis urlbase.");
						 try {
	                        Thread.sleep(2000);
                        } catch (InterruptedException e) {
	                        e.printStackTrace();
                        }
						 continue;
					}
					String json = strs.toArray(new String[0])[0];
					Prey prey = new Gson().fromJson(json, Prey.class);
					LOG.info("pop prey: " + json);
					long interval = System.currentTimeMillis() - prey.getPrevFetchTime();
					long realInterval = prey.getFetchinterval() * 60 * 1000;
					if (interval >= realInterval) {
						Long count = jedis.zrem(URLBASE, prey.toString());
						LOG.info("remove count: " + count);
						// 将上次抓取时间设置为当前时间，供下次抓取使用
						prey.setPrevFetchTime(System.currentTimeMillis());
						double score = 1.0d / (System.currentTimeMillis() + prey.getFetchinterval());
						jedis.zadd(URLBASE, score, prey.toString());
						LOG.info("push prey: " + prey.toString() + ", score:" + score);
					} else {
						long wait = realInterval - interval;
						LOG.info("Sleep " + wait + " milliseconds");
	                    try {
	                        Thread.sleep(wait);
                        } catch (InterruptedException e) {
	                        e.printStackTrace();
                        }
	                    continue;
					}
					
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(Params.JOB_TYPE, prey.getJobType());
					Map<String, Object> args = new HashMap<String, Object>();
					args.put(Params.URL, prey.getUrl());
					args.put(Params.PROXY_TYPE, prey.getProxyType());
					args.put(Params.PREV_FETCH_TIME, prey.getPrevFetchTime());
					map.put(Params.ARGS, args);

					SlaveManager slaveManager = new RAMSlaveManager();
					try {
		                slaveManager.create(map);
	                } catch (Exception e) {
	                	LOG.warn(e.getMessage());
		                e.printStackTrace();
	                }
				}
			}
		}).start();
		
	}

	private class HeartBeatThread extends Thread {
		private long heartbeat;
		public HeartBeatThread(long heartbeat) {
			this.heartbeat = heartbeat;
        }
		@Override
		public void run() {
			while (true) {
				try {
					LOG.info("HeartBeatThread sleep millisecond: " + heartbeat);
		            TimeUnit.MILLISECONDS.sleep(heartbeat);
	            } catch (InterruptedException e) {
		            e.printStackTrace();
	            }
				
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.err.println("Usage: CrawlerServer <port>");
			System.exit(-1);
		}

		int port = Integer.parseInt(args[0]);
		MasterServer server = new MasterServer(port);
		server.start();
	}

}
