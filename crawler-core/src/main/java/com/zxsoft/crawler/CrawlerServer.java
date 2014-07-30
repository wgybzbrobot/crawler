package com.zxsoft.crawler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import com.zxsoft.crawler.core.Crawler;

@Configuration
@RestController
@EnableAutoConfiguration
@EnableCaching
@ComponentScan(basePackages = { "com.zxsoft.crawler" })
public class CrawlerServer {

	public static boolean start;
	public static long startTime;
	public static long stopTime;
	
	private static ApplicationContext ctx;
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CrawlerServer.class);
		app.setShowBanner(false);
		ctx = app.run(args);
		
		System.out.println("Starting Crawler ...");
		Crawler crawler = new Crawler(ctx);
		crawler.start();
	}
	
	public static ApplicationContext getCtx() {
		return ctx;
	}

}
