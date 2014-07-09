package com.zxsoft.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import com.zxsoft.crawler.core.Crawler;

@Configuration
@RestController
@EnableAutoConfiguration
//@EnableGemfireRepositories
@EnableCaching
@ComponentScan(basePackages = { "com.zxsoft.crawler" })
public class CrawlerServer {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CrawlerServer.class);
		app.setShowBanner(false);
		ApplicationContext ctx = app.run(args);
		Crawler crawler = ctx.getBean(Crawler.class);
		crawler.start();
	}

	@Bean
	CacheFactoryBean cacheFactoryBean() {
		return new CacheFactoryBean();
	}

	@Bean
	LocalRegionFactoryBean<Integer, Integer> localRegionFactoryBean(final Cache cache) {
		return new LocalRegionFactoryBean<Integer, Integer>() {
			{
				setCache(cache);
				setName("hello");
			}
		};
	}

	@Bean
	GemfireCacheManager cacheManager(final Cache gemfireCache) {
		return new GemfireCacheManager() {
			{
				setCache(gemfireCache);
			}
		};
	}

}
