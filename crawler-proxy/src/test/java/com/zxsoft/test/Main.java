package com.zxsoft.test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableAutoConfiguration
@EnableCaching
@ComponentScan(basePackages = { "com.zxsoft" })
public class Main {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Main.class);
		app.setShowBanner(false);
		app.run(args);
	}

}
