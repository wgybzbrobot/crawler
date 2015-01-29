package com.zxsoft.crawler.api;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zxsoft.crawler.util.URLFormatter;

/**
 * 爬虫将根据这个对象信息进行循环抓取, 任务默认类型是<b>网络巡检</b>
 * 
 * @see Prey.urlType
 */
public class Prey implements Serializable {

        /**
	 * 
	 */
        private static final long serialVersionUID = -5812527440561239425L;

        /**
         * 网站id
         */
        private int source_id;

        /**
         * 版块id
         */
        private int sectionId;
        /**
         * 版块地址
         */
        private String url;
        /**
         * 版块名称
         */
        private String comment;
        /**
         * 任务类型, 默认是网络巡检
         */
        private JobType jobType = JobType.NETWORK_INSPECT;

        /**
         * 每隔fetchinteval(分钟)进行循环抓取
         */
        private int fetchinterval;

        /**
         * 开始时间
         */
        private long start;

        /**
         * 上次抓取时间，默认为0, 单位毫秒(ms)
         */
        private long prevFetchTime;

        /**
         * 境内外标识
         */
        private int country_code;
        /**
         * 省份代码
         */
        private int province_code;
        /**
         * 城市代码
         */
        private int city_code;

        private State state;

        /**
         * 任务状态，1表示执行，0表示暂停
         */
        public enum State {
                JOB_STOP(0), JOB_EXCUTING(1);
                private int value;

                State(int value) {
                        this.value = value;
                }

                public int getValue() {
                        return this.value;
                }
        }

        /**
         * 被执行次数
         */
        private int count;
        
        /**
         * 正在执行整个任务的机器号
         */
        private int server_id;
        
        /**
         * url地址是否动态变化， 目前支持根据时间动态编码, deault is false.
         * @see URLFormatter
         */
        private boolean autoUrl = false;
        
        private String engineUrl;
        private String keyword;
        /**
         * 关键字编码
         */
        private String encode;
        
        /**
         * 用户名
         */
        private String username;
        /**
         *  密码
         */
        private String password;
        

        /**
         * 用于初始化全网搜索
         * @param jobType
         * @param engineUrl
         * @param keyword
         */
        public Prey(JobType jobType, String engineUrl, String keyword) {
                this.jobType = jobType;
                this.engineUrl = engineUrl;
                this.keyword = keyword;
        }
        /**
         * 
         * @param jobType
         * @param engineUrl
         * @param keyword
         * @param autoUrl  url地址是否动态编码, @see autoUrl
         */
        public Prey(JobType jobType, String engineUrl, String keyword, boolean autoUrl) {
                this.jobType = jobType;
                this.engineUrl = engineUrl;
                this.keyword = keyword;
                this.autoUrl = autoUrl;
        }
        /**
         * 
         * @param jobType
         * @param engineUrl
         * @param keyword
         * @param encode  关键字编码
         */
        public Prey(JobType jobType, String engineUrl, String keyword, String encode) {
                this.jobType = jobType;
                this.engineUrl = engineUrl;
                this.keyword = keyword;
                this.encode = encode;
        }
        public Prey(JobType jobType, String engineUrl, String keyword, String encode, boolean autoUrl) {
                this.jobType = jobType;
                this.engineUrl = engineUrl;
                this.keyword = keyword;
                this.encode = encode;
                this.autoUrl = autoUrl;
        }
        
        /**
         *  用于初始化网络巡检任务
         * @param jobType
         * @param source_id
         * @param url
         * @param sectionId
         * @param comment
         * @param fetchinterval
         * @param start
         * @param prevFetchTime
         * @param country_code
         * @param province_code
         * @param city_code
         * @param state         任务状态
         */
        public Prey(JobType jobType, int source_id, String url, int sectionId, String comment,  int fetchinterval, long start, long prevFetchTime,
                                        int country_code, int province_code, int city_code, State state) {
                super();
                this.source_id = source_id;
                this.sectionId = sectionId;
                this.url = url;
                this.comment = comment;
                this.jobType = jobType;
                this.start = start;
                this.fetchinterval = fetchinterval;
                this.prevFetchTime = prevFetchTime;
                this.country_code = country_code;
                this.province_code = province_code;
                this.city_code = city_code;
                this.state = state;
        }
        public Prey(JobType jobType, int source_id, String url, int sectionId, String comment,  int fetchinterval, long start, long prevFetchTime,
                                        int country_code, int province_code, int city_code, State state, boolean autoUrl) {
                super();
                this.source_id = source_id;
                this.sectionId = sectionId;
                this.url = url;
                this.comment = comment;
                this.jobType = jobType;
                this.start = start;
                this.fetchinterval = fetchinterval;
                this.prevFetchTime = prevFetchTime;
                this.country_code = country_code;
                this.province_code = province_code;
                this.city_code = city_code;
                this.state = state;
                this.autoUrl = autoUrl;
        }
        
        /**
         * 返回Json
         */
        @Override
        public String toString() {
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                String json = gson.toJson(this);
                json = json.replaceAll("\u003d", "=");
                return json;
        }
        
        public String getEngineUrl() {
                return engineUrl;
        }

        public boolean isAutoUrl() {
                return autoUrl;
        }
        public void setAutoUrl(boolean autoUrl) {
                this.autoUrl = autoUrl;
        }
        public void setEngineUrl(String engineUrl) {
                this.engineUrl = engineUrl;
        }

        public State getState() {
                return state;
        }

        public void setState(State state) {
                this.state = state;
        }

        public int getServer_id() {
                return server_id;
        }

        public void setServer_id(int server_id) {
                this.server_id = server_id;
        }

        public String getEncode() {
                return encode;
        }
        public void setEncode(String encode) {
                this.encode = encode;
        }
        public int getCount() {
                return count;
        }

        public void setCount(int count) {
                this.count = count;
        }

        public int getSource_id() {
                return source_id;
        }

        public void setSource_id(int source_id) {
                this.source_id = source_id;
        }

        public int getCountry_code() {
                return country_code;
        }

        public void setCountry_code(int country_code) {
                this.country_code = country_code;
        }

        public int getProvince_code() {
                return province_code;
        }

        public void setProvince_code(int province_code) {
                this.province_code = province_code;
        }

        public int getCity_code() {
                return city_code;
        }

        public void setCity_code(int city_code) {
                this.city_code = city_code;
        }

        public long getStart() {
                return start;
        }

        public void setStart(long start) {
                this.start = start;
        }

        public String getUrl() {
                return url;
        }
        public int getFetchinterval() {
                return fetchinterval;
        }

        public void setFetchinterval(int fetchinterval) {
                this.fetchinterval = fetchinterval;
        }

        public long getPrevFetchTime() {
                return prevFetchTime;
        }

        public void setPrevFetchTime(long prevFetchTime) {
                this.prevFetchTime = prevFetchTime;
        }

        public String getUsername() {
                return username;
        }

        public void setUsername(String username) {
                this.username = username;
        }

        public String getPassword() {
                return password;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public int getSectionId() {
                return sectionId;
        }

        public void setSectionId(int sectionId) {
                this.sectionId = sectionId;
        }

        public String getComment() {
                return comment;
        }

        public void setComment(String comment) {
                this.comment = comment;
        }

        public JobType getJobType() {
                return jobType;
        }

        public void setJobType(JobType jobType) {
                this.jobType = jobType;
        }

        public String getKeyword() {
                return keyword;
        }

        public void setKeyword(String keyword) {
                this.keyword = keyword;
        }

        public void setUrl(String url) {
                this.url = url;
        }

}
