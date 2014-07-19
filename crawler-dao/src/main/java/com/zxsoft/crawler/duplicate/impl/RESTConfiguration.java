package com.zxsoft.crawler.duplicate.impl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class RESTConfiguration {

	@Bean
	public RestServer restServer(Environment env) {
		int port = 80;
		if (env.getProperty("rest.port") == null) {
			port = Integer.valueOf(env.getProperty("rest.port"));
		}
		return new RestServer(env.getProperty("rest.user"), env.getProperty("rest.password"),
		        env.getProperty("rest.host"), port);
	}
}
