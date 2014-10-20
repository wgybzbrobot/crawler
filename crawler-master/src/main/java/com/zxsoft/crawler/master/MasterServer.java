package com.zxsoft.crawler.master;

import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.util.CrawlerConfiguration;

/**
 *
 */
public class MasterServer {
	private static final Logger LOG = LoggerFactory.getLogger(MasterServer.class);

	private Component component;
	private MasterApp app;
	private int port;
	private boolean running;

	public MasterServer(int port) {
		this.port = port;
		// Create a new Component.
		component = new Component();

		// Add a new HTTP server listening on port 8182.
		component.getServers().add(Protocol.HTTP, port);

		// Attach the application.
		app = new MasterApp();

		component.getDefaultHost().attach("/master", app);
		
		component.getContext().getParameters().add("maxThreads", "1000");
		
		MasterApp.server = this;

	}

	public boolean isRunning() {
		return running;
	}

	public void start() throws Exception {
		LOG.info("Starting CrawlerServer on port " + port + "...");
		component.start();
		LOG.info("Started CrawlerServer on port " + port);
		running = true;
		MasterApp.started = System.currentTimeMillis();
		
		Configuration conf = CrawlerConfiguration.create();
		long heartbeat = conf.getLong("heartbeat", 3 * 60 * 1000); // default is 3 min
		new HeartBeatThread(heartbeat).start();
	}

	class HeartBeatThread extends Thread {
		private long heartbeat;
		public HeartBeatThread(long heartbeat) {
			this.heartbeat = heartbeat;
        }
		@Override
		public void run() {
			while (true) {
				try {
		            TimeUnit.MILLISECONDS.sleep(heartbeat);
	            } catch (InterruptedException e) {
		            e.printStackTrace();
	            }
				
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.err.println("Usage: CrawlerServer <port>");
			System.exit(-1);
		}

		int port = Integer.parseInt(args[0]);
		MasterServer server = new MasterServer(port);
		server.start();
	}

}
