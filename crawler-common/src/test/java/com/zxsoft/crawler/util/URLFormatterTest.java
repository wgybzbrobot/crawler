package com.zxsoft.crawler.util;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class URLFormatterTest {

        @Test
        public void test0() throws UnsupportedEncodingException {
                String url = "http://www.soubao.net/search/searchList.aspx"
                                                + "?startdate=%tY%tm%td&enddate=%tY%tm%td";
                url = URLFormatter.format(url);
                System.out.println(url);
        }
        
        @Test
        public void test1() throws UnsupportedEncodingException {
                String url = "http://www.soubao.net/search/searchList.aspx"
                                                + "?keyword=%s&startdate=%tY%tm%td&enddate=%tY%tm%td";
                url = URLFormatter.format(url, "中国");
                System.out.println(url);
        }
}
