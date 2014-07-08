package com.zxsoft.crawler;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import com.zxsoft.crawler.core.Crawler;

@Configuration
@RestController
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.zxsoft.crawler"})
public class CrawlerServer {

	public static void main(String[] args) {
		// SpringApplication.run(CrawlerServer.class, args);
		SpringApplication app = new SpringApplication(CrawlerServer.class);
		app.setShowBanner(false);
		ApplicationContext ctx = app.run(args);
		System.out.println("Let's inspect the beans provided by Spring Boot:");

//		String[] beanNames = ctx.getBeanDefinitionNames();
//		Arrays.sort(beanNames);
//		for (String beanName : beanNames) {
//			System.out.println(beanName);
//		}

		Crawler crawler = ctx.getBean(Crawler.class);
		crawler.start();
	}
        
    }

}
