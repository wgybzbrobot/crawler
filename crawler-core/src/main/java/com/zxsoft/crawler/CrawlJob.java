package com.zxsoft.crawler;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.zxsoft.crawler.parse.NetworkInspectionParserController;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.ParserNotFoundException;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.storage.WebPage;

public class CrawlJob extends Job {

	public CrawlJob(Configuration conf) throws IOException {
	    super(conf);
	    setJarByClass(this.getClass());
    }

	public CrawlJob(Configuration conf, String jobName) throws IOException {
		super(conf, jobName);
		setJarByClass(this.getClass());
	}
	
}
