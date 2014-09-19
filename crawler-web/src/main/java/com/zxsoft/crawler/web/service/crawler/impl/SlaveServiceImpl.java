package com.zxsoft.crawler.web.service.crawler.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import org.apache.hadoop.conf.Configuration;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.google.gson.Gson;
import com.zxsoft.crawler.master.MasterPath;
import com.zxsoft.crawler.web.service.crawler.SlaveService;

public class SlaveServiceImpl extends SimpleCrawlerServiceImpl implements SlaveService {

	public SlaveServiceImpl() {
		super();
    }
	
	@Override
	public List<Map<String, Object>> slaves() throws IOException {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		
		ClientResource cli = new ClientResource(CRAWLER_MASTER + MasterPath.SLAVE_RESOURCE_PATH);

		String json = "";
		Representation r = cli.get();
		try {
			json = r.getText();
			Map<String, Object> map = new Gson().fromJson(json, Map.class);
			result = (List<Map<String, Object>>) map.get("slavestatus");
		} catch (IOException e) {
			e.printStackTrace();
//			r.release();
			cli.release();
			throw new IOException(e);
		} finally {
//			r.release();
			cli.release();
		}
		
		return result;

	}

}
