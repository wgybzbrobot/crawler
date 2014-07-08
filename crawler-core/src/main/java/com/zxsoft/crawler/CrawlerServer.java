package com.zxsoft.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import com.zxsoft.crawler.core.Crawler;
import com.zxsoft.crawler.dao.ConfDao;

@Configuration
@RestController
@EnableAutoConfiguration
@ComponentScan(basePackages={"com.zxsoft.crawler"})
public class CrawlerServer {

	public static void main(String[] args) {
//		SpringApplication.run(CrawlerServer.class, args);
		SpringApplication app = new SpringApplication(CrawlerServer.class); 
        app.setShowBanner(false); 
        app.run(args);
        
    }

}
