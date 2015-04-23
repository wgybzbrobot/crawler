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
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.FetchStatus.Status;
import com.zxsoft.crawler.parse.ExtInfo;
import com.zxsoft.crawler.parse.MultimediaExtractor;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.parse.Platform;
import com.zxsoft.crawler.plugin.parse.ext.DateExtractor;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
import com.zxsoft.crawler.protocol.util.Md5Signatrue;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;
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

    public ForumParser(RecordInfo recordInfo, DetailRule detailRule, Long prevFetchTime,
                    ExtInfo extInfo) {
        super(recordInfo, detailRule, prevFetchTime, extInfo);
    }

    /**
     * 解析页面入口
     * 
     * @throws CrawlerException
     */
    public FetchStatus parse() throws CrawlerException {
        if (detailRule == null)
            throw new CrawlerException(ErrorCode.CONF_ERROR, "Detail page rule is null.");
        String mainUrl = recordInfo.getOriginal_url();

        WebPage page = new WebPage(mainUrl, detailRule.getAjax(), null);
        ProtocolOutput _output = fetch(page);

        if (!_output.getStatus().isSuccess()) {
            return new FetchStatus(mainUrl, 51, Status.PROTOCOL_FAILURE);
        }

        Document mainDoc = _output.getDocument();
        page.setDocument(mainDoc);

        /*
         * 解析主贴
         */
        RecordInfo info = recordInfo.clone();

        info.setUrl(mainUrl);
        info.setPlatform(Platform.PLATFORM_FORUM);

        String replyNumDom = detailRule.getReplyNum();
        if (!StringUtils.isEmpty(replyNumDom)
                        && !CollectionUtils.isEmpty(mainDoc.select(replyNumDom)))
            info.setComment_count(Utils.extractNum(mainDoc.select(replyNumDom).first()
                            .text()));

        String reviewNumDom = detailRule.getReviewNum();
        if (!StringUtils.isEmpty(reviewNumDom)
                        && !CollectionUtils.isEmpty(mainDoc.select(reviewNumDom)))
            info.setRead_count(Integer.valueOf(Utils.extractNum(mainDoc
                            .select(reviewNumDom).first().text())));

        Elements masterEles = mainDoc.select(detailRule.getMaster());
        if (!CollectionUtils.isEmpty(masterEles)) {
            Element masterEle = masterEles.first();
            String authorDom = detailRule.getAuthor();
            if (!StringUtils.isEmpty(authorDom)
                            && !CollectionUtils.isEmpty(masterEle.select(authorDom))) {
                Element userEle = masterEle.select(authorDom).first();
                info.setNickname(userEle.text());
                info.setOriginal_name(info.getNickname());
                recordInfo.setOriginal_name(info.getNickname());
                if (!StringUtils.isEmpty(userEle.absUrl("href"))) {
                    info.setHome_url(userEle.absUrl("href"));
                }
            }
            Elements contentEles = masterEle.select(detailRule.getContent());
            if (!CollectionUtils.isEmpty(contentEles)) {
                Element contentEle = contentEles.first();
                info.setContent(contentEle.text());
                info.setPic_url(MultimediaExtractor.extractImgUrl(contentEle, ""));
                info.setVoice_url(MultimediaExtractor.extractAudioUrl(contentEle));
                info.setVideo_url(MultimediaExtractor.extractVideoUrl(contentEle));
            }

            if (extInfo.getTimestamp() == 0) {
                String dateDom = detailRule.getDate();
                if (!StringUtils.isEmpty(dateDom)
                                && !CollectionUtils.isEmpty(masterEle.select(dateDom))) {
                    String dateField = masterEle.select(dateDom).first().html();
                        info.setTimestamp(DateExtractor.extractInMilliSecs(dateField));
                } 
            } else {
                info.setTimestamp(extInfo.getTimestamp());
            }

            String original_id = Md5Signatrue.generateMd5(extInfo.getIdentify_md5(), info.getNickname(),
                            info.getContent(), info.getPic_url(), info.getVoice_url(),
                            info.getVideo_url());
            info.setOriginal_id(original_id);
            info.setId(original_id);
            if (info.getTimestamp() == 0L)
                info.setTimestamp(info.getLasttime());
            recordInfo.setOriginal_id(original_id);
            recordInfos.add(info);
        } else {
            return new FetchStatus(mainUrl, 42, Status.CONF_ERROR);
        }

        /*
         * 解析回复
         */
        Document _doc = mainDoc;
        String currentUrl = mainUrl, newPageUrl = "";
        _output = null;
        if (!detailRule.getFetchorder()) { // 从第一页
            int pageNum = 1;
            do {
                boolean isContinue = parsePage(prevFetchTime, _doc, mainUrl, currentUrl,
                                detailRule);
                if (!isContinue) {
                    break;
                }
                pageNum++;
                WebPage np = new WebPage(_doc.location(), detailRule.getAjax(), _doc);
                _output = fetchNextPage(pageNum, np);
                if (_output == null
                                || _output.getStatus().getCode() != STATUS_CODE.SUCCESS)
                    break;
                _doc = _output.getDocument();
                currentUrl = newPageUrl;
            } while (!StringUtils.isEmpty(newPageUrl));

        } else { // 从最后一页
            WebPage np = new WebPage(_doc.location(), detailRule.getAjax(), _doc); // 跳转到最后一页
            _output = fetchLastPage(np);
            Document lastDoc = null;
            if (_output == null || _output.getStatus().getCode() != STATUS_CODE.SUCCESS
                            || (lastDoc = _output.getDocument()) == null) {
                parsePage(prevFetchTime, _doc, mainUrl, currentUrl, detailRule);
            } else {
                _doc = lastDoc;
                while (true) {
                    if (_doc == null || StringUtils.isEmpty(currentUrl = _doc.location()))
                        break;
                    boolean isContinue = parsePage(prevFetchTime, _doc, mainUrl,
                                    currentUrl, detailRule);
                    if (!isContinue) {
                        break;
                    }
                    // 获取上一页
                    np.setDocument(_doc);
                    np.setUrl(_doc.location());
                    _output = fetchPrevPage(-1, np);
                    if (_output == null
                                    || _output.getStatus().getCode() != STATUS_CODE.SUCCESS)
                        break;
                    _doc = _output.getDocument();
                }
            }
        }

        int count = 0;
        // List<RecordInfo> list = getRecordInfos();
        // for (RecordInfo recordInfo : list) {
        // LOG.debug(recordInfo.toString());
        // }
        count = indexWriter.write(recordInfos);
        return new FetchStatus(mainUrl, 21, Status.SUCCESS, count);
    }

    /**
     * 解析一页
     * 
     * @throws CrawlerException
     */
    private boolean parsePage(long prevFetchTime, Document doc, String mainUrl,
                    String currentUrl, DetailRule detailRule)
                    throws SelectorParseException, CrawlerException {
        // 没有回复
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
            String id = save(reply, element, null, detailRule);
            // 由于时间的误差，将抓取时间拓展１分钟
            if (reply.getTimestamp() != 0
                            && reply.getTimestamp() + 60000L < prevFetchTime)
                return false;
            if (id == null)
                continue;
            // 解析子回复
            String subReplyDom = detailRule.getSubReply();
            if (StringUtils.isEmpty(subReplyDom))
                continue;
            Elements subReplyEles = element.select(subReplyDom);
            String parentId = id;
            if (!CollectionUtils.isEmpty(subReplyEles)) {
                for (Element ele : subReplyEles) {
                    RecordInfo subReply = recordInfo.clone();
                    subReply.setUrl(currentUrl);
                    subReply.setPlatform(Platform.PLATFORM_REPLY);
                    subReply.setOriginal_id(parentId);
                    subReply.setOriginal_url(mainUrl);
                    saveSub(subReply, ele, detailRule);
                }
            }
        }
        return true;
    }

    /**
     * 保存回复
     * 
     * @throws CrawlerException
     */
    private String save(RecordInfo reply, Element element, String parentId,
                    DetailRule detailRule) throws SelectorParseException,
                    CrawlerException {
        String replyAuthorDom = detailRule.getReplyAuthor();
        if (!StringUtils.isEmpty(replyAuthorDom)
                        && !CollectionUtils.isEmpty(element.select(replyAuthorDom))) {
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
            // LOG.error("");
            throw new CrawlerException(ErrorCode.CONF_ERROR,
                            "Cannot get reply content from " + reply.getUrl() + " using rule:"
                                            + detailRule.getReplyContent() + ", Html: "
                                            + element.html());
            // return null;
        }
        String replyDateDom = detailRule.getReplyDate();
        if (!StringUtils.isEmpty(replyDateDom)
                        && !CollectionUtils.isEmpty(element.select(replyDateDom))) {
            String dateField = element.select(replyDateDom).first().html();
            reply.setTimestamp(DateExtractor.extractInMilliSecs(dateField));
        }
        reply.setOriginal_id(parentId);
        String id = Md5Signatrue.generateMd5(extInfo.getIdentify_md5(),reply.getNickname(), reply.getContent(),
                        reply.getPic_url(), reply.getVoice_url(), reply.getVideo_url());
        reply.setId(id);
        if (reply.getTimestamp() == 0L)
            reply.setTimestamp(reply.getLasttime());
        recordInfos.add(reply);
        return id;
    }

    /**
     * 保存子回复
     */
    private String saveSub(RecordInfo reply, Element element, DetailRule detailRule)
                    throws SelectorParseException {
        String subReplyAuthorDom = detailRule.getSubReplyAuthor();
        if (!StringUtils.isEmpty(subReplyAuthorDom)
                        && !CollectionUtils.isEmpty(element.select(subReplyAuthorDom))) {
            Element userEle = element.select(subReplyAuthorDom).first();
            reply.setNickname(userEle.text());
            if (!StringUtils.isEmpty(userEle.absUrl("href"))) {
                reply.setHome_url(userEle.absUrl("href"));
            }
        }
        String subReplyContentDom = detailRule.getSubReplyContent();
        if (!StringUtils.isEmpty(subReplyContentDom)
                        && !CollectionUtils.isEmpty(element.select(subReplyContentDom))) {
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
        String id = Md5Signatrue.generateMd5(extInfo.getIdentify_md5(),reply.getNickname(), reply.getContent(),
                        reply.getPic_url(), reply.getVoice_url(), reply.getVideo_url());
        reply.setId(id);
        if (reply.getTimestamp() == 0L)
            reply.setTimestamp(reply.getLasttime());
        recordInfos.add(reply);
        return id;
    }

}
