package com.zxsoft.crawler.master;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.api.Prey;

public class SlaveResource extends ServerResource {

        private static Logger LOG = LoggerFactory.getLogger(SlaveResource.class);
        
	@Get("json")
	public Object retrieve() throws Exception {
		
		List<SlaveStatus> list = MasterApp.slaveMgr.list(); 
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("slavestatus", list);
		
		return map;

	}

	@Put("json")
	public Object create(Prey prey) throws Exception {
	        LOG.info("Create Job: " + prey.toString());
		String res = MasterApp.slaveMgr.create(prey); 
		return res;
	}

}
