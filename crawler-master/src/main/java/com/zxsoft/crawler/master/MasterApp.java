package com.zxsoft.crawler.master;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.zxsoft.crawler.master.impl.RAMSlaveManager;
import com.zxsoft.crawler.urlbase.JobManager;

public class MasterApp extends Application {

	public static MasterServer server;
	public static long started;
	
	public static SlaveManager slaveMgr;
	
	public MasterApp(JobManager jobManager) {
	    slaveMgr = new RAMSlaveManager(jobManager);
    }
	
	@Override
	public synchronized Restlet createInboundRoot() {
		getTunnelService().setEnabled(true);
		getTunnelService().setExtensionsTunnel(true);
		Router router = new Router(getContext());
		
		router.attach("/" + MasterPath.JOB_RESOURCE_PATH, JobResource.class);
		router.attach("/" + MasterPath.WORKER_RESOURCE_PATH, WorkerResource.class);
		
		return router;
	}
}
