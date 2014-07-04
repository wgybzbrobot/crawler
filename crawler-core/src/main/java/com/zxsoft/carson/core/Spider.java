package com.zxsoft.carson.core;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.storage.Seed;
import com.zxsoft.crawler.storage.WebPageMy;
import com.zxsoft.crawler.tools.Tool;
import com.zxsoft.crawler.tools.Tools;
import com.zxsoft.crawler.util.parse.ParseUtil;
import com.zxsoft.crawler.util.parse.ParserNotFoundException;

public class Spider implements Runnable, Tool, Configurable {

	private static Logger LOG = LoggerFactory.getLogger(Spider.class);

	private Tools tools;
	private Configuration conf;
	private Seed seed;

	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	public void setSeed(Seed seed) {
		this.seed = seed;
	}

	public Spider() {
	}

	public Spider(Tools tools) {
		this.tools = tools;
	}

	public void run() {
		if (seed == null) {
			return;
		}
		Downloader downloader = new Downloader(tools);
		WebPageMy page = null;
		try {
	        page = downloader.connect(seed);
        } catch (MalformedURLException e1) {
	        e1.printStackTrace();
	        return;
        } catch (IOException e1) {
        	tools.getInfoService().addSeed(seed);
	        e1.printStackTrace();
	        return;
        }

		if (page == null || page.getDocument() == null)
			return;
		ParseUtil parseUtil = new ParseUtil();
		parseUtil.setTools(tools);
		parseUtil.setConf(conf);
		try {
			parseUtil.parse(page);
		} catch (ParserNotFoundException e) {
			LOG.error("Parser Not Found " + seed.getUrl(), e);
			return;
		}
	}

	public Tools getTools() {
		return tools;
	}

	public void setTools(Tools tools) {
		this.tools = tools;
	}

	public Configuration getConf() {
		return this.conf;
	}
}
