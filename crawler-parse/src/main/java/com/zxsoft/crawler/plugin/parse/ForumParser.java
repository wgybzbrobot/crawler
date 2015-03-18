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

import com.zxisl.commons.utils.Assert;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.FetchStatus.Status;
import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.parse.Platform;
import com.zxsoft.crawler.plugin.parse.ext.DateExtractor;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
import com.zxsoft.crawler.protocol.util.Md5Signatrue;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.store.OutputException;
import com.zxsoft.crawler.util.Utils;

/**
 * 论坛类解析器
 * <p>
 * 逻辑:
 * <ol>
 * <li>解析主帖</li>
 * <li>若从最后一页抓取，则调转到最后一页, 倒序集合</li>
 * <li>解析页面</li>
 * <li>翻页, 解析页面</li>
 * </ol>
 */
public class ForumParser extends Parser {

        private static Logger LOG = LoggerFactory.getLogger(ForumParser.class);

        private List<RecordInfo> recordInfos = new LinkedList<RecordInfo>();

        public List<RecordInfo> getRecordInfos() {
                return recordInfos;
        }

        /**
         * 解析页面入口
         */
        public FetchStatus parse(WebPage page) throws Exception {
                Assert.notNull(page, "Page is null");
                String mainUrl = page.getBaseUrl();
                long prevFetchTime = page.getPrevFetchTime();
                DetailConf detailConf = confDao.getDetailConf(page.getListUrl(), Utils.getHost(mainUrl));
                if (detailConf == null) {
                        LOG.error("没有详细页配置: " + mainUrl);
                        return new FetchStatus(mainUrl, 41, Status.CONF_ERROR);
                }
                page.setAjax(detailConf.isAjax());
                ProtocolOutput _output = fetch(page);
                if (!_output.getStatus().isSuccess()) {
                        return new FetchStatus(mainUrl, 51, Status.PROTOCOL_FAILURE);
                }

                Document mainDoc = _output.getDocument();
                page.setDocument(mainDoc);

                /*
                 * 解析主贴
                 */
                RecordInfo info = new RecordInfo(mainUrl, comment, Platform.PLATFORM_FORUM, ip, country_code, province_code, city_code,
                                                location_code, location, source_id, source_name, server_id, source_type);
                info.setTitle(page.getTitle());
                info.setUpdate_time(page.getUpdateTime());
                
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
                        if (!StringUtils.isEmpty(authorDom) && !CollectionUtils.isEmpty(masterEle.select(authorDom))) {
                                Element userEle = masterEle.select(authorDom).first();
                                info.setNickname(userEle.text());
                                if (!StringUtils.isEmpty(userEle.absUrl("href"))) {
                                        info.setHome_url(userEle.absUrl("href"));
                                }
                        }
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
                        
                        // 原创记录或者父记录url
                        original_url = mainUrl;
                         // 原创用户或者父用户昵称
                        original_name = info.getNickname();
                         // 原创记录或者记录户标题
                        original_title = info.getTitle();
                         // 版块名称
//                        type = ;
                        
                        getRecordInfos().add(info);
                } else {
                        return new FetchStatus(mainUrl, 42, Status.CONF_ERROR);
                }

                /*
                 * 解析回复
                 */
                Document _doc = mainDoc;
                String currentUrl = mainUrl, newPageUrl = "";
                _output = null;
                if (!detailConf.isFetchorder()) { // 从第一页
                        int pageNum = 1;
                        do {
                                boolean isContinue = parsePage(prevFetchTime, _doc, mainUrl, currentUrl, detailConf);
                                if (!isContinue) {
                                        break;
                                }
                                pageNum++;
                                WebPage np = page.clone();
                                np.setDocument(_doc);
                                np.setBaseUrl(_doc.location());
                                _output = fetchNextPage(pageNum, np);
                                if (_output == null || _output.getStatus().getCode() != STATUS_CODE.SUCCESS)
                                        break;
                                _doc = _output.getDocument();
                                currentUrl = newPageUrl;
                        } while (!StringUtils.isEmpty(newPageUrl));

                } else { // 从最后一夜
                        WebPage np = page.clone(); // 跳转到最后一页
                        page.setDocument(_doc);
                        page.setBaseUrl(_doc.location());
                        _output = fetchLastPage(np);
                        Document lastDoc = null;
                        if (_output == null || _output.getStatus().getCode() != STATUS_CODE.SUCCESS
                                                        || (lastDoc = _output.getDocument()) == null) {
                                parsePage(prevFetchTime, _doc, mainUrl, currentUrl, detailConf);
                        } else {
                                _doc = lastDoc;
                                while (true) {
                                        if (_doc == null || StringUtils.isEmpty(currentUrl = _doc.location()))
                                                break;
                                        boolean isContinue = parsePage(prevFetchTime, _doc, mainUrl, currentUrl, detailConf);
                                        if (!isContinue) {
                                                break;
                                        }
                                        // 获取上一页
                                        np.setDocument(_doc);
                                        np.setBaseUrl(_doc.location());
                                        _output = fetchPrevPage(-1, np);
                                        if (_output == null || _output.getStatus().getCode() != STATUS_CODE.SUCCESS)
                                                break;
                                        _doc = _output.getDocument();
                                }
                        }
                }

