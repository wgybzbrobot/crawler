package com.zxsoft.crawler;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.LocalRegionFactoryBean;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.support.GemfireCacheManager;
import org.springframework.web.bind.annotation.RestController;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.Region;
import com.zxsoft.crawler.core.Crawler;
import com.zxsoft.crawler.urlbase.UrlbaseFactory;
<<<<<<< HEAD
import com.zxsoft.crawler.urlbase.impl.RedisUrlbaseFactory;
=======
import com.zxsoft.crawler.urlbase.redis.RedisUrlbaseFactory;
>>>>>>> e0da859e27ba5eb6fafd625d352b658a95752736

@Configuration
@RestController
@EnableAutoConfiguration
@EnableGemfireRepositories
@EnableCaching
@ComponentScan(basePackages = { "com.zxsoft.crawler" })
public class CrawlerServer {

	public static boolean start;
	public static long startTime;
	
	private static ApplicationContext ctx;
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CrawlerServer.class);
		app.setShowBanner(false);
		ctx = app.run(args);
		
		ctx = app.run(args);
		System.out.println("Starting Crawler ...");
		Crawler crawler = new Crawler(ctx);
		crawler.start();
	}
	
	public ApplicationContext getCtx() {
		return ctx;
	}

}
