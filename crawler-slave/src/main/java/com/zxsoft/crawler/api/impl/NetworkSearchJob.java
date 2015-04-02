package com.zxsoft.crawler.api.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.api.CrawlTool;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.parse.NetworkSearchParserController;

/**
 * 全网搜索
 */
public class NetworkSearchJob extends CrawlTool {

    private static final long serialVersionUID = -79033832898281550L;
    private static Logger LOG = LoggerFactory.getLogger(NetworkSearchJob.class);

    public NetworkSearchJob() {
    }

    @Override
    public Map<String, Object> run(JobConf jobConf) throws Exception {

        NetworkSearchParserController parserController = new NetworkSearchParserController();

       parserController.parse(jobConf);

        return null;
    }

}
