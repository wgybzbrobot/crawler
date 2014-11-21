package com.zxsoft.crawler.plugin.parse;

import org.apache.hadoop.conf.Configuration;
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
