package com.zxsoft.crawler.web.verification;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.parse.ParseTool;
import com.zxsoft.crawler.plugin.parse.ext.DateExtractor;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.URLFormatter;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PageHelper;
import com.zxsoft.crawler.web.model.ThreadInfo;

public class ListConfigVerification extends ParseTool {

        private static Logger LOG = LoggerFactory.getLogger(ListConfigVerification.class);

        public Map<String, Object> verify(ConfList listConf, String keyword, boolean autoUrl) {

                Map<String, Object> map = new HashMap<String, Object>();
                Map<String, String> errors = new HashMap<String, String>();

                List<ThreadInfo> list = new ArrayList<ThreadInfo>();
                String pageStr = "", testurl = listConf.getUrl();
                if ("search".equals(listConf.getCategory())) {
                        if (autoUrl) {
                                try {
                                        testurl = URLFormatter.format(testurl, keyword);
                                } catch (UnsupportedEncodingException e) {
                                       LOG.error("", e);
                                       return null;
                                }
                        } else {
                                testurl = String.format(testurl, keyword);
                        }
//                        try {
//                                testurl = URIUtil.encodePathQuery(testurl, "UTF-8");
//                        } catch (URIException e) {
//                                e.printStackTrace();
//                        }
                } else {
                        try {
                               testurl =  URLFormatter.format(testurl);
                        } catch (UnsupportedEncodingException e) {
                                LOG.error("", e);
                                return null;
                        }
                }
                WebPage page = new WebPage(testurl, listConf.getAjax());
                ProtocolOutput protocolOutput = fetch(page);
                Document document = null;

                if (protocolOutput == null || !protocolOutput.getStatus().isSuccess()) {
                        errors.put("urlerror", "连接失败");
                } else {
                        try {
                                document = protocolOutput.getDocument();
                              String html = document.html();
                              System.out.println(html);
//                              map.put("html", html);
                                if (StringUtils.isEmpty(listConf.getListdom())) {
                                        errors.put("listdomerror", "必填");
                                } else {// document.select("form#moderate  table:gt(1)");
                                        Elements elements = document.select(listConf.getListdom());
                                        if (CollectionUtils.isEmpty(elements)) {
                                                errors.put("listdom", "获取列表失败");
                                        } else {
                                                Element listElement = elements.first();
                                                if (StringUtils.isEmpty(listConf.getLinedom())) {
                                                        errors.put("linedom", "必填");
                                                } else {
                                                        Elements lineElements = listElement.select(listConf.getLinedom());
                                                        if (CollectionUtils.isEmpty(lineElements) || lineElements.size() < 3) {
                                                                errors.put("linedom", "获取列表行失败");
        //                                                        LOG.info(listElement.html());
                                                        } else {
                                                                int i = 0;
                                                                int updateErrorCount = 0, releaseDateErrorCount = 0;
                                                                int urlErrorCount = 0;
                                                                for (Element lineEle : lineElements) {
                                                                        i++;
                                                                        if (CollectionUtils.isEmpty(lineEle.select(listConf.getUrldom()))) {
                                                                                if (i < 10) {
                                                                                        continue;
                                                                                }
                                                                                urlErrorCount++;
                                                                        }
        
                                                                        Element urlEle = lineEle.select(listConf.getUrldom()).first();
                                                                        if (urlEle == null) {
                                                                                continue;
                                                                        }
                                                                        String url = urlEle.absUrl("href");
                                                                        String title = urlEle.text();
        
                                                                        /*
                                                                         * 更新时间
                                                                         */
                                                                        Date update = null;
                                                                        if (!StringUtils.isEmpty(listConf.getUpdatedom())) {
                                                                                Elements dateElements = lineEle.select(listConf.getUpdatedom());
                                                                                if (CollectionUtils.isEmpty(dateElements)) {
                                                                                        updateErrorCount++;
                                                                                } else {
                                                                                        update = DateExtractor.extract(dateElements.first().html());
                                                                                        if (update == null) {
                                                                                                LOG.warn("没有获取到时间: " + dateElements.first().html());
                                                                                        }
                                                                                }
                                                                        }
                                                                        /*
                                                                         * 发布时间
                                                                         */
                                                                        Date releaseDate = null;
                                                                        if (!StringUtils.isEmpty(listConf.getDatedom())) {
                                                                                Elements dateElements = lineEle.select(listConf.getDatedom());
                                                                                if (CollectionUtils.isEmpty(dateElements)) {
                                                                                        releaseDateErrorCount++;
                                                                                } else {
                                                                                        releaseDate = DateExtractor.extract(dateElements.first().html());
                                                                                        if (releaseDate == null) {
                                                                                                LOG.warn("没有获取到时间: " + dateElements.first().html());
                                                                                        }
                                                                                }
                                                                        }
        
                                                                        
                                                                        ThreadInfo info = new ThreadInfo(url, title, update, releaseDate);
        
                                                                        /*
                                                                         * 作者
                                                                         */
                                                                        if (!StringUtils.isEmpty(listConf.getAuthordom())) {
                                                                                Elements authorEles = lineEle.select(listConf.getAuthordom());
                                                                                if (!CollectionUtils.isEmpty(authorEles)) {
                                                                                        info.setAuthor(authorEles.first().text());
                                                                                }
                                                                        }
                                                                        
                                                                        if (!StringUtils.isEmpty(listConf.getSynopsisdom())) {
                                                                                Elements synoEles = lineEle.select(listConf.getSynopsisdom());
                                                                                if (!CollectionUtils.isEmpty(synoEles)) {
                                                                                        info.setSynopsis(synoEles.first().text());
                                                                                }
                                                                        }
        
                                                                        list.add(info);
                                                                }
                                                                if (updateErrorCount > 10) {
                                                                        errors.put("updatedom", "获取更新时间失败超过10次");
                                                                }
                                                                if (releaseDateErrorCount > 10) {
                                                                        errors.put("datedom", "获取发布时间失败超过10次");
                                                                }
                                                                if (urlErrorCount > 10) {
                                                                        errors.put("urldom", "获取详细页URL失败");
                                                                }
                                                        }
                                                }
                                        }
                                }
        
                                if (StringUtils.isEmpty(pageStr)) {
                                        try {
                                                Element pagebar = PageHelper.getPageBar(document);
                                                pageStr = pagebar.html();
                                        } catch (NullPointerException | PageBarNotFoundException e) {
                                                LOG.warn("没有找到分页栏");
                                        }
                                }
                        } catch (Exception e) {
                                LOG.error("配置验证出错", e);
                        }
                }

                map.put("errors", errors);
                map.put("list", list);
                if (StringUtils.isEmpty(pageStr))
                        pageStr = "没有找到";
                map.put("pagebar", pageStr);
                return map;
        }

}
