package com.zxsoft.crawler;

import java.util.List;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.JobStatus.State;
import com.zxsoft.crawler.slave.SlavePath;

public class SlaveServer {
	private static final Logger LOG = LoggerFactory.getLogger(SlaveServer.class);

	private Component component;
	private SlaveApp app;
	private int port;
	private boolean running;

	public SlaveServer(int port) {
		this.port = port;
		// Create a new Component.
		component = new Component();

		// Add a new HTTP server listening on port 8182.
		component.getServers().add(Protocol.HTTP, port);

		// Attach the application.
		app = new SlaveApp();
		
		component.getDefaultHost().attach("/" + SlavePath.PATH, app);
		
		component.getContext().getParameters().set("maxThreads", "1000");
		
		SlaveApp.server = this;
	}

	public boolean isRunning() {
		return running;
	}

	public void start() throws Exception {
		LOG.info("Starting CrawlerServer on port " + port + "...");
		component.start();
		LOG.info("Started CrawlerServer on port " + port);
		running = true;
		SlaveApp.started = System.currentTimeMillis();
	}

	public boolean canStop() throws Exception {
		List<JobStatus> jobs = SlaveApp.jobMgr.list(null, State.RUNNING);
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
		SlaveServer server = new SlaveServer(port);
		server.start();
		
	}
}
