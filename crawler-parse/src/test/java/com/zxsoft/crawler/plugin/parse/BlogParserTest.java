package com.zxsoft.crawler.plugin.parse;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.Assert;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.parse.FetchStatus.Status;
import com.zxsoft.crawler.storage.WebPage;

public class BlogParserTest {

        private static Logger LOG = LoggerFactory.getLogger(BlogParserTest.class);
                        
        /**
         * 新浪博客
         * @throws Exception
         */
        @Test
        public void testParseAnhuiNews() throws Exception {
                String baseUrl = "http://bbs.anhuinews.com/thread-1102159-1-2.html";
                WebPage page = new WebPage();
                page.setBaseUrl(baseUrl);
                page.setListUrl("http://bbs.anhuinews.com/forum-316-1.html");
                page.setTitle("我为祖国贴春联”春联征集火热进行中，欢迎网友踊跃参与");
                page.setAjax(false);

                Parser parser = new ForumParser();
                FetchStatus fetchStatus = parser.parse(page);
                Assert.isTrue(fetchStatus.getStatus() == Status.SUCCESS);
                LOG.info("count:" + fetchStatus);
        }
}
