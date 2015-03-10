package com.zxsoft.crawler.api.impl;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.api.CrawlTool;
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.dns.DNSCache;
import com.zxsoft.crawler.parse.LocationUtils;
import com.zxsoft.crawler.parse.NetworkInspectParserController;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.OverseaDao;
import com.zxsoft.crawler.parse.ParserNotFoundException;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.URLFormatter;

/**
 * 网络巡检任务
 */
public class NetworkInspectJob extends CrawlTool {

        private static final long serialVersionUID = -6300768337043076256L;

        private static Logger LOG = LoggerFactory.getLogger(NetworkInspectJob.class);

        public NetworkInspectJob() {
        }

        @Override
        public Map<String, Object> run(Map<String, Object> args) throws Exception {

                /*
                 * 读取并检查参数
                 */
                String url = (String) args.get(Params.URL);
                long prevFetchTime = 0;
                try {
                        prevFetchTime = (long) args.get(Params.PREV_FETCH_TIME);
                } catch (NullPointerException e) {
                        prevFetchTime = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L;
                        LOG.warn("任务没有上次抓取时间, 将使用程序设定:" + prevFetchTime);
                } catch (Exception e) {
                        LOG.error(e.getMessage());
                }

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("url", url);
                map.put("comment", (String) args.get(Params.COMMENT));
                map.put("starttime", new Date().getTime());

                /*
                 * 传入参数
                 */
                int region = (Integer) args.get(Params.COUNTRY_CODE);
                int provinceId = (Integer) args.get(Params.PROVINCE_CODE);
                int cityId = (Integer) args.get(Params.CITY_CODE);
                int source_id = (Integer) args.get(Params.SOURCE_ID);
                int server_id = (Integer) args.get(Params.SERVER_ID);
                int source_type = JobType.NETWORK_INSPECT.getValue();
                int sectionId = (Integer) args.get(Params.SECTION_ID);
                String comment = (String) args.get(Params.COMMENT);
                int locationCode = 0;
                
                /*
                 * 通过ip查询location, location_code
                 */
                String ip = "", location = "";
                try {
                        String _url = url;
                        if (url.contains("%t")) 
                                _url = URLFormatter.format(url);
                        URL u = new URL(_url);
                        ip = DNSCache.getIp(u);
                        location = LocationUtils.getLocation(ip);
                        locationCode = LocationUtils.getLocationCode(ip);
//                        OverseaDao overseaDao = new OverseaDao();
//                        Map <String, Object> _map = overseaDao.getOversea(u.getHost());
//                        if (_map != null) {
//                                ip = (String)_map.get("ip");
//                                location = (String)_map.get("location");
//                                locationCode = (Integer)_map.get("code");
//                        }
                } catch (Exception e) {
                        LOG.warn(e.getMessage(), e);
                }

                try {
                        NetworkInspectParserController parseUtil = new NetworkInspectParserController();
                        WebPage page = new WebPage(url, sectionId, comment, prevFetchTime, region, provinceId, cityId, locationCode, location, ip,
                                                        JobType.NETWORK_INSPECT, source_id, server_id, source_type);
                        FetchStatus status = parseUtil.parse(page);
                        map.put("code", 2001);
                        map.put("count", status.getCount());
                        map.put("message", status.getMessage());
                        map.put("status", status.getStatus());
                } catch (NullPointerException e) {
                        map.put("code", 5001);
                        map.put("message", e.getMessage());
                        LOG.error(e.getMessage());
                        e.printStackTrace();
                } catch (ParserNotFoundException e) {
                        map.put("code", 5002);
                        map.put("message", e.getMessage());
                        LOG.error(e.getMessage());
                        e.printStackTrace();
                } catch (Exception e) {
                        map.put("code", 5005);
                        map.put("message", e.getMessage());
                        LOG.error(e.getMessage());
                        e.printStackTrace();
                }

                return map;
        }
}
