package com.zxsoft.crawler.parse;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
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
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.common.ListRule;
import com.zxsoft.crawler.plugin.parse.ext.DateExtractor;
import com.zxsoft.crawler.plugin.parse.ext.DateExtractor2;
import com.zxsoft.crawler.plugin.parse.ext.TextExtract;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
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
                        jobConf.getWorkerId(), jobConf.getIdentify_md5(),
                        jobConf.getKeyword(), jobConf.getIp(), jobConf.getLocation(),
                        jobConf.getSource_name(), JobType.NETWORK_INSPECT.getValue(),
                        jobConf.getCountry_code(), jobConf.getLocationCode(),
                        jobConf.getProvince_code(), jobConf.getCity_code());
        recordInfo.setPlatform(jobConf.getPlatform());

        ListRule rule = jobConf.getListRule();

        if (rule == null) {
            throw new CrawlerException(ErrorCode.CONF_ERROR, "No List Page Rule");
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
            throw new CrawlerException(ErrorCode.NETWORK_ERROR, output.getStatus()
                            .toString());
        }

        Document document = output.getDocument();

        LOG.info("Searching " + keyword + " with " + jobConf.getSource_name() + "-->"
                        + jobConf.getType() + "...");

        Elements oldLines = null; // 用于检查页面数据是否没有变动

        int likeCount = 0;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 1200000L) {
            Elements list = document.select(listDom);
            if (CollectionUtils.isEmpty(list))
                throw new CrawlerException(ErrorCode.CONF_ERROR,
                                "Cannot get records from(" + original_url + ") by rule: "
                                                + listDom);

            Elements lines = list.first().select(rule.getLinedom());
            if (CollectionUtils.isEmpty(lines))
                throw new CrawlerException(ErrorCode.CONF_ERROR,
                                "Cannot get record lines by rule: " + rule.getLinedom());

            LOG.info("【" + jobConf.getType() + "】第" + pageNum.get() + " 页, 数量: "
                            + lines.size());

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
                
                RecordInfo info = recordInfo.clone();

                //计算百度快照
                Elements anchors = line.getElementsByTag("a");;
                if (!CollectionUtils.isEmpty(anchors)) {
                    for (Element anchor : anchors) {
                        if (anchor.ownText().contains("百度快照")) {
                            info.setHome_url(anchor.absUrl("href"));
                        }
                    }
                }
                


                /** 日期 */
                info.setTimestamp(0L);
                long date = 0L;
                if (!StringUtils.isEmpty(rule.getDatedom())
                                && !CollectionUtils.isEmpty(line.select(rule
                                                .getDatedom()))) {
                    String str = line.select(rule.getDatedom()).first().html();
                    date = DateExtractor.extractInMilliSecs(str);
                    if (date != 0L)
                        info.setTimestamp(date);
                }
                
                info.setTitle(title);
                info.setUrl(curl);
                info.setContent(synopsis);

                /*
                 * 进入页面链接中抓取数据
                 */
                if (jobConf.getGoInto()) {
                    try { // click into
                        WebPage _p = new WebPage(curl, rule.getAjax());
                        ProtocolOutput _o = fetch(_p);
                        if (STATUS_CODE.NOTFOUND.equals(_o.getStatus().getCode()) 
                                        || STATUS_CODE.ACCESS_DENIED.equals(_o.getStatus().getCode())
                                        || _o.getDocument() == null) {
                            continue;// 过滤403,404网页
                        }
                        
                        int i = 0;
                        while (STATUS_CODE.RETRY.equals(_o.getStatus().getCode()) && i++ <= 3) {
                            _o = fetch(_p);
                        }
                        if (STATUS_CODE.RETRY.equals(_o.getStatus().getCode())
                                        || STATUS_CODE.NOTFOUND.equals(_o.getStatus().getCode()) 
                                        || STATUS_CODE.ACCESS_DENIED.equals(_o.getStatus().getCode())
                                        || _o.getDocument() == null)
                            continue;


                        Document _d = _o.getDocument();
                        
                        try { // 过滤链接是网站主页
                            URL _u = new URL(_d.location());
                            if (StringUtils.isEmpty(_u.getPath()) || "/".equals(_u.getPath()))
                                continue;
                        } catch (Exception e) {
                            continue;
                        }
                        info.setUrl(_d.location());
//                        String text = res.getText();
//                        if (text.contains("While trying to retrieve the URL")) {
//                            System.out.println(_d.location());
//                        }
                        
//                        if (text.length() > info.getContent().length())
//                            info.setContent(text);
                        
                        // 设置时间
                        info.setTimestamp(calTime(_d, info.getTimestamp()));
                        String text = TextExtract.parse(_d.html());
                        info.setContent(text);
                    } catch (Exception e) {
                        LOG.error("Click into search page error: " + e.getMessage(), e);
                    }
                } 

                info.setId(Md5Signatrue.generateMd5(jobConf.getIdentify_md5(), curl));

                if (info.getTimestamp() != 0L && !StringUtils.isEmpty(info.getContent()))
                    infos.add(info);
//                System.out.println(keyword + new Date(info.getTimestamp()).toLocaleString() + "\t"
//                                + info.getUrl());
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

            if (isSamePage(lines, oldLines) && likeCount++ > 10)
                break;
            oldLines = lines;

            pageNum.incrementAndGet();

        }
        LOG.info("Complete search" + keyword + " with " + jobConf.getSource_name()
                        + "-->" + jobConf.getType() + " , total fetch record number is "
                        + sum);
    }
    
    private long calTime(Document _d, long outTime) {
        
        if (outTime <= 315504000000L)
            outTime = 0L;
        
        long _timeIn = 0;
        
        DateExtractor2 dateExtractor2 = new DateExtractor2();
        dateExtractor2.extract(_d);
        _timeIn = dateExtractor2.getTimeInMs();
        
        if (_timeIn == 0 )
            return outTime;
        
        return _timeIn;
    }
}
