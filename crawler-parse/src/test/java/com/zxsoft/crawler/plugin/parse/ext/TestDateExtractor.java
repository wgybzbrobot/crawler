package com.zxsoft.crawler.plugin.parse.ext;

import java.util.Date;

import org.junit.Test;

public class TestDateExtractor {

    @Test
    public void testExtract() {
        String text = "中国时间: 13:36 2015年04月22日星期三";
        // text = text.replaceAll("\u00A0", "");
        System.out.println(text);
        Date date = DateExtractor.extract(text);
        System.out.println(date.toLocaleString());
    }

    @Test
    public void testExtract2() {
        String text = "于 2014-12-3&nbsp;17:01 发表在";
        Date date = DateExtractor.extract(text);
        System.out.println(date.toLocaleString());
    }

    @Test
    public void testExtract3() {
        String text = "http://www.voachinese.com/content/congress-tibet-20130422/2731129.html";
        Date date = DateExtractor.extract(text);
        System.out.println(date.toLocaleString());
    }
}
