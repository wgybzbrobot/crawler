package com.zxsoft.crawler.api.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.api.CrawlTool;
import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.parse.NetworkInspectParserController;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.ParserNotFoundException;
import com.zxsoft.crawler.storage.WebPage;

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
                String url = (String) args.get(Params.URL);
                String comment = (String) args.get(Params.COMMENT);
                long prevFetchTime = 0;
                try {
                        prevFetchTime = (long) args.get(Params.PREV_FETCH_TIME);
                } catch (NullPointerException e) {
                        // 60 days ago
                        prevFetchTime = System.currentTimeMillis() - 60 * 24 * 60 * 60 * 1000L;
                        LOG.warn(url + "任务没有上次抓取时间, 将使用程序设定:" + prevFetchTime);
                } catch (Exception  e) {
                        LOG.error(e.getMessage());
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("url", url);
                map.put("comment", comment);
                map.put("starttime", new Date().getTime());
                try {
                        NetworkInspectParserController parseUtil = new NetworkInspectParserController();
                        WebPage page = new WebPage(url, prevFetchTime);
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
