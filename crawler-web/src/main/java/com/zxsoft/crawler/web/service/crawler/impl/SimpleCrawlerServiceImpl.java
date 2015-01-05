package com.zxsoft.crawler.web.service.crawler.impl;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.io.ClassPathResource;
import com.zxisl.commons.utils.StringUtils;

public abstract class SimpleCrawlerServiceImpl {

        private static Logger LOG = LoggerFactory.getLogger(SimpleCrawlerServiceImpl.class);

        protected static String CRAWLER_MASTER;

        static {
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
