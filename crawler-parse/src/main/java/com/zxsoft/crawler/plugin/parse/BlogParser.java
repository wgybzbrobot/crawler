package com.zxsoft.crawler.plugin.parse;

import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.Assert;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.StringUtils;

import com.zxsoft.crawler.dns.DNSCache;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.parse.FetchStatus.Status;
import com.zxsoft.crawler.plugin.parse.ext.DateExtractor;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.util.Md5Signatrue;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.Utils;


/**
 * 博客类解析器
 * @author xiayun
 *
 */
public class BlogParser extends Parser {

        private static Logger LOG = LoggerFactory.getLogger(BlogParser.class);
        private List<RecordInfo> recordInfos = new LinkedList<RecordInfo>();
        public List<RecordInfo> getRecordInfos() {
                return recordInfos;
        }
        private String ip;
        private long monitorTime = new Date().getTime() / 1000L;
        
        /**
         * 解析页面入口
         */
        @Override
        public FetchStatus parse(WebPage page) throws Exception {
                Assert.notNull(page, "Page is null");
                String mainUrl = page.getBaseUrl();
                long prevFetchTime = page.getPrevFetchTime();
                DetailConf detailConf = confDao.getDetailConf(page.getListUrl(), Utils.getHost(mainUrl));
                if (detailConf == null) {
                        LOG.error("没有详细页配置: " + mainUrl);
                        return new FetchStatus(mainUrl, 41, Status.CONF_ERROR);
                }

                ProtocolOutput _output = fetch(page);
                if (!_output.getStatus().isSuccess()) {
                        return new FetchStatus(mainUrl, 51, Status.PROTOCOL_FAILURE);
                }
                
                Document mainDoc = _output.getDocument();
                page.setDocument(mainDoc);
                ip = DNSCache.getIp(new URL(mainUrl));
                
                /*
                 * 解析博主的博客内容
                 */
                RecordInfo info = new RecordInfo(page.getTitle(), mainUrl, System.currentTimeMillis() / 1000);
                info.setIp(ip);
                String replyNumDom = detailConf.getReplyNum();
                if (!StringUtils.isEmpty(replyNumDom) && !CollectionUtils.isEmpty(mainDoc.select(replyNumDom)))
                        info.setComment_count(Utils.extractNum(mainDoc.select(replyNumDom).first().text()));
                String reviewNumDom = detailConf.getReviewNum();
                if (!StringUtils.isEmpty(reviewNumDom) && !CollectionUtils.isEmpty(mainDoc.select(reviewNumDom)))
                        info.setRead_count(Integer.valueOf(Utils.extractNum(mainDoc.select(reviewNumDom).first().text())));

                Elements masterEles = mainDoc.select(detailConf.getMaster());
                if (!CollectionUtils.isEmpty(masterEles)) {
                        Element masterEle = masterEles.first();
                        String authorDom = detailConf.getAuthor();
                        if (!StringUtils.isEmpty(authorDom) && !CollectionUtils.isEmpty(masterEle.select(authorDom)))
                                info.setNickname(masterEle.select(authorDom).first().text());
                        Elements contentEles = masterEle.select(detailConf.getContent());
                        if (!CollectionUtils.isEmpty(contentEles)) {
                                Element contentEle = contentEles.first();
                                info.setContent(contentEle.text());
                                info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, ""));
                                info.setVoice_url(MultimediaExtractor.extractAudioUrl(contentEle));
                                info.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
                        }
                        if (info.getTimestamp() == 0) {
                                String dateDom = detailConf.getDate();
                                if (!StringUtils.isEmpty(dateDom) && !CollectionUtils.isEmpty(masterEle.select(dateDom))) {
                                        String dateField = masterEle.select(dateDom).first().html();
                                        if (!StringUtils.isEmpty(dateField)) {
                                                info.setTimestamp(DateExtractor.extractInMilliSecs(dateField));
                                        }
                                }
                        }
                        info.setId(Md5Signatrue.generateMd5(info.getNickname(), info.getContent(), info.getPic_url(), info.getVoice_url(),
                                info.getVideo_url()));
                        getRecordInfos().add(info);
                } else {
                        return new FetchStatus(mainUrl, 42, Status.CONF_ERROR);
                }
                
                /*
                 * 解析博友的回复
                 */
                Document _doc = mainDoc;
                
                
                
                
                
                
                return null;
        }

}
