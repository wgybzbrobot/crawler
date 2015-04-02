package com.zxsoft.crawler.api.impl;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.api.CrawlTool;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.parse.NetworkInspectParserController;
import com.zxsoft.crawler.parse.ParserNotFoundException;

/**
 * 网络巡检任务
 */
public class NetworkInspectJob extends CrawlTool {

    private static final long serialVersionUID = -6300768337043076256L;

    private static Logger LOG = LoggerFactory.getLogger(NetworkInspectJob.class);

    public NetworkInspectJob() {
    }

    @Override
    public Map<String, Object> run(JobConf jobConf) {

        NetworkInspectParserController parseUtil = new NetworkInspectParserController();
        try {
            parseUtil.parse(jobConf);
        } catch (ParserNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (CrawlerException e) {
            LOG.error("code:" + e.code(), e);
        }
        return null;
    }
}
