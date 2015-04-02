package com.zxsoft.crawler.common;

public class CrawlerException extends Exception {

    private static final long serialVersionUID = 5591402881944908914L;

    public enum ErrorCode {
        SUCCESS(10000),
        CONF_ERROR(20000),
        NETWORK_ERROR(30000),
        SYSTEM_ERROR(40000),
        OUTPUT_DATA_ERROR(50000);
        public final int code;

        private ErrorCode(int c) {
            code = c;
        }
    }

    int code = 0;

    public CrawlerException(ErrorCode code, String msg) {
        super(msg);
        this.code = code.code;
    }

    public CrawlerException(ErrorCode code, String msg, Throwable th) {
        super(msg, th);
        this.code = code.code;
    }

    public CrawlerException(ErrorCode code, Throwable th) {
        super(th);
        this.code = code.code;
    }
    
    public int code() { return code; }

}
