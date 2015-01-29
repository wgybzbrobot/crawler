package com.zxsoft.crawler.master;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.zxsoft.crawler.api.Prey;

public class SlaveResource extends ServerResource {

	@Get("json")
	public Object retrieve() throws Exception {
		
		List<SlaveStatus> list = MasterApp.slaveMgr.list(); 
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("slavestatus", list);
		
		return map;

	}

	@Put("json")
	public Object create(Prey prey) throws Exception {
		String res = MasterApp.slaveMgr.create(prey); 
		return res;
	}

}
