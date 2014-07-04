package com.zxsoft.carson.core;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.storage.Seed;
import com.zxsoft.crawler.tools.Tools;
import com.zxsoft.crawler.util.parse.Category;

public class App {

	private static Logger LOG = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisService redisSerivice = (RedisService) context.getBean("redisService");
		ConfService confService = (ConfService) context.getBean("confService");
		InfoService infoService = (InfoService) context.getBean("infoService");

		Configuration conf = CarsonConfiguration.create();

		Tools tools = new Tools(redisSerivice, confService, infoService);

		ThreadPoolExecutor executor = newFixedThreadPool(conf.getInt("spider.thread.num", 10));

		if (conf.getBoolean("spider.master", false)) {
			try {
				List<ListConf> list = confService.getListConfs();
				Injector injector = new Injector();
				injector.setTools(tools);
				injector.inject(list);
			} catch (RedisConnectionFailureException e) {
//				e.printStackTrace();
				LOG.error("Cannot connect to Redis, Crawler will exit.");
				System.exit(1);
			} catch (CannotGetJdbcConnectionException e) {
//				e.printStackTrace();
				LOG.error("Cannot connect to Mysql, Crawler will exit.");
				System.exit(1);
			}
		} else {
			// test redis and mysql connect status
		}

		while (!executor.isShutdown()) {
			Seed seed = tools.getInfoService().getSeed();
			if (seed == null) {
				try {
	                TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
	                e.printStackTrace();
                }
				continue;
			}
			seed.setLimitDate(seed.getLastfetchtime());
			if (seed.getType() == Category.LIST_PAGE && seed.getFetchinterval() > 0) {// 列表页
				seed.setLastfetchtime(new Date());
				tools.getInfoService().updateSeed(seed);
			} else {
				tools.getInfoService().deleteSeed(seed);
			}
				
			Spider spider = new Spider(tools);
			spider.setSeed(seed);
			spider.setConf(conf);
			executor.execute(spider);
		}

		context.close();
		System.exit(0);
	}

	public static ThreadPoolExecutor newFixedThreadPool(int nThreads) {

		final ThreadPoolExecutor result = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
		        new ArrayBlockingQueue<Runnable>(64), new ThreadPoolExecutor.CallerRunsPolicy());

		result.setThreadFactory(new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					public void uncaughtException(Thread t, Throwable e) {
						LOG.error("Thread exception: " + t.getName(), e);
						result.shutdown();
					}
				});
				return t;
			}
		});

		return result;
	}
}
