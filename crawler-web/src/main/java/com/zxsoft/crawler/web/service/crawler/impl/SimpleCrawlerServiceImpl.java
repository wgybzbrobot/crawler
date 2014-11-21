package com.zxsoft.crawler.web.service.crawler.impl;

import java.io.IOException;
import java.util.Properties;

//import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.io.ClassPathResource;
import org.thinkingcloud.framework.util.StringUtils;

public abstract class SimpleCrawlerServiceImpl {

	private static Logger LOG  = LoggerFactory.getLogger(SimpleCrawlerServiceImpl.class);
	
	protected static String CRAWLER_MASTER;

	static {
//		Configuration conf = new Configuration();
//		conf.addResource("master.xml");
		/*try {
			conf.addResource(resource.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
//		ClassPathResource resource = new ClassPathResource("master.xml");

		ClassPathResource resource = new ClassPathResource("master.properties");
		Properties properties = new Properties();
		try {
	        properties.load(resource.getInputStream());
        } catch (IOException e) {
	        e.printStackTrace();
        }
		CRAWLER_MASTER = properties.getProperty("crawler.master.url");

		if (StringUtils.isEmpty(CRAWLER_MASTER)) {
			throw new NullPointerException("爬虫主控URL地址未找到, master.properties中是否配置了<crawler.master.url>");
		}

		if (CRAWLER_MASTER.endsWith("/")) {
			CRAWLER_MASTER = CRAWLER_MASTER + "master/";
		} else {
			CRAWLER_MASTER = CRAWLER_MASTER + "/master/";
		}
		
		LOG.info("Crawler master url:" + CRAWLER_MASTER);
	}
}
