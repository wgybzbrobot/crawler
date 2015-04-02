package com.zxsoft.crawler.plugin.parse;

import com.zxsoft.crawler.common.DetailRule;
import com.zxsoft.crawler.parse.ExtInfo;
import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.storage.WebPage;

/**
 * 新浪微博搜索解析器
 */
public class SinaWeiboSearchParser extends Parser {

	public SinaWeiboSearchParser(RecordInfo recordInfo, DetailRule detailRule,
                    long prevFetchTime, ExtInfo extInfo) {
        super(recordInfo, detailRule, prevFetchTime, extInfo);
    }

    @Override
    public FetchStatus parse() throws Exception {
	    
	    return null;
    }

}
