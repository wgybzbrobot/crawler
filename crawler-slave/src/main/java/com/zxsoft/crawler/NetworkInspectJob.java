package com.zxsoft.crawler;

import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.parse.NetworkInspectParserController;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.ParserNotFoundException;
import com.zxsoft.crawler.storage.WebPage;

/**
 * 网络巡检
 */
public class NetworkInspectJob extends CrawlTool {

	private static Logger LOG = LoggerFactory.getLogger(NetworkInspectJob.class);
	
	public NetworkInspectJob () {}
	
	public NetworkInspectJob (Configuration conf) {
		setConf(conf);
	}
	
	@Override
    public Map<String, Object> run(Map<String, Object> args) throws Exception {
		
		String url = (String) args.get(Params.URL);
		String urlType = (String) args.get(Params.URL_TYPE);
		
//		numJobs = 1;
//		currentJob = new CrawlJob(getConf(), "NetworkInsecpt");
		
		Configuration conf = getConf();
		
		long interval = conf.getLong("fetch.interval", 60 * 60 * 1000L);
		
		try {
			interval = Long.valueOf(args.get(Params.Interval).toString());
		} catch (ClassCastException | NullPointerException e) {
		}
		
		Map<String, Object> map = new HashMap<String, Object>(); 
		map.put("url", url);
		try {
			NetworkInspectParserController parseUtil = new NetworkInspectParserController(conf);
			WebPage page = new WebPage(url, urlType, interval);
			FetchStatus status = parseUtil.parse(page);
			map.put("code", 2001);
			map.put("count", status.getCount());
			map.put("message", status.getMessage());
		} catch (NullPointerException e) {
			map.put("code", 5001);
			map.put("message", e.getMessage());
		} catch (ParserNotFoundException e) {
			map.put("code", 5002);
			map.put("message", e.getMessage());
		} 
		
		LOG.info("URL[" + url + "], code[" + map.get("code") + "], count[" + map.get("count") + "], message[" + (String)map.get("message") + "]");
	    return map;
    }
}
