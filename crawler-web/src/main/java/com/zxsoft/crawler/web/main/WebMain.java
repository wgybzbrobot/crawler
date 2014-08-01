package com.zxsoft.crawler.web.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@RestController
@EnableAutoConfiguration
@EnableCaching
@ComponentScan(basePackages = { "com.zxsoft.crawler" })
public class WebMain {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(WebMain.class);
		app.setShowBanner(false);
		app.run(args);
	}
}
