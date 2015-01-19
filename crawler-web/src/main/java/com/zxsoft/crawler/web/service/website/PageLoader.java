package com.zxsoft.crawler.web.service.website;

import org.jsoup.nodes.Document;

import com.zxsoft.crawler.parse.ParseTool;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.storage.WebPage;

public class PageLoader extends ParseTool {

        /**
         * 下载网页
         * @param url
         * @param ajax 是否ajax加载
         * @return
         */
        public String loadPage(String url, boolean ajax) {
                WebPage page = new WebPage(url, ajax);
                ProtocolOutput protocolOutput = fetch(page);
                Document document = null;

                if (protocolOutput == null || !protocolOutput.getStatus().isSuccess()) {
                       return "连接失败";
                }
                document = protocolOutput.getDocument();
                String html = document.html();
//                System.out.println(html);
                return html;
        }
}
