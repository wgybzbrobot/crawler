package com.zxsoft.crawler.parse;

import com.zxsoft.crawler.storage.WebPage;

public abstract class Parser extends ParseTool {

        // 原创记录或者父记录url
        protected String original_url;
        // 原创用户或者父用户昵称
        protected String original_name;
        // 原创记录或者记录户标题
        protected String original_title;
        // 版块id
        protected int sectionId;
        // 版块名称
        protected String comment;
        
        /*
         * 通用字段
         */
        protected String ip;
        protected int country_code;
        protected int province_code;
        protected int city_code;
        protected int location_code;
        protected String location;
        protected int source_type;
        protected int source_id;
        protected String source_name;
        protected int server_id;

        /**
         * 设置通用字段
         */
        public void setup(WebPage page) {
                this.ip = page.getIp();
                this.country_code = page.getRegion();
                this.province_code = page.getProvinceId();
                this.city_code = page.getCityId();
                this.location_code = page.getLocationCode();
                this.location = page.getLocation();
                this.source_type = page.getSource_type();
                this.source_id = page.getSource_id();
                this.source_name = page.getSource_name();
                this.server_id = page.getServer_id();
                this.sectionId = page.getSectionId();
                this.comment = page.getComment();
        }
        
        public abstract FetchStatus parse(WebPage page) throws Exception;
        
        public Parser() {
        }

        public Parser(String ip, int country_code, int province_code, int city_code, int location_code, String location) {
                super();
                this.ip = ip;
                this.country_code = country_code;
                this.province_code = province_code;
                this.city_code = city_code;
                this.location_code = location_code;
                this.location = location;
        }

        public String getIp() {
                return ip;
        }

        public void setIp(String ip) {
                this.ip = ip;
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

        public int getLocation_code() {
                return location_code;
        }

        public void setLocation_code(int location_code) {
                this.location_code = location_code;
        }

        public String getLocation() {
                return location;
        }

        public void setLocation(String location) {
                this.location = location;
        }

}
