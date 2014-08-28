package com.zxsoft.crawler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.JobStatus.State;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.storage.WebPage.JOB_TYPE;
import com.zxsoft.crawler.urlbase.UrlbaseFactory;
import com.zxsoft.crawler.urlbase.impl.RedisUrlbaseFactory;

public class CrawlerServer {
	private static final Logger LOG = LoggerFactory.getLogger(CrawlerServer.class);

	private Component component;
	private CrawlerApp app;
	private int port;
	private boolean running;

	public CrawlerServer(int port) {
		this.port = port;
		// Create a new Component.
		component = new Component();

		// Add a new HTTP server listening on port 8182.
		component.getServers().add(Protocol.HTTP, port);

		// Attach the application.
		app = new CrawlerApp();
		component.getDefaultHost().attach("/crawler", app);
		CrawlerApp.server = this;
	}

	public boolean isRunning() {
		return running;
	}

	public void start() throws Exception {
		LOG.info("Starting NutchServer on port " + port + "...");
		component.start();
		LOG.info("Started NutchServer on port " + port);
		running = true;
		CrawlerApp.started = System.currentTimeMillis();
	}

	public boolean canStop() throws Exception {
		List<JobStatus> jobs = CrawlerApp.jobMgr.list(null, State.RUNNING);
		if (!jobs.isEmpty()) {
			return false;
		}
		return true;
	}

	public boolean stop(boolean force) throws Exception {
		if (!running) {
			return true;
		}
		if (!canStop() && !force) {
			LOG.warn("Running jobs - can't stop now.");
			return false;
		}
		LOG.info("Stopping NutchServer on port " + port + "...");
		component.stop();
		LOG.info("Stopped NutchServer on port " + port);
		running = false;
		return true;
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.err.println("Usage: CrawlerServer <port>");
			System.exit(-1);
		}
		int port = Integer.parseInt(args[0]);
		CrawlerServer server = new CrawlerServer(port);
		server.start();
		
		
		/*while (true) {
			UrlbaseFactory urlFactory = new RedisUrlbaseFactory();
			WebPage page = urlFactory.poll();
			if (page == null || page.getBaseUrl() == null) {
				TimeUnit.SECONDS.sleep(30);
				continue;
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(Params.CRAWL_ID, "crawl");
			map.put(Params.JOB_TYPE, JOB_TYPE.NETWORK_INSPECT.toString());
			Map<String, Object> moreArgs = new HashMap<String, Object>();
			moreArgs.put(Params.URL, page.getBaseUrl());
			moreArgs.put(Params.URL_TYPE, page.getType());
			moreArgs.put(Params.PrevFetchTime, page.getPrevFetchTime());
			
			map.put(Params.ARGS, moreArgs);
			JobResource jobResource = new JobResource();
			jobResource.create(map);
			
		}*/
		
	}
}
