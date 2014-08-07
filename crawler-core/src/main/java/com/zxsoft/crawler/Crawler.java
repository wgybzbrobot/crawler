package com.zxsoft.crawler;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import com.zxsoft.crawler.parse.ParseTool;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.urlbase.UrlbaseFactory;
import com.zxsoft.crawler.util.CrawlerConfiguration;

@Configuration
@RestController
@EnableAutoConfiguration
@EnableCaching
@ComponentScan(basePackages = { "com.zxsoft.crawler", "com.zxsoft.proxy" })
public class Crawler {

	private static Logger LOG = LoggerFactory.getLogger(Crawler.class);
	
	public static boolean isCrawlerRunning;
	public static long crawlerStartTime;
	public static long crawlerStopTime;
	
	private static ApplicationContext ctx;
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Crawler.class);
		app.setShowBanner(false);
		ctx = app.run(args);
		
		LOG.info("Starting Crawler ...");
		
		UrlbaseFactory urlbaseRedisFactory = ctx.getBean(UrlbaseFactory.class);
		Crawler.isCrawlerRunning = true;
		Crawler.crawlerStartTime = System.currentTimeMillis();
		ParseTool.init(ctx);
		org.apache.hadoop.conf.Configuration conf = CrawlerConfiguration.create();
		ThreadPoolExecutor executor = newFixedThreadPool(conf.getInt("spider.thread.num", 10));
		LOG.debug("Crawler is started.");
		while (!executor.isShutdown()) {
			WebPage page = urlbaseRedisFactory.poll();
			if (page == null || page.getBaseUrl() == null) {
				try {
	                TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                	LOG.debug("爬虫被终止运行");
	                e.printStackTrace();
                }
				continue;
			}
			CrawlJob crawlJob = new CrawlJob(ctx, page, conf);
			executor.execute(crawlJob);
		}
		LOG.info("Crawler exit.");
		Crawler.isCrawlerRunning = false;
		Crawler.crawlerStopTime = System.currentTimeMillis();
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
	
	public static ApplicationContext getCtx() {
		return ctx;
	}
	
}