                int count = 0;
                try {
                        // List<RecordInfo> list = getRecordInfos();
                        // for (RecordInfo recordInfo : list) {
                        // LOG.debug(recordInfo.toString());
                        // }
                        count = indexWriter.write(getRecordInfos());
                } catch (OutputException e) {
                        throw new OutputException(mainUrl + " 数据输出失败.");
                        // return new FetchStatus(mainUrl, 61,
                        // Status.OUTPUT_FAILURE);
                }
                return new FetchStatus(mainUrl, 21, Status.SUCCESS, count);
        }

        /**
         * 解析一页
         */
        private boolean parsePage(long prevFetchTime, Document doc, String mainUrl, String currentUrl, DetailConf detailConf)
                                        throws SelectorParseException {
                // 没有回复
                if (StringUtils.isEmpty(detailConf.getReply())) {
                        return false;
                }
                Elements replyEles = doc.select(detailConf.getReply());
                Collections.reverse(replyEles);
                for (Element element : replyEles) {
                        RecordInfo reply = new RecordInfo(currentUrl, comment,Platform.PLATFORM_REPLY, ip, country_code, province_code, city_code,
                                                        location_code, location, source_id, source_name, server_id, source_type);
                        reply.setOriginal_url(mainUrl);
                        // 保存回复
                        String id = save(reply, element, null, detailConf);
                        // 由于时间的误差，将抓取时间拓展１分钟
                        if (reply.getTimestamp() != 0 && reply.getTimestamp() + 60000L < prevFetchTime)
                                return false;
                        if (id == null)
                                continue;
                        // 解析子回复
                        String subReplyDom = detailConf.getSubReply();
                        if (StringUtils.isEmpty(subReplyDom))
                                continue;
                        Elements subReplyEles = element.select(subReplyDom);
                        String parentId = id;
                        if (!CollectionUtils.isEmpty(subReplyEles)) {
                                for (Element ele : subReplyEles) {
                                        RecordInfo subReply = new RecordInfo(currentUrl,comment, Platform.PLATFORM_REPLY, ip, country_code,
                                                                        province_code, city_code, location_code, location, source_id, source_name,
                                                                        server_id, source_type);
                                        subReply.setOriginal_id(parentId);
                                        subReply.setOriginal_url(mainUrl);
                                        saveSub(subReply, ele, detailConf);
                                }
                        }
                }
                return true;
        }

        /**
         * 保存回复
         */
        private String save(RecordInfo reply, Element element, String parentId, DetailConf detailConf) throws SelectorParseException {
                String replyAuthorDom = detailConf.getReplyAuthor();
                if (!StringUtils.isEmpty(replyAuthorDom) && !CollectionUtils.isEmpty(element.select(replyAuthorDom))) {
                        Element userEle = element.select(replyAuthorDom).first();
                        reply.setNickname(userEle.text());
                        if (!StringUtils.isEmpty(userEle.absUrl("href"))) {
                                reply.setHome_url(userEle.absUrl("href"));
                        }
                }
                Elements contentEles = element.select(detailConf.getReplyContent());
                if (!CollectionUtils.isEmpty(contentEles)) {
                        Element contentEle = contentEles.first();
                        reply.setContent(contentEle.text());
                        reply.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, ""));
                        reply.setVoice_url(MultimediaExtractor.extractAudioUrl(contentEle));
                        reply.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
                } else {
                        return null;
                }
                String replyDateDom = detailConf.getReplyDate();
                if (!StringUtils.isEmpty(replyDateDom) && !CollectionUtils.isEmpty(element.select(replyDateDom))) {
                        String dateField = element.select(replyDateDom).first().html();
                        reply.setTimestamp(DateExtractor.extractInMilliSecs(dateField));
                }
                reply.setOriginal_id(parentId);
                String id = Md5Signatrue.generateMd5(reply.getNickname(), reply.getContent(), reply.getPic_url(), reply.getVoice_url(),
                                                reply.getVideo_url());
                reply.setId(id);
                reply.setLasttime(System.currentTimeMillis());
                getRecordInfos().add(reply);
                return id;
        }

        /**
         * 保存子回复
         */
        private String saveSub(RecordInfo reply, Element element, DetailConf detailConf) throws SelectorParseException {
                String subReplyAuthorDom = detailConf.getSubReplyAuthor();
                if (!StringUtils.isEmpty(subReplyAuthorDom) && !CollectionUtils.isEmpty(element.select(subReplyAuthorDom))) {
                        Element userEle = element.select(subReplyAuthorDom).first();
                        reply.setNickname(userEle.text());
                        if (!StringUtils.isEmpty(userEle.absUrl("href"))) {
                                reply.setHome_url(userEle.absUrl("href"));
                        }
                }
                String subReplyContentDom = detailConf.getSubReplyContent();
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
                String subReplyDate = detailConf.getSubReplyDate();
                if (!CollectionUtils.isEmpty(element.select(subReplyDate))) {
                        String dateField = element.select(subReplyDate).first().text();
                        reply.setTimestamp(DateExtractor.extractInMilliSecs(dateField));
                }
                String id = Md5Signatrue.generateMd5(reply.getNickname(), reply.getContent(), reply.getPic_url(), reply.getVoice_url(),
                                                reply.getVideo_url());
                reply.setId(id);
                reply.setLasttime(System.currentTimeMillis());
                getRecordInfos().add(reply);
                return id;
        }

}
