package com.zxsoft.crawler.plugin.parse;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector.SelectorParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.DetailRule;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.parse.ExtInfo;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.parse.FetchStatus.Status;
import com.zxsoft.crawler.parse.Platform;
import com.zxsoft.crawler.plugin.parse.ext.DateExtractor;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
import com.zxsoft.crawler.protocol.util.Md5Signatrue;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.Utils;

/**
 * 博客类解析器
 * 
 * @author xiayun
 *
 */
public class BlogParser extends Parser {


        private static Logger LOG = LoggerFactory.getLogger(BlogParser.class);
        private List<RecordInfo> recordInfos = new LinkedList<RecordInfo>();

        public BlogParser(RecordInfo recordInfo, DetailRule detailRule,
                        long prevFetchTime, ExtInfo extInfo) {
            super(recordInfo, detailRule, prevFetchTime, extInfo);
        }

        /**
         * 解析页面入口
         * @throws CrawlerException 
         */
        @Override
        public FetchStatus parse() throws CrawlerException  {
            if (detailRule == null)
                throw new CrawlerException(ErrorCode.CONF_ERROR,
                                "Detail page rule is null.");
            
        String mainUrl = recordInfo.getOriginal_url();
        WebPage page = new WebPage(mainUrl, detailRule.getAjax(), null);
                ProtocolOutput _output = fetch(page);
                
                if (!_output.getStatus().isSuccess()) {
                        return new FetchStatus(mainUrl, 51, Status.PROTOCOL_FAILURE);
                }

                Document mainDoc = _output.getDocument();
                page.setDocument(mainDoc);

                /*
                 * 解析博主的博客内容
                 */
                RecordInfo info = recordInfo.clone();
                info.setUrl(mainUrl);
                info.setPlatform(Platform.PLATFORM_FORUM);
                String replyNumDom = detailRule.getReplyNum();
                if (!StringUtils.isEmpty(replyNumDom) && !CollectionUtils.isEmpty(mainDoc.select(replyNumDom)))
                        info.setComment_count(Utils.extractNum(mainDoc.select(replyNumDom).first().text()));
                String reviewNumDom = detailRule.getReviewNum();
                if (!StringUtils.isEmpty(reviewNumDom) && !CollectionUtils.isEmpty(mainDoc.select(reviewNumDom)))
                        info.setRead_count(Integer.valueOf(Utils.extractNum(mainDoc.select(reviewNumDom).first().text())));

                Elements masterEles = mainDoc.select(detailRule.getMaster());
                if (CollectionUtils.isEmpty(masterEles)) 
                    throw new CrawlerException(ErrorCode.CONF_ERROR, "No master rule found");
                    
                Element masterEle = masterEles.first();
                
                if (StringUtils.isEmpty(detailRule.getAuthor())) {
                        String authorDom = detailRule.getAuthor();
                        if (!StringUtils.isEmpty(authorDom) && !CollectionUtils.isEmpty(masterEle.select(authorDom))) {
                                Element userEle = masterEle.select(authorDom).first();
                                info.setNickname(userEle.text());
                                if (!StringUtils.isEmpty(userEle.absUrl("href"))) {
                                        info.setHome_url(userEle.absUrl("href"));
                                }
                        }
                } else {
                        info.setNickname(extInfo.getAuthor());
                        info.setHome_url(extInfo.getHomeUrl() == null ? "" : extInfo.getHomeUrl());
                }
                Elements contentEles = null;
                if (!StringUtils.isEmpty(detailRule.getContent()) && !CollectionUtils.isEmpty(contentEles = masterEle.select(detailRule.getContent())) ) {
                        Element contentEle = contentEles.first();
                        info.setContent(contentEle.text());
                        info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, ""));
                        info.setVoice_url(MultimediaExtractor.extractAudioUrl(contentEle));
                        info.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
                }
                if (extInfo.getTimestamp() == 0) {
                        String dateDom = detailRule.getDate();
                        if (!StringUtils.isEmpty(dateDom) && !CollectionUtils.isEmpty(masterEle.select(dateDom))) {
                                String dateField = masterEle.select(dateDom).first().html();
                                if (!StringUtils.isEmpty(dateField)) {
                                        info.setTimestamp(DateExtractor.extractInMilliSecs(dateField));
                                }
                        }
                } else {
                        info.setTimestamp(extInfo.getTimestamp());
                }
                String original_id = Md5Signatrue.generateMd5(info.getNickname(), info.getContent(), info.getPic_url(), info.getVoice_url(),
                                info.getVideo_url());
                info.setId(original_id);
                info.setOriginal_id(original_id);
                recordInfo.setOriginal_id(original_id);
                recordInfos.add(info);
                
                /*
                 * 解析博友的回复
                 */
                Document _doc = mainDoc;
                String currentUrl = mainUrl, newPageUrl = "";
                _output = null;
                if (!detailRule.getFetchorder()) { // 从第一页
                        int pageNum = 1;
                        do {
                                boolean isContinue = parsePage(prevFetchTime, _doc, mainUrl, currentUrl);
                                if (!isContinue) {
                                        break;
                                }
                                pageNum++;
                                WebPage np =new WebPage(_doc.location(), detailRule.getAjax(),
                                                _doc);
                                _output = fetchNextPage(pageNum, np);
                                if (_output == null || _output.getStatus().getCode() != STATUS_CODE.SUCCESS)
                                        break;
                                _doc = _output.getDocument();
                                currentUrl = newPageUrl;
                        } while (!StringUtils.isEmpty(newPageUrl));

                } else { // 从最后一页
                        WebPage np =new WebPage(_doc.location(), detailRule.getAjax(),
                                        _doc);; // 跳转到最后一页
                        _output = fetchLastPage(np);
                        Document lastDoc = null;
                        if (_output == null || _output.getStatus().getCode() != STATUS_CODE.SUCCESS
                                                        || (lastDoc = _output.getDocument()) == null) {
                                parsePage(prevFetchTime, _doc, mainUrl, currentUrl);
                        } else {
                                _doc = lastDoc;
                                while (true) {
                                        if (_doc == null || StringUtils.isEmpty(currentUrl = _doc.location()))
                                                break;
                                        boolean isContinue = parsePage(prevFetchTime, _doc, mainUrl, currentUrl);
                                        if (!isContinue) {
                                                break;
                                        }
                                        // 获取上一页
                                        np.setDocument(_doc);
                                        np.setUrl(_doc.location());
                                        _output = fetchPrevPage(-1, np);
                                        if (_output == null || _output.getStatus().getCode() != STATUS_CODE.SUCCESS)
                                                break;
                                        _doc = _output.getDocument();
                                }
                        }
                }

                int count = 0;
                        count = indexWriter.write(recordInfos);
                return new FetchStatus(mainUrl, 21, Status.SUCCESS, count);
        }
        
        /**
         * 解析一页
         */
        private boolean parsePage(long prevFetchTime, Document doc, String mainUrl, String currentUrl)
                throws SelectorParseException {
                // 没有回复配置
                if (StringUtils.isEmpty(detailRule.getReply())) {
                        return false;
                }
                Elements replyEles = doc.select(detailRule.getReply());
                Collections.reverse(replyEles);
                for (Element element : replyEles) {
                        RecordInfo reply = recordInfo.clone();
                        reply.setUrl(currentUrl);
                        reply.setPlatform(Platform.PLATFORM_REPLY);
                         // 保存回复
                        String id = save(reply, element, null);
                        // 由于时间的误差，将抓取时间拓展１分钟
                        if (reply.getTimestamp() != 0 && reply.getTimestamp() + 60000L < prevFetchTime)
                                return false;
                        if (id == null)
                                continue;
                        // 解析子回复
                        String subReplyDom = detailRule.getSubReply();
                        if (StringUtils.isEmpty(subReplyDom))
                                continue;
                        Elements subReplyEles = element.select(subReplyDom);
                        if (!CollectionUtils.isEmpty(subReplyEles)) {
                                for (Element ele : subReplyEles) {
                                        RecordInfo subReply = recordInfo.clone();
                                        reply.setUrl(currentUrl);
                                        reply.setPlatform(Platform.PLATFORM_REPLY);
                                        saveSub(subReply, ele);
                                }
                        }
                }
                return true;
        }
        
        /**
         * 保存回复
         */
        private String save(RecordInfo reply, Element element, String parentId)
                throws SelectorParseException {
                String replyAuthorDom = detailRule.getReplyAuthor();
                if (!StringUtils.isEmpty(replyAuthorDom) && !CollectionUtils.isEmpty(element.select(replyAuthorDom))) {
                        Element userEle = element.select(replyAuthorDom).first();
                        reply.setNickname(userEle.text());
                        if (!StringUtils.isEmpty(userEle.absUrl("href"))) {
                                reply.setHome_url(userEle.absUrl("href"));
                        }
                }
                Elements contentEles = element.select(detailRule.getReplyContent());
                if (!CollectionUtils.isEmpty(contentEles)) {
                        Element contentEle = contentEles.first();
                        reply.setContent(contentEle.text());
                        reply.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, ""));
                        reply.setVoice_url(MultimediaExtractor.extractAudioUrl(contentEle));
                        reply.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
                } else {
                        return null;
                }
                String replyDateDom = detailRule.getReplyDate();
                if (!StringUtils.isEmpty(replyDateDom) && !CollectionUtils.isEmpty(element.select(replyDateDom))) {
                        String dateField = element.select(replyDateDom).first().html();
                        reply.setTimestamp(DateExtractor.extractInMilliSecs(dateField));
                }
                reply.setOriginal_id(parentId);
                String id = Md5Signatrue.generateMd5(reply.getNickname(), reply.getContent(), reply.getPic_url(), reply.getVoice_url(),
                        reply.getVideo_url());
                reply.setId(id);
                recordInfos.add(reply);
                return id;
        }

        /**
         * 保存子回复
         */
        private String saveSub(RecordInfo reply, Element element) throws SelectorParseException {
                String subReplyAuthorDom = detailRule.getSubReplyAuthor();
                if (!StringUtils.isEmpty(subReplyAuthorDom) && !CollectionUtils.isEmpty(element.select(subReplyAuthorDom))) {
                        Element userEle = element.select(subReplyAuthorDom).first();
                        reply.setNickname(userEle.text());
                        if (!StringUtils.isEmpty(userEle.absUrl("href"))) {
                                reply.setHome_url(userEle.absUrl("href"));
                        }
                }
                String subReplyContentDom = detailRule.getSubReplyContent();
                if (!StringUtils.isEmpty(subReplyContentDom) && !CollectionUtils.isEmpty(element.select(subReplyContentDom))) {
                        reply.setContent(element.select(subReplyContentDom).first().text());
                        Elements imgs = element.select(subReplyContentDom).select("img");
                        StringBuilder imgUrlSb = new StringBuilder();
                        for (Element img : imgs) {
                                imgUrlSb.append(img.attr("abs:src"));// 多个url用空格隔开
                        }
                        reply.setPic_url(imgUrlSb.toString());
                }
                reply.setVoice_url("");
                reply.setVideo_url("");
                String subReplyDate = detailRule.getSubReplyDate();
                if (!CollectionUtils.isEmpty(element.select(subReplyDate))) {
                        String dateField = element.select(subReplyDate).first().text();
                        reply.setTimestamp(DateExtractor.extractInMilliSecs(dateField));
                }
                String id = Md5Signatrue.generateMd5(reply.getNickname(), reply.getContent(), reply.getPic_url(), reply.getVoice_url(),
                        reply.getVideo_url());
                reply.setId(id);
                recordInfos.add(reply);
                return id;
        }

}
