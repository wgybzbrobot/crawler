package com.zxsoft.crawler.parse;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxisl.nldp.Nldp;
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.common.ListRule;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.util.Md5Signatrue;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.URLFormatter;

/**
 * 解析全网搜索，与网络巡检不同的是不用进入详细页
 */
public final class NetworkSearchParserController extends ParseTool {

    private static Logger LOG = LoggerFactory
                    .getLogger(NetworkSearchParserController.class);
    private AtomicInteger pageNum = new AtomicInteger(1);
    private AtomicInteger sum = new AtomicInteger(0);

    public void parse(JobConf jobConf) throws UnsupportedEncodingException,
                    MalformedURLException, CrawlerException {

        RecordInfo recordInfo = new RecordInfo(jobConf.getSource_id(), jobConf.getType(),
                        jobConf.getWorkerId(), jobConf.getIdentify_md5(), jobConf.getKeyword(),
                        jobConf.getIp(), jobConf.getLocation(),
                        jobConf.getSource_name(),
                        JobType.NETWORK_INSPECT.getValue(),
                        jobConf.getCountry_code(), jobConf.getLocationCode(),
                        jobConf.getProvince_code(), jobConf.getCity_code());
        recordInfo.setPlatform(jobConf.getPlatform());
        
        ListRule rule = jobConf.getListRule();

        if (rule == null) {
            throw new CrawlerException(ErrorCode.CONF_ERROR,
                            "No List Page Rule");
        }

        String keyword = jobConf.getKeyword();

        if (!StringUtils.isEmpty(jobConf.getKeywordEncode())) {
            keyword = URLEncoder.encode(keyword, jobConf.getKeywordEncode());
        }

        String original_url = URLFormatter.format(jobConf.getUrl(), keyword);

        String listDom = rule.getListdom();
        if (StringUtils.isEmpty(listDom))
            throw new CrawlerException(ErrorCode.CONF_ERROR,
                            "No list dom in ListPage rule .");

        HttpFetcher httpFetcher = new HttpFetcher();
        WebPage _page = new WebPage(original_url, rule.getAjax(), null);
        ProtocolOutput output = httpFetcher.fetch(_page);

        if (!output.getStatus().isSuccess()) {
            throw new CrawlerException(ErrorCode.NETWORK_ERROR, output.getStatus().toString());
        }

        Document document = output.getDocument();

        LOG.info("Searching " + keyword + " with " + jobConf.getSource_name()
                        + "-->" + jobConf.getType() + "...");

        Elements oldLines = null; // 用于检查页面数据是否没有变动
        
        while (true) {
            Elements list = document.select(listDom);
            if (CollectionUtils.isEmpty(list)) 
                throw new CrawlerException(ErrorCode.CONF_ERROR,
                                "Cannot get records from(" + original_url + ") by rule: " + listDom);

            Elements lines = list.first().select(rule.getLinedom());
            if (CollectionUtils.isEmpty(lines)) 
                throw new CrawlerException(ErrorCode.CONF_ERROR,
                                "Cannot get record lines by rule: "
                                                + rule.getLinedom());

            LOG.info("【" + jobConf.getType() + "】第" + pageNum.get()
                            + " 页, 数量: " + lines.size());

            List<RecordInfo> infos = new LinkedList<RecordInfo>();

            String curl = "";
            for (Element line : lines) {
                Elements _urls = line.select(rule.getUrldom());

                if (CollectionUtils.isEmpty(_urls))
                    continue;
                if (!StringUtils.isEmpty(_urls.first().absUrl("href"))) {
                    /** 链接地址 */
                    curl = _urls.first().absUrl("href");
                } else {
                    Elements _anchors = _urls.first().getElementsByTag("a");
                    if (CollectionUtils.isEmpty(_anchors)) // 没有链接
                        continue;
                    else
                        curl = _anchors.first().absUrl("href");
                }

                sum.incrementAndGet();

                /** 标题 */
                String title = line.select(rule.getUrldom()).first().text();
                /** 简介 */
                String synopsis = "";
                if (!StringUtils.isEmpty(rule.getSynopsisdom())) {
                    Elements synEles = line.select(rule.getSynopsisdom());
                    if (!CollectionUtils.isEmpty(synEles)) {
                        synopsis = synEles.first().text();
                    }
                }
                
                /** 日期 */
                long date = 0L;
                if (!StringUtils.isEmpty(rule.getDatedom())
                                && !CollectionUtils.isEmpty(line.select(rule
                                                .getDatedom()))) {
                    String str = line.select(rule.getDatedom()).first().html();
                    date = new Nldp(str).extractDateInMillis();
                }

                RecordInfo info = recordInfo.clone();
                info.setTitle(title);
                info.setUrl(curl);
                info.setId(Md5Signatrue.generateMd5(jobConf.getIdentify_md5(),curl));
                info.setContent(synopsis);
                info.setTimestamp(date);
                if (info.getTimestamp() == 0L)
                    info.setTimestamp(info.getLasttime());
                infos.add(info);
            }

            indexWriter.write(infos);

            // 翻页
            _page.setDocument(document);
            _page.setUrl(document.location());
            ProtocolOutput ptemp = fetchNextPage(pageNum.get(), _page);
            if (!ptemp.getStatus().isSuccess()) {
                LOG.debug("No next page, exit.");
                break;
            }
            
            document = ptemp.getDocument();
            
            if (isSamePage(lines, oldLines))
                break;
            oldLines = lines;
                
            pageNum.incrementAndGet();

        }
        LOG.info("Complete search" + keyword + " with "
                        + jobConf.getSource_name() + "-->" + jobConf.getType()
                        + " , total fetch record number is " + sum);
    }
}
