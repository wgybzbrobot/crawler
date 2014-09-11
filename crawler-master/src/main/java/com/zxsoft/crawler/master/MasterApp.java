package com.zxsoft.crawler.master;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.zxsoft.crawler.master.impl.RAMSlaveManager;

public class MasterApp extends Application {

	public static MasterServer server;
	public static long started;
	
	public static SlaveManager slaveMgr;
	
	static {
		slaveMgr = new RAMSlaveManager();
	}
	
	@Override
	public synchronized Restlet createInboundRoot() {
		getTunnelService().setEnabled(true);
		getTunnelService().setExtensionsTunnel(true);
		Router router = new Router(getContext());
		
		router.attach("/" + MasterPath.SLAVE_RESOURCE_PATH, SlaveResource.class);
		
		return router;
	}
}
