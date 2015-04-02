package com.zxsoft.crawler.parse;

import com.zxsoft.crawler.common.DetailRule;
import com.zxsoft.crawler.storage.RecordInfo;

public abstract class Parser extends ParseTool {

    protected RecordInfo recordInfo;
    protected final DetailRule detailRule;
    protected final long prevFetchTime;
    protected final ExtInfo extInfo;

    public Parser(RecordInfo recordInfo, DetailRule detailRule,
                    Long prevFetchTime, ExtInfo extInfo) {
        this.recordInfo = recordInfo;
        this.detailRule = detailRule;
        this.prevFetchTime = prevFetchTime;
        this.extInfo = extInfo;
    }

    public abstract FetchStatus parse() throws Exception;

}
