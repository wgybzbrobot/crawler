//package com.zxsoft.crawler.parse;
//
//import java.io.UnsupportedEncodingException;
//import java.net.MalformedURLException;
//
//import org.junit.Test;
//
//import com.zxsoft.crawler.storage.WebPage;
//
//public class TestNetworkSearchParseController {
//
//        /**
//         * 百度搜索
//         * @throws ParserNotFoundException
//         * @throws MalformedURLException 
//         * @throws UnsupportedEncodingException 
//         */
//        @Test
//        public void testBaiduSearch() throws ParserNotFoundException, UnsupportedEncodingException, MalformedURLException {
//                String engineUrl = "http://www.baidu.com/s?wd=%s&ie=utf-8";
//                WebPage page = new WebPage("大数据  你知道吗?", engineUrl);
//                
//                NetworkSearchParserController parserController = new NetworkSearchParserController();
//                parserController.parse(page);
//        }
//        
//        /**
//         * 搜狗搜索
//         * @throws ParserNotFoundException
//         * @throws MalformedURLException 
//         * @throws UnsupportedEncodingException 
//         */
//        @Test
//        public void testSogouSearch() throws ParserNotFoundException, UnsupportedEncodingException, MalformedURLException {
//                String engineUrl = "http://www.sogou.com/web?query=%s";
//                WebPage page = new WebPage("大数据  你知道吗?", engineUrl);
//                
//                NetworkSearchParserController parserController = new NetworkSearchParserController();
//                parserController.parse(page);
//        }
//        
//        /**
//         * 有道搜索
//         * @throws ParserNotFoundException
//         * @throws MalformedURLException 
//         * @throws UnsupportedEncodingException 
//         */
//        @Test
//        public void testYoudaoSearch() throws ParserNotFoundException, UnsupportedEncodingException, MalformedURLException {
//                String engineUrl = "http://www.youdao.com/search?q=%s&ue=utf8&keyfrom=web.index";
//                WebPage page = new WebPage("大数据  你知道吗?", engineUrl);
//                
//                NetworkSearchParserController parserController = new NetworkSearchParserController();
//                parserController.parse(page);
//        }
//}
