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
import com.zxsoft.crawler.parse.NetworkSearchParserController;
import com.zxsoft.crawler.parse.ParserNotFoundException;

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

        Map<String, Object> result = new HashMap<String, Object>();
        NetworkSearchParserController parserController = new NetworkSearchParserController();
        
        try {
            result.put("status", "RUNNING");
            parserController.parse(jobConf);
            result.put("status", "SUCCESS");
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
