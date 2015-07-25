package com.zxsoft.crawler.master;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.master.searchjob.OnceNetworkSearchThread;
import com.zxsoft.crawler.master.searchjob.RecurNetworkSearchThread;
import com.zxsoft.crawler.urlbase.JobManager;
import com.zxsoft.crawler.urlbase.TaskSchedulerThread;

/**
 * 主控节点
 */
public class MasterServer {
	private static final Logger LOG = LoggerFactory.getLogger(MasterServer.class);

	private Component component;
	public final MasterApp app;
	private final int port;
	private boolean running;

	// redis fields
	private final String redis_host;
	private final int redis_port;
	private final String redis_passwd;

	private boolean enableSearch;
	private int search_interval;

	private JobManager jobManager;

	public MasterServer(int port, String redis_host, int redis_port, String redis_passwd) {
		this.port = port;
		this.redis_host = redis_host;
		this.redis_port = redis_port;
		this.redis_passwd = redis_passwd;

		// Create a new Component.
		component = new Component();
		// Add a new HTTP server listening on port 8182.
		component.getServers().add(Protocol.HTTP, port);
		// Attach the application.
		jobManager = new JobManager(redis_host, redis_port, redis_passwd);
		app = new MasterApp(jobManager);
		component.getDefaultHost().attach("/master", app);
		component.getContext().getParameters().add("maxThreads", "1000");
		MasterApp.server = this;
	}

	public boolean isRunning() {
		return running;
	}

	// private final long heartbeat = 1 * 60 * 1000L; // default is 1 min

	public void start() throws Exception {
		LOG.info("Starting MasterNode on port " + port + "...");
		component.start();
		LOG.info("Started MasterNode on port " + port);
		running = true;
		MasterApp.started = System.currentTimeMillis();

		// 任务队列管理
		new TaskSchedulerThread(redis_host, redis_port, redis_passwd, jobManager).start();

		if (enableSearch) {
			LOG.info("开启全网搜索功能.");
			int realInterval = 10;
			new OnceNetworkSearchThread().start();
			new RecurNetworkSearchThread(realInterval).start();
		}
	}

	public boolean isEnableSearch() {
		return enableSearch;
	}

	public void setEnableSearch(boolean enableSearch) {
		this.enableSearch = enableSearch;
	}

	public int getSearch_interval() {
		return search_interval;
	}

	public void setSearch_interval(int search_interval) {
		this.search_interval = search_interval;
	}

	public static void main(String[] args) throws Exception {

		Options options = new Options();
		options.addOption("help", false, "Display this help imformation");
		options.addOption("port", true, "Master Server port");
		options.addOption("enableSearch", false, "Enable Search Job");
		options.addOption("search_interval", true, "Interval to get tasks from oracle db");
		options.addOption("redis_host", true, "Redis host for urlbase");
		options.addOption("redis_port", true, "Redis port ");
		options.addOption("redis_passwd", true, "Redis password");

		CommandLineParser parser = new GnuParser();
		try {
			CommandLine line = parser.parse(options, args);
			//			if (line.hasOption("help")) {
			//				HelpFormatter formatter = new HelpFormatter();
			//				formatter.printHelp("com.zxsoft.crawler.master.MasterServer", options);
			//				System.exit(0);
			//			}
			int port = Integer.valueOf(line.getOptionValue("port", "9999"));
			String redis_host = line.getOptionValue("redis_host", "192.168.32.72");
			String redis_passwd = line.getOptionValue("redis_passwd", null);
			int redis_port = Integer.valueOf(line.getOptionValue("redis_port", "6379"));

			MasterServer server = new MasterServer(port, redis_host, redis_port, redis_passwd);

			//			if (line.hasOption("enableSearch")) {
			server.setEnableSearch(true);
			int search_interval = Integer.valueOf(line.getOptionValue("search_interval", "10"));
			server.setSearch_interval(search_interval);
			//			}

			server.start();
		} catch (Exception exp) {
			LOG.error(exp.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("com.zxsoft.crawler.master.MasterServer", options);
		}

	}

	public boolean stop(boolean b) throws Exception {
		if (!running) {
			return true;
		}
		// if (!canStop() && !force) {
		// LOG.warn("Running jobs - can't stop now.");
		// return false;
		// }
		LOG.info("Stopping NutchServer on port " + port + "...");
		component.stop();
		LOG.info("Stopped NutchServer on port " + port);
		running = false;
		return true;
	}

}
