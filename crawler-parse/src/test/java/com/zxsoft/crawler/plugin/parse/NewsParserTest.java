package com.zxsoft.crawler.plugin.parse;

import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.util.CrawlerConfiguration;

public class NewsParserTest {

	private HttpFetcher httpFetcher;

	private Configuration conf;

	public void setUp() {
		conf = CrawlerConfiguration.create();
	}

	public void test() throws Exception {
		
	}
}
