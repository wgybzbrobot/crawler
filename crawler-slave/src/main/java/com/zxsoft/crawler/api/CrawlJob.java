package com.zxsoft.crawler.api;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;

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
