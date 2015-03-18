package com.zxsoft.crawler.api.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.Assert;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.api.CrawlTool;
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.dns.DNSCache;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.LocationUtils;
import com.zxsoft.crawler.parse.NetworkSearchParserController;
import com.zxsoft.crawler.parse.ParserNotFoundException;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.URLFormatter;

/**
 * 全网搜索
 */
public class NetworkSearchJob extends CrawlTool {

        private static final long serialVersionUID = -79033832898281550L;
        private static Logger LOG = LoggerFactory.getLogger(NetworkSearchJob.class);

        public NetworkSearchJob() {
        }

        @Override
        public Map<String, Object> run(Map<String, Object> args) throws Exception {

                String keyword = (String) args.get(Params.KEYWORD);
                Assert.notNull(keyword);
                String engineUrl = (String) args.get(Params.ENGINE_URL);
                String url =  (String) args.get(Params.URL);
                
                String encode = args.get("encode") == null ? "" : (String) args.get("encode");
                
                Map<String, Object> map = new HashMap<String, Object>();
                
//                if (StringUtils.isEmpty(url)) {
//                        LOG.error("error, no url:" + engineUrl);
//                        map.put("code", 5004);
//                        map.put("message", "search url not set by engineUrl and keyword.");
//                        return map;
//                }
                
                /*
                 * 传入参数
                 */
                int region = (Integer) args.get(Params.COUNTRY_CODE);
                int provinceId = (Integer) args.get(Params.PROVINCE_CODE);
                int cityId = (Integer) args.get(Params.CITY_CODE);
                int source_id = (Integer) args.get(Params.SOURCE_ID);
                String source_name = (String)args.get(Params.SOURCE_NAME);
                int server_id = (Integer) args.get(Params.SERVER_ID);
                int source_type = JobType.NETWORK_SEARCH.getValue();
                int sectionId = (Integer) args.get(Params.SECTION_ID);
                String comment = (String) args.get(Params.COMMENT);
                int locationCode = 0;
                int platform = (Integer)args.get("platform");
            
                String _url = engineUrl;
                url = engineUrl;
//                if (url.contains("%t")) 
                        _url = URLFormatter.format(_url, keyword);
//                else 
//                        _url = URLFormatter.format(_url);
                
                String   ip = DNSCache.getIp( new URL(_url));
                String location = LocationUtils.getLocation(ip);
              locationCode = LocationUtils.getLocationCode(ip);
              
              WebPage page = new WebPage(url, engineUrl, keyword, platform, sectionId, comment, region, provinceId, cityId, locationCode, location, ip,
                                              JobType.NETWORK_SEARCH, source_id,source_name, server_id, source_type);
              page.setEncode(encode);
//                WebPage page = new WebPage(keyword, engineUrl);
                page.setBaseUrl(url);
                
                NetworkSearchParserController parserController = new NetworkSearchParserController();
             
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
                } catch (IllegalArgumentException e) {
                        LOG.error("Argument error, may be parse rule not configured." + url, e);
                        map.put("code", 5003);
                        map.put("message", "argument error, may be parse rule not configured" + e.getMessage());
                } catch (MalformedURLException e) {
                        LOG.error("Not valid url:" + url, e);
                        map.put("code", 5004);
                        map.put("message", "not valid url," + url + e.getMessage());
                }

                LOG.debug((String) map.get("message"));
                return map;
        }

}
