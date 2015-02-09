package com.zxsoft.crawler.plugin.parse.ext;

import org.junit.Test;

import com.zxisl.commons.utils.Assert;

public class TestExtExtractor {

        @Test
        public void testExtractSource() {
                Assert.isTrue("安徽财经网".equals(ExtExtractor.extractSource("2014-12-22 8:57:16   来源：安徽财经网  评论(").trim()));
                Assert.isTrue("苹果日报".equals(ExtExtractor.extractSource("来源：苹果日报")));
                Assert.isTrue("中時即時".equals(ExtExtractor.extractSource("來自: 中時即時    ").trim()));
        }

        @Test
        public void test() {
                String text = "<p class=\"xg1\"><a href=\"http://www.want-daily.com/home.php?mod=spacecp&amp;"
                                                + "ac=favorite&amp;type=article&amp;id=139474&amp;handlekey=favoritearticlehk_139474\" "
                                                + "id=\"a_favorite\" onclick=\"showWindow(this.id, this.href, 'get', 0);\" class=\"oshr ofav\">收藏"
                                                + "</a><a href=\"http://www.want-daily.com/home.php?mod=spacecp&amp;ac=share&amp;type"
                                                + "=article&amp;id=139474&amp;handlekey=sharearticlehk_139474\" id=\"a_share\" "
                                                + "onclick=\"showWindow(this.id, this.href, 'get', 0);\" class=\"oshr\">分享</a>2015-1-12 16:08"
                                                + "<span class=\"pipe\">|</span>發佈者: <a href=\"http://www.want-daily.com/home.php?mod=space&amp;uid=1078\">"
                                                + "gilbert</a><span class=\"pipe\">|</span>查看數: 56<span class=\"pipe\">|</span>評論數: 0               "
                                                + "<span class=\"pipe\">|</span>原作者: 李明賢               <span class=\"pipe\">|</span>來自: 中時即時                                            </p>";
                Assert.isTrue(56 == ExtExtractor.extractReadNum(text));
                Assert.isTrue(0 == ExtExtractor.extractReplyNum(text));
                Assert.isTrue("中時即時".equals(ExtExtractor.extractSource(text).trim()));
        }

        @Test
        public void testExtractReplyNum() {
                String text = "回复数: 89";
                Assert.isTrue(89 == ExtExtractor.extractReplyNum(text));
        }
        @Test
        public void testExtractReadNum() {
                String text = "阅读数: 19";
                Assert.isTrue(19 == ExtExtractor.extractReadNum(text));
        }
        @Test
        public void testExtractReadNum2() {
                String text = "127";
                Assert.isTrue(127 == ExtExtractor.extractReadNum(text));
        }
}
