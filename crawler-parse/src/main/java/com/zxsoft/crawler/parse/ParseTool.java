package com.zxsoft.crawler.parse;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.store.Output;
import com.zxsoft.crawler.store.impl.MysqlOutput;
import com.zxsoft.crawler.store.impl.RestOutput;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

public abstract class ParseTool {
        private Logger LOG = LoggerFactory.getLogger(ParseTool.class);
        private static HttpFetcher httpFetcher = new HttpFetcher();
        
        protected static Output indexWriter = new RestOutput();
        
        protected ProtocolOutput fetch(WebPage page) {
                return httpFetcher.fetch(page);
        }

        /**
         * 获取上一页
         */
        protected ProtocolOutput fetchPrevPage(int pageNum, WebPage page) {
                try {
                        return httpFetcher.fetchPrevPage(pageNum, page);
                } catch (PrevPageNotFoundException e) {
                        LOG.debug("Cannot get preview page of " + page.getBaseUrl() + ", may be it has no preview page.");
                } catch (PageBarNotFoundException e) {
                        LOG.debug("Cannot get page bar of " + page.getBaseUrl() + ", may be it has no page bar.");
                }
                ProtocolStatus status = new ProtocolStatus();
                status.setCode(STATUS_CODE.FAILED);
                status.setMessage("Cannot get preview page of " + page.getBaseUrl() + ", may be it has no preview page.");
                return new ProtocolOutput(null, status);
        }

        /**
         * 获取下一页
         */
        protected ProtocolOutput fetchNextPage(int pageNum, WebPage page) {
                try {
                        return httpFetcher.fetchNextPage(pageNum, page);
                } catch (PageBarNotFoundException e) {
                        LOG.debug("Cannot get Next page of " + page.getBaseUrl() + ", may be it has no next page.");
                }
                ProtocolStatus status = new ProtocolStatus();
                status.setCode(STATUS_CODE.FAILED);
                status.setMessage("Cannot get Next page of " + page.getBaseUrl() + ", may be it has no next page.");
                return new ProtocolOutput(null, status);
        }

        /**
         * 获取最后页
         */
        protected ProtocolOutput fetchLastPage(WebPage page) {
                try {
                        return httpFetcher.fetchLastPage(page);
                } catch (PageBarNotFoundException e) {
                        LOG.debug("Cannot get last page of " + page.getBaseUrl() + ", may be it has no last page.");
                }
                ProtocolStatus status = new ProtocolStatus();
                status.setCode(STATUS_CODE.FAILED);
                status.setMessage("Cannot get last page of " + page.getBaseUrl() + ", may be it has no last page.");
                return new ProtocolOutput(null, status);
        }

        protected boolean isSamePage(Elements lines, Elements oldlines) {
            
            if (lines == null || oldlines == null)
                return false;
            
            if (lines.text().trim().equals(oldlines.text().trim()))
                return true;
            return false;
        }
        
}
