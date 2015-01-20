package com.zxsoft.crawler.api;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
         * 版块地址
         */
        private String url;
        private String comment;
        /**
         * 任务类型, 默认是网络巡检
         */
        private String jobType = JobType.NETWORK_INSPECT.toString();

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
         * 境内外标示
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
         * Only Constructor
         * 
         * @param site
         * @param url
         * @param fetchinterval
         * @param prevFetchTime
         */
        public Prey(int source_id, String url, String comment, String jobType, int fetchinterval, long start, long prevFetchTime,
                                        int country_code, int province_code, int city_code, State state) {
                super();
                this.source_id = source_id;
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

        public State getState() {
                return state;
        }

        public void setState(State state) {
                this.state = state;
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

        public void setUrl(String url) {
                this.url = url;
        }

        public String getComment() {
                return comment;
        }

        public void setComment(String comment) {
                this.comment = comment;
        }

        public String getJobType() {
                return jobType;
        }

        public void setJobType(String jobType) {
                this.jobType = jobType;
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
}
