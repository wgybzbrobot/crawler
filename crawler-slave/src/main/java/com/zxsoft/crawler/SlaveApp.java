package com.zxsoft.crawler;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.api.impl.RAMConfManager;
import com.zxsoft.crawler.api.impl.RAMJobManager;
import com.zxsoft.crawler.slave.SlavePath;

public class SlaveApp extends Application {
	public static ConfManager confMgr;
	public static JobManager jobMgr;
	public static SlaveServer server;
	public static long started;

	static {
		confMgr = new RAMConfManager();
		jobMgr = new RAMJobManager();
	}

	/**
	 * Creates a root Restlet that will receive all incoming calls.
	 */
	@Override
	public synchronized Restlet createInboundRoot() {
		getTunnelService().setEnabled(true);
		getTunnelService().setExtensionsTunnel(true);
		Router router = new Router(getContext());
		// configs
		router.attach("/", APIInfoResource.class);
		router.attach("/" + AdminResource.PATH, AdminResource.class);
		router.attach("/" + AdminResource.PATH + "/{" + Params.CMD + "}", AdminResource.class);
		router.attach("/" + ConfResource.PATH, ConfResource.class);
		router.attach("/" + ConfResource.PATH + "/{" + Params.CONF_ID + "}", ConfResource.class);
		router.attach("/" + ConfResource.PATH + "/{" + Params.CONF_ID + "}/{" + Params.PROP_NAME
		        + "}", ConfResource.class);
		router.attach("/" + SlavePath.JOB_RESOURCE_PATH, JobResource.class);
		router.attach("/" + SlavePath.JOB_RESOURCE_PATH + "/{" + Params.JOB_ID + "}", JobResource.class);
		router.attach("/" + SlavePath.JOB_RESOURCE_PATH, JobResource.class);
		router.attach("/" + SlavePath.JOB_RESOURCE_PATH + "/{" + Params.JOB_ID + "}/{" + Params.CMD + "}",
		        JobResource.class);
		return router;
	}
}
