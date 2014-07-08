package com.zxsoft.crawler.core;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import com.zxsoft.crawler.dao.ConfDao;
import com.zxsoft.crawler.storage.Seed;
import com.zxsoft.crawler.urlbase.UrlbaseFactory;
import com.zxsoft.crawler.urlbase.redis.UrlbaseRedisFactory;
import com.zxsoft.crawler.util.CrawlerConfiguration;
import com.zxsoft.crawler.util.parse.Category;

@Component
public class Crawler extends Thread {

	private static Logger LOG = LoggerFactory.getLogger(Crawler.class);

	@Autowired
	private UrlbaseRedisFactory urlbaseFactory;
	
	@Autowired
	private   JdbcTemplate jdbcTemplate;
	
	@Autowired
	private ConfDao confDao;
	
	@Override
	public void run() {
//		SpringApplication.run(Crawler.class, args);
		
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		org.apache.hadoop.conf.Configuration conf = CrawlerConfiguration.create();
		ThreadPoolExecutor executor = newFixedThreadPool(conf.getInt("spider.thread.num", 10));

		confDao.getListConf("http://bbs.anhuinews.com/forum-319-1.html");
//		String url = urlbaseFactory.peek();
//		System.out.println(url);
//		while (!executor.isShutdown()) {
			
//			Seed seed = tools.getInfoService().getSeed();
//			if (seed == null) {
//				try {
//	                TimeUnit.SECONDS.sleep(30);
//                } catch (InterruptedException e) {
//	                e.printStackTrace();
//                }
//				continue;
//			}
//			seed.setLimitDate(seed.getLastfetchtime());
//			if (seed.getType() == Category.LIST_PAGE && seed.getFetchinterval() > 0) {// 列表页
//				seed.setLastfetchtime(new Date());
//				tools.getInfoService().updateSeed(seed);
//			} else {
//				tools.getInfoService().deleteSeed(seed);
//			}
//				
//			Spider spider = new Spider(tools);
//			spider.setSeed(seed);
//			spider.setConf(conf);
//			executor.execute(spider);
//		}

//		context.close();
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
