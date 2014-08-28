package com.zxsoft.crawler;

import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.NetworkInspectionParserController;
import com.zxsoft.crawler.parse.NetworkSearchParserController;
import com.zxsoft.crawler.parse.ParserNotFoundException;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.storage.WebPage;

/**
 * 全网搜索
 */
public class NetworkSearchJob extends CrawlTool /*implements CrawlJob*/ {

	private static Logger LOG = LoggerFactory.getLogger(NetworkSearchJob.class);

	public NetworkSearchJob() {}
	
	public NetworkSearchJob(Configuration conf) {
		setConf(conf);
	}
	
	@Override
    public Map<String, Object> run(Map<String, Object> args) throws Exception {
		
		String keyword = (String) args.get(Params.KEYWORD);
		String urlType = (String) args.get(Params.URL_TYPE);
		String engineId = (String) args.get(Params.ENGINE_ID);
		
		WebPage page = new WebPage(keyword, engineId, urlType);
		
		Configuration conf = getConf();
		NetworkSearchParserController parserController = new NetworkSearchParserController(conf);
		Map<String, Object> map = new HashMap<String, Object>(); 
		try {
			FetchStatus status = parserController.parse(page);
			map.put("url", status.getUrl());
			map.put("code", 2001);
			map.put("count", status.getCount());
			map.put("message", status.getMessage());
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
