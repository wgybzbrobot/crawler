package com.zxsoft.crawler.master;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.CollectionUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.google.gson.Gson;
import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.master.impl.RAMSlaveManager;
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
		final long heartbeat = conf.getLong("heartbeat", 3 * 60 * 1000); // default is 3 min
		final String redisUrl = conf.get("redis.host");
		final int redisPort = conf.getInt("redis.port", 6379);
		
		// 监测slave
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
//						SlaveManager slaveManager = new RAMSlaveManager();
//						try {
//	                        slaveManager.list();
//                        } catch (Exception e) {
//	                        e.printStackTrace();
//                        }
						LOG.info("SlaveMonitorThread sleep " + heartbeat / 60000 + " minutes");
			            TimeUnit.MILLISECONDS.sleep(heartbeat);
		            } catch (InterruptedException e) {
			            e.printStackTrace();
		            }
				}
			}
		}, "SlaveMonitorThread").start();
		
		// 任务队列管理
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Jedis jedis = new Jedis(redisUrl, redisPort);
					Set<String> strs = null;
					try {
						strs = jedis.zrevrange(URLBASE, 0, 0);
					} catch (JedisConnectionException e) {
						LOG.error(e.getMessage());
						continue;
					}
					if (CollectionUtils.isEmpty(strs)) {
						LOG.warn("No records in redis urlbase.");
						 try {
	                        Thread.sleep(30000);
                        } catch (InterruptedException e) {
                        	LOG.error(e.getMessage());
	                        e.printStackTrace();
                        }
						 continue;
					}
					String json = strs.toArray(new String[0])[0];
					Prey prey = new Gson().fromJson(json, Prey.class);
					long interval = System.currentTimeMillis() - prey.getPrevFetchTime();
					long realInterval = prey.getFetchinterval() * 60 * 1000L;
					long prevFetchTime = prey.getPrevFetchTime();
					if (interval >= realInterval) {
						jedis.zrem(URLBASE, prey.toString());
						// 将上次抓取时间设置为当前时间，供下次抓取使用
						prey.setPrevFetchTime(System.currentTimeMillis());
						double score = 1.0d / (System.currentTimeMillis() / 60000.0d + prey.getFetchinterval() * 1.0d);
						jedis.zadd(URLBASE, score, prey.toString());
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
					LOG.info("分发任务: " + prey.toString());
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(Params.JOB_TYPE, prey.getJobType());
					Map<String, Object> args = new HashMap<String, Object>();
					args.put(Params.URL, prey.getUrl());
					args.put(Params.PROXY_TYPE, prey.getProxyType());
					args.put(Params.PREV_FETCH_TIME, prevFetchTime);
					args.put(Params.COMMENT, prey.getComment());
					map.put(Params.ARGS, args);

					SlaveManager slaveManager = new RAMSlaveManager();
					try {
		                slaveManager.create(map);
	                } catch (Exception e) {
	                	LOG.warn(e.getMessage());
		                e.printStackTrace();
	                }
					jedis.close();
				}
			}
		}, "TaskSchedulerThread").start();
		
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
