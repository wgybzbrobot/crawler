package com.zxsoft.crawler.parse;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.common.DetailRule;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.common.ListRule;
import com.zxsoft.crawler.plugin.parse.ext.DateExtractor;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.URLFormatter;

/**
 * 调用相应的解析器解析网页
 */
public final class NetworkInspectParserController extends ParseTool {

    private static Logger LOG = LoggerFactory
                    .getLogger(NetworkInspectParserController.class);
    private static final int _pageNum = 2;

    public void parse(JobConf jobConf) throws ParserNotFoundException,
                    UnsupportedEncodingException, CrawlerException {

        RecordInfo recordInfo = new RecordInfo(jobConf.getSource_id(), jobConf.getType(),
                        jobConf.getWorkerId(), jobConf.getIdentify_md5(),
                        jobConf.getKeyword(), jobConf.getIp(), jobConf.getLocation(),
                        jobConf.getSource_name(), JobType.NETWORK_INSPECT.getValue(),
                        jobConf.getCountry_code(), jobConf.getLocationCode(),
                        jobConf.getProvince_code(), jobConf.getCity_code());

        ListRule rule = jobConf.getListRule();

        if (rule == null) {
            throw new CrawlerException(ErrorCode.CONF_ERROR, "No List Page Rule");
        }

        String listUrl = jobConf.getUrl();
        if (listUrl.contains("%t")) {
            listUrl = URLFormatter.format(listUrl);
        }

        WebPage page = new WebPage(listUrl, rule.getAjax());
        ParserFactory factory = new ParserFactory();

        ProtocolOutput output = fetch(page);

        if (!output.getStatus().isSuccess()) {
            throw new CrawlerException(ErrorCode.NETWORK_ERROR, "Fail to get List page ");
        }

        Document document = output.getDocument();

        LOG.info("Fetching " + jobConf.getSource_name() + "-->" + jobConf.getType()
                        + "...");

        String listDom = rule.getListdom(), lineDom = rule.getLinedom(), updateDom = rule
                        .getUpdatedom();
        String urlDom = rule.getUrldom();
        boolean hasUpdate = false, continuePage = true;
        int sum = 0, pageNum = 1;
        String msg = "";

        Elements oldLines = null; // 用于检查页面数据是否没有变动

        while (true) {
            Elements list = document.select(listDom);
            if (CollectionUtils.isEmpty(list)) {
                LOG.trace(document.html());
                throw new CrawlerException(ErrorCode.CONF_ERROR,
                                "No record find in list page.");
            }

            Elements lines = list.first().select(lineDom);
            // 没有更新日期时
            if (!hasUpdate && pageNum > _pageNum) {
                continuePage = false;
                msg = StringUtils.concat(msg, "没有获取列表页中记录的更新时间，抓完设定的页数" + _pageNum
                                + ", 若数量为0,则可能配置有误.");
                break;
            } else if (pageNum > _pageNum + 1) { // 有更新日期
                continuePage = false;
                msg += "抓完设定的页数" + (_pageNum + 1);
                break;
            }

            LOG.info("【" + jobConf.getSource_name() + "-->" + jobConf.getType() + "】第"
                            + pageNum + "页,　记录数: " + lines.size());
            int count = 0;

            for (Element line : lines) {

                if (CollectionUtils.isEmpty(line.select(urlDom))
                                || StringUtils.isEmpty(line.select(urlDom).first()
                                                .absUrl("href")))
                    continue;

                /*
                 * 如果详细页没有这些字段，则在列表页获取
                 */
                ExtInfo extInfo = new ExtInfo();
                extInfo.setIdentify_md5(jobConf.getIdentify_md5());
                
                Date update = null;
                if (!StringUtils.isEmpty(updateDom)
                                && !CollectionUtils.isEmpty(line.select(updateDom))) {
                    update = DateExtractor.extract(line.select(updateDom).first().html());
                    // if (update != null && update.getTime() + 60000L <
                    // page.getPrevFetchTime()) {
                    // if (count > 5) {
                    // msg += "截止时间" + new
                    // Date(page.getPrevFetchTime()).toLocaleString();
                    // continuePage = false;
                    // break;
                    // }
                    // count++;
                    // }
                    if (update != null) {
                        extInfo.setUpdate(update.getTime());
                        hasUpdate = true;
                    }
                }

                if (!StringUtils.isEmpty(rule.getDatedom())
                                && !CollectionUtils
                                                .isEmpty(line.select(rule.getDatedom()))) {
                    Date releasedate = DateExtractor.extract(line
                                    .select(rule.getDatedom()).first().html());
                    extInfo.setTimestamp(releasedate.getTime());
                }
                if (!StringUtils.isEmpty(rule.getAuthordom())
                                && !CollectionUtils.isEmpty(line.select(rule
                                                .getAuthordom()))) {
                    Element authorEle = line.select(rule.getAuthordom()).first();
                    extInfo.setAuthor(authorEle.text());
                    Elements _anchors = authorEle.getElementsByTag("a");
                    if (!CollectionUtils.isEmpty(_anchors)) {
                        extInfo.setHomeUrl(_anchors.first().absUrl("href"));
                    }
                }

                String curl = "";
                Elements as = line.getElementsByTag("a");
                if (!CollectionUtils.isEmpty(as) && as.size() == 1) { // 行记录就是一条url
                    curl = as.first().absUrl("href");
                } else {
                    curl = line.select(urlDom).first().absUrl("href");
                }
                URL u = null;
                try {
                    u = new URL(curl);
                } catch (MalformedURLException e1) {
                    LOG.warn("Not valid url: " + curl);
                    continue;
                }
                recordInfo.setOriginal_url(curl);
                String title = line.select(urlDom).first().text();
                recordInfo.setTitle(title);

                try {

                    DetailRule detailRule = seletDetailRule(jobConf.getDetailRules(), u);
                    if (null == detailRule) {
                        LOG.warn("Related detail page rule not configured: "
                                        + u.getHost());
                        continue;
                    }

                    Object[] params = new Object[] { recordInfo, detailRule,
                            jobConf.getPrevFetchTime(), extInfo };
                    Class[] paramClassArr = new Class[] { recordInfo.getClass(),
                            detailRule.getClass(), Long.class, extInfo.getClass() };
                    Parser parser = factory.getParserByCategory(rule.getCategory(),
                                    params, paramClassArr);
                    FetchStatus _status = parser.parse();
                    sum += _status.getCount();
                } catch (Exception e) {
                    LOG.error(listUrl + "," + msg, e);
                }
                if (!continuePage) {
                    break;
                }
            }

            if (!continuePage) {
                break;
            } else { // 翻页
                WebPage np = new WebPage(document.location(), rule.getAjax(), document);
                ProtocolOutput ptemp = fetchNextPage(pageNum, np);
                if (ptemp == null || !ptemp.getStatus().isSuccess()) {
                    break;
                }

                document = ptemp.getDocument();

                if (isSamePage(lines, oldLines))
                    break;

                oldLines = lines;
                pageNum++;
            }
        }
        LOG.info(jobConf.getSource_name() + "-->" + jobConf.getType()
                        + " complete fetch, total fetch record number is " + sum);
    }

    /** 一个版块可能有多个详细页规则, 根据host获取对应的规则. */
    private DetailRule seletDetailRule(Set<DetailRule> rules, URL u)
                    throws CrawlerException {
        if (rules == null)
            throw new CrawlerException(ErrorCode.CONF_ERROR, "No detail page rules");

        DetailRule[] drs = rules.toArray(new DetailRule[] {});

        if (rules.size() == 1)
            return drs[0];

        for (DetailRule dr : drs) {
            if (StringUtils.isEmpty(dr.getHost()))
                throw new CrawlerException(ErrorCode.CONF_ERROR,
                                "Host field in detail rule is null");
            if (dr.getHost().trim().contains(u.getHost().trim()))
                return dr;
        }
        return null;
    }
}
