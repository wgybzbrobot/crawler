package com.zxsoft.crawler.plugin.parse.ext;

import org.junit.Test;

public class TestDateExtractor {

        @Test
        public void testHandler() {
                String text = "<div class=>2014-12-22 8:57:16&nbsp;&nbsp;&nbsp;3小时前来2天前源：安徽财经网&nbsp;&nbsp;";
                text = new DateExtractor().preHandle(text);
                System.out.println(text);
        }
}
