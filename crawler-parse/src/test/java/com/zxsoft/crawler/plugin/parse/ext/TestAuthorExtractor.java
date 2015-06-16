package com.zxsoft.crawler.plugin.parse.ext;

import org.junit.Test;

import com.zxisl.commons.utils.Assert;
import com.zxsoft.crawler.parse.ext.ExtExtractor;

public class TestAuthorExtractor {

    @Test
    public void test() {
        String author = ExtExtractor.extractAuthor("来源：博讯  作者：");
        Assert.isTrue("".equals(author));
    }
}
