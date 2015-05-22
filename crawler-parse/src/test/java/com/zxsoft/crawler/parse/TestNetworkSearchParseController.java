//package com.zxsoft.crawler.parse;
//
//import java.io.UnsupportedEncodingException;
//import java.net.MalformedURLException;
//import java.util.Set;
//
//import org.junit.Test;
//
//import com.zxsoft.crawler.api.JobType;
//import com.zxsoft.crawler.common.CrawlerException;
//import com.zxsoft.crawler.common.DetailRule;
//import com.zxsoft.crawler.common.JobConf;
//import com.zxsoft.crawler.common.ListRule;
//
//public class TestNetworkSearchParseController {
//
//    /**
//     * 百度搜索
//     * 
//     * @throws CrawlerException 
//     * @throws MalformedURLException 
//     * @throws UnsupportedEncodingException 
//     */
//    @Test
//    public void testBaiduSearch() throws  CrawlerException, UnsupportedEncodingException, MalformedURLException {
//        String url = "http://www.baidu.com/s?wd=%s&ie=utf-8";
//        String listdom = "div#content_left", linedom="div.c-container", urldom="h3.t > a";
//        String datedom = "div.f13 span", synopsisdom = "div.c-abstract", updatedom="", authordom="";
//        
//        ListRule listRule = new ListRule(false, "search", listdom, linedom, urldom, datedom, updatedom, synopsisdom, authordom);
//        Set<DetailRule> detailRules = null;
//        JobConf job = new JobConf(JobType.NETWORK_SEARCH, url, "百度", 3, 2, "百度搜索", listRule, detailRules);
//        job.setCountry_code(1);
//        job.setKeyword("合肥传销");
//        
//        NetworkSearchParserController parserController = new NetworkSearchParserController();
//        parserController.parse(job);
//    }
//
//    @Test
//    public void testBingSearch() throws  CrawlerException, UnsupportedEncodingException, MalformedURLException {
//        String url = "http://cn.bing.com/search?q=%s";
//        String listdom = "ol#b_results", linedom="[class~=(b_algo|b_ans|b_pag)", urldom="h2 a";
//        String synopsisdom = "div.b_caption p";
//        
//        ListRule listRule = new ListRule(false, "search", listdom, linedom, urldom, "", "", synopsisdom, "");
//        Set<DetailRule> detailRules = null;
//        JobConf job = new JobConf(JobType.NETWORK_SEARCH, url, "微软", 3, 2, "必应搜索", listRule, detailRules);
//        job.setCountry_code(1);
//        job.setGoInto(true);
//        job.setKeyword("合肥传销");
//        
//        NetworkSearchParserController parserController = new NetworkSearchParserController();
//        parserController.parse(job);
//    }
//
//}
