package com.zxsoft.crawler.plugin.parse.ext;

import java.util.Date;

import org.junit.Test;

public class TestDateExtractor {

        @Test
        public void testExtract() {
                String text = "发表于 1 小时前";
//                text = text.replaceAll("\u00A0", "");
                System.out.println(text);
                Date date  = DateExtractor.extract(text);
                System.out.println(date.toLocaleString());
        }
        
        @Test
        public void testExtract2() {
                String text = "于 2014-12-3&nbsp;17:01 发表在";
                Date date  = DateExtractor.extract(text);
                System.out.println(date.toLocaleString());
        }

        @Test
        public void testExtract3() {
                String text = "2015年 1月 22日";
                Date date  = DateExtractor.extract(text);
                System.out.println(date.toLocaleString());
        }
}
