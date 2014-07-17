package com.zxsoft.crawler.parse;

import org.junit.Assert;
import org.junit.Test;

public class RegExpTest {

    public String regexp = "^(http://)?tieba.baidu.com/(home|i|f/index)/.*";

    @Test
    public void test1() {
        String url = "http://tieba.baidu.com/ns?cl=2&rn=20&tn=news&";
        Assert.assertFalse(url.matches(regexp));
    }

    @Test
    public void test2() {
        String url = "http://tieba.baidu.com/home/83737273s";
        Assert.assertTrue(url.matches(regexp));
    }

    @Test
    public void test3() {
        String url = "http://tieba.baidu.com/f/index/forumpark?cn=";
        Assert.assertTrue(url.matches(regexp));
    }

    @Test
    public void test4() {
        String url = "http://tieba.baidu.com/i/main?un=%D";
        Assert.assertTrue(url.matches(regexp));
    }
    
    @Test
    public void test5() {
        String url = "http://static.tieba.baidu.com/tb/tsgz/index.html";
        Assert.assertFalse(url.matches(regexp));
    }
    
    @Test
    public void test6 () {
        String url = "http://sports.news.sina.com.cn/ekdke/2014-05-20/220630179000.shtml";
        String reg = "^http://.*news.sina.com.cn/.*/\\d{2,4}-\\d{1,2}-\\d{1,2}/.*.shtml";
        Assert.assertTrue(url.matches(reg));
    }
    
    @Test public void test7() {
        String url = "http://sports.sina.com.cn/golf/2014-05-22/10597176416.shtml";
        String reg = "^http://.*sina.com.cn/.*shtml";
        Assert.assertTrue(url.matches(reg));
    }

}
