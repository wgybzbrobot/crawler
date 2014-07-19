package com.zxsoft.crawler.core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.zxsoft.crawler.CrawlerServer;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.urlbase.UrlbaseFactory;
import com.zxsoft.crawler.util.CrawlerConfiguration;

public class Crawler extends Thread {

	private static Logger LOG = LoggerFactory.getLogger(Crawler.class);

	private ApplicationContext ctx;
	
	public Crawler(ApplicationContext ctx) {
		this.ctx = ctx;
    }
	
	private static ThreadPoolExecutor executor;
	
	@Override
	public void run() {
		UrlbaseFactory urlbaseRedisFactory = ctx.getBean(UrlbaseFactory.class);
		
		System.out.println("Crawler is started.");
		
		CrawlerServer.start = true;
		CrawlerServer.startTime = System.currentTimeMillis();
		
		org.apache.hadoop.conf.Configuration conf = CrawlerConfiguration.create();
		executor = newFixedThreadPool(conf.getInt("spider.thread.num", 10));
		while (!executor.isShutdown()) {
			
			WebPage page = urlbaseRedisFactory.peek();
			String url = page.getBaseUrl();
			if (url == null) {
				try {
	                TimeUnit.SECONDS.sleep(160);
                } catch (InterruptedException e) {
	                e.printStackTrace();
                }
				continue;
			}
			CrawlJob crawlJob = new CrawlJob(ctx, page, conf);
			executor.execute(crawlJob);
			
			try {
                TimeUnit.SECONDS.sleep(160);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
