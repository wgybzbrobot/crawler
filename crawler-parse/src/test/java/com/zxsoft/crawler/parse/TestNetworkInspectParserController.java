package com.zxsoft.crawler.parse;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.DetailRule;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.common.ListRule;

public class TestNetworkInspectParserController {

    /**
     * @throws CrawlerException
     * @throws ParserNotFoundException
     * @throws UnsupportedEncodingException
     */
    @Test
    public void test() throws ParserNotFoundException,
                    UnsupportedEncodingException, CrawlerException {
        String url = "http://www.canyu.org/default.aspx";
        String listdom = "div#middle", linedom = "ul li", urldom = "a";
        String datedom = "", synopsisdom = "", updatedom = "", authordom = "";
        ListRule listRule = new ListRule(false, "news", listdom, linedom, urldom,
                        datedom, updatedom, synopsisdom, authordom);

        DetailRule detailRule = new DetailRule("http://www.canyu.org", "", "", "", "",
                        false, false, "div#bodyTd", "table tbody tr td:eq(1)",
                        "table tbody tr td:eq(0)", "div#content", "", "", "", "", "", "",
                        "", "");
        detailRule.setEncode("GBK");
        Set<DetailRule> detailRules = new HashSet<DetailRule>();
        detailRules.add(detailRule);
        
        JobConf job = new JobConf(JobType.NETWORK_SEARCH, url, "参与网", 504, 1262, "参与网首页",
                        listRule, detailRules);
        job.setCountry_code(1);

        NetworkInspectParserController parserController = new NetworkInspectParserController();
        parserController.parse(job);
    }
}
