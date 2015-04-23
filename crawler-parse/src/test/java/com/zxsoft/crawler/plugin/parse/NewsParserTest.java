package com.zxsoft.crawler.plugin.parse;

import org.junit.Test;

import com.zxsoft.crawler.common.DetailRule;
import com.zxsoft.crawler.parse.ExtInfo;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.storage.RecordInfo;

public class NewsParserTest {

    @Test
    public void test() throws Exception {
        // String replyNum

        String replyNum = "";
        String reviewNum= "";
        String forwardNum= "";
        String sources= "";
        Boolean fetchorder = true;
        Boolean ajax = false;
        String master = "div#bodyContent";
        String author ="";
        String date="ul.smallList li";
        String content = "div.col3";
        String reply = "";
        String replyAuthor = "";
        String replyDate = "";
        String replyContent = "";
        String subReply = "";
        String subReplyAuthor = "";
        String subReplyDate = "";
        String subReplyContent = "";
        DetailRule detailRule = new DetailRule("http://www.dw.de", replyNum, reviewNum,
                        forwardNum, sources, fetchorder, ajax, master, author, date,
                        content, reply, replyAuthor, replyDate, replyContent, subReply,
                        subReplyAuthor, subReplyDate, subReplyContent);
        
        RecordInfo recordInfo = new RecordInfo();
        recordInfo.setUrl("http://test");
        recordInfo.setOriginal_url("http://www.dw.de/%E8%B5%B0%E5%87%BA%E5%8D%97%E9%9D%9E-%E9%80%83%E7%A6%BB%E6%8E%92%E5%A4%96%E6%9A%B4%E5%8A%9B/a-18397052");
        
        Parser parser = new NewsParser(recordInfo, detailRule, 0L, new ExtInfo());
        
        parser.parse();
    }
}
