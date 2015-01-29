package com.zxsoft.crawler.api;

/**
 * 参数的命名必须和{@link Prey}一样
 * @author xiayun
 * @see Prey
 *
 */
public interface Params {

        public static final String CONF_ID = "conf";
        public static final String PROP_NAME = "prop";
        public static final String PROP_VALUE = "value";
        public static final String PROPS = "props";
        public static final String CRAWL_ID = "crawl";
        public static final String JOB_ID = "job";
        public static final String ARGS = "args";
        public static final String CMD = "cmd";
        public static final String FORCE = "force";

        public static final String JOB_CMD_STOP = "stop";
        public static final String JOB_CMD_ABORT = "abort";
        public static final String JOB_CMD_GET = "get";
        public static final String JOB_STATE = "state";

        public static final String JOB_TYPE = "jobType";
        public static final String URL = "url";
        public static final String KEYWORD = "keyword";
        public static final String SITE = "site";
        public static final String Interval = "interval";
        public static final String PREV_FETCH_TIME = "prevFetchTime";

        public static final String COUNTRY_CODE = "country_code";
        public static final int COUNTRY_DOMESTIC = 1;
        public static final int COUNTRY_OVERSEA = 0;

        public static final String PROVINCE_CODE = "province_code";
        public static final String CITY_CODE = "city_code";
        public static final String LOCATION_CODE = "location_code";
        public static final String SOURCE_ID = "source_id";
        public static final String SOURCE_TYPE = "source_type";
        public static final String SERVER_ID = "server_id";

        public static final String SECTION_ID = "sectionId";
        public static final String COMMENT = "comment";
        /** 搜索引擎 */
        public static final String ENGINE_URL = "engineUrl";

}
