package com.zxsoft.crawler.core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.CrawlerServer;
import com.zxsoft.crawler.urlbase.UrlbaseFactory;
import com.zxsoft.crawler.util.CrawlerConfiguration;

public class Crawler extends Thread {

	private static Logger LOG = LoggerFactory.getLogger(Crawler.class);

	private UrlbaseFactory urlbaseFactory;
	
	public Crawler (UrlbaseFactory urlbaseFactory) {
		this.urlbaseFactory = urlbaseFactory;
	}
	
	@Override
	public void run() {
		System.out.println("Crawler is started.");
		
		CrawlerServer.startTime = System.currentTimeMillis();
		
		org.apache.hadoop.conf.Configuration conf = CrawlerConfiguration.create();
		ThreadPoolExecutor executor = newFixedThreadPool(conf.getInt("spider.thread.num", 10));
		while (!executor.isShutdown()) {
			
			String url = urlbaseFactory.peek();
			if (url == null) {
				try {
	                TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
	                e.printStackTrace();
                }
				continue;
			}
			Spider spider = new Spider(url);
			spider.setConf(conf);
			executor.execute(spider);
	
		}
		
		LOG.info("Crawler exit.");
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
