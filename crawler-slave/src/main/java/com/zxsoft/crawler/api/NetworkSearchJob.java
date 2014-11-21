package com.zxsoft.crawler.api;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.NetworkSearchParserController;
import com.zxsoft.crawler.parse.ParserNotFoundException;
import com.zxsoft.crawler.storage.WebPage;

/**
 * 全网搜索
 */
public class NetworkSearchJob extends CrawlTool {

    private static final long serialVersionUID = -79033832898281550L;
	private static Logger LOG = LoggerFactory.getLogger(NetworkSearchJob.class);

	public NetworkSearchJob() {}
	
	public NetworkSearchJob(Configuration conf) {
		setConf(conf);
	}
	
	@Override
    public Map<String, Object> run(Map<String, Object> args) throws Exception {
		
		String keyword = (String) args.get(Params.KEYWORD);
		Assert.notNull(keyword);
		String urlType = (String) args.get(Params.PROXY_TYPE);
		String listUrl = (String) args.get(Params.ENGINE_URL);
		
		WebPage page = new WebPage(keyword, listUrl, urlType);
		
		NetworkSearchParserController parserController = new NetworkSearchParserController();
		Map<String, Object> map = new HashMap<String, Object>(); 
		try {
			FetchStatus status = parserController.parse(page);
			map.put("url", status.getUrl());
			map.put("code", 2001);
			map.put("count", status.getCount());
			map.put("message", status.getMessage());
			map.put("description", status.getDescription());
		} catch (NullPointerException e) {
			map.put("code", 5001);
			map.put("message", e.getMessage());
		} catch (ParserNotFoundException e) {
			LOG.error(e.getMessage());
			map.put("code", 5002);
			map.put("message", e.getMessage());
		} 
		
		LOG.debug((String)map.get("message"));
	    return map;
    }

}
