package com.zxsoft.crawler.api.impl;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.api.CrawlTool;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
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
        Map<String, Object> result = new HashMap<String, Object>();
        NetworkInspectParserController parseUtil = new NetworkInspectParserController();
        try {
            result.put("status", "RUNNING");
            parseUtil.parse(jobConf);
            result.put("status", "SUCCESS");
        } catch (ParserNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            result.put("status", "FAILURE");
        } catch (CrawlerException e) {
            ErrorHandler.handle(e, jobConf);
            if (e.code() == ErrorCode.NETWORK_ERROR.code) {
                result.put("status", "TIMEOUT");
            } else {
                result.put("status", "FAILURE");
            }
        }
        return result;
    }
}
