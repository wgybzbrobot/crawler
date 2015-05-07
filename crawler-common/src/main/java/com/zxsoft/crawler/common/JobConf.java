package com.zxsoft.crawler.common;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.util.URLFormatter;

public class JobConf implements Serializable {

        private static final long serialVersionUID = -5812527440561239425L;

        // 任务类型, 默认是网络巡检
        private  JobType jobType; 
        private  String url;             // 任务url地址
        private int workerId; //  指定该任务到具体的worker机器上执行 
        private String keyword;
        private String keywordEncode; 
        /**
         * url地址是否动态变化， 目前支持根据时间动态编码, deault is false.
         * @see URLFormatter
         */
        private boolean autoUrl = false;
//        private String engineUrl;
        /**
         * 网页编码
         */
        private String encode;
        private  String source_name;  //  任务名称, 来源名称, 网站名称
        private int platform; // 平台类型，如：博客、微博、论坛等，用数字代替
        private  int source_id; // 网站id
        
        private long  jobId;
        
        private String ip;
        
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

        // 默认是执行状态
        private State state = State.JOB_EXCUTING;

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
        
        private boolean recurrence = true;
        
        /**
         * 上次抓取时间，默认为0, 单位毫秒(ms)
         */
        private long prevFetchTime;
        
        /**
         * 版块id
         */
        private  int sectionId;

        /**
         * 版块名称
         */
        private  String type;


        /**
         * 每隔fetchinteval(分钟)进行循环抓取
         */
        private int fetchinterval;

        /**
         * 开始时间
         */
        private long start;

        /**
         * 被执行次数
         */
        private long count;
        
        /**
         * 由于网络原因，导致重爬的次数
         */
        private int retry;
        
        /**
         * 用户名
         */
        private String username;
        /**
         *  密码
         */
        private String password;
        
        private Boolean auth;
        
        private   ListRule listRule;
        
        private  Set<DetailRule> detailRules;
        
        private String location;
        private int locationCode; 
        
        /**
         * 这个字段仅用于测试
         */
        private String identify_md5;
        
        private Boolean goInto = false; 
        
         public String toString () {
             Gson gson = new GsonBuilder().disableHtmlEscaping().create();
             String json = gson.toJson(this);
//             json = json.replaceAll("\u003d", "=");
             return json;
         }

        public JobConf(JobType jobType, String url, String source_name,
                        int source_id, int sectionId, String type,
                        ListRule listRule, Set<DetailRule> detailRules) {
            super();
            this.jobType = jobType;
            this.url = url;
            this.source_name = source_name;
            this.source_id = source_id;
            this.sectionId = sectionId;
            this.type = type;
            this.listRule = listRule;
            this.detailRules = detailRules;
        }

        public JobConf() {
        }

        public JobType getJobType() {
            return jobType;
        }

        public Boolean getGoInto() {
            return goInto;
        }

        public void setGoInto(Boolean goInto) {
            this.goInto = goInto;
        }

        public void setJobType(JobType jobType) {
            this.jobType = jobType;
        }

        public String getIdentify_md5() {
            return identify_md5;
        }

        public void setIdentify_md5(String identify_md5) {
            this.identify_md5 = identify_md5;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getWorkerId() {
            return workerId;
        }

        public void setWorkerId(int workerId) {
            this.workerId = workerId;
        }

        public boolean isRecurrence() {
            return recurrence;
        }

        public void setRecurrence(boolean recurrence) {
            this.recurrence = recurrence;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getKeywordEncode() {
            return keywordEncode;
        }

        public void setKeywordEncode(String keywordEncode) {
            this.keywordEncode = keywordEncode;
        }

        public boolean isAutoUrl() {
            return autoUrl;
        }

        public void setAutoUrl(boolean autoUrl) {
            this.autoUrl = autoUrl;
        }

        public String getEncode() {
            return encode;
        }

        public void setEncode(String encode) {
            this.encode = encode;
        }

        public String getSource_name() {
            return source_name;
        }

        public void setSource_name(String source_name) {
            this.source_name = source_name;
        }

        public int getPlatform() {
            return platform;
        }

        public void setPlatform(int platform) {
            this.platform = platform;
        }

        public int getSource_id() {
            return source_id;
        }

        public void setSource_id(int source_id) {
            this.source_id = source_id;
        }

        public long getJobId() {
            return jobId;
        }

        public void setJobId(long jobId) {
            this.jobId = jobId;
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

        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }

        public long getPrevFetchTime() {
            return prevFetchTime;
        }

        public void setPrevFetchTime(long prevFetchTime) {
            this.prevFetchTime = prevFetchTime;
        }

        public int getSectionId() {
            return sectionId;
        }

        public void setSectionId(int sectionId) {
            this.sectionId = sectionId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getFetchinterval() {
            return fetchinterval;
        }

        public void setFetchinterval(int fetchinterval) {
            this.fetchinterval = fetchinterval;
        }

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
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

        public Boolean getAuth() {
            return auth;
        }

        public void setAuth(Boolean auth) {
            this.auth = auth;
        }

        public ListRule getListRule() {
            return listRule;
        }

        public void setListRule(ListRule listRule) {
            this.listRule = listRule;
        }

        public Set<DetailRule> getDetailRules() {
            return detailRules;
        }

        public void setDetailRules(Set<DetailRule> detailRules) {
            this.detailRules = detailRules;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getRetry() {
            return retry;
        }

        public void setRetry(int retry) {
            this.retry = retry;
        }

        public int getLocationCode() {
            return locationCode;
        }

        public void setLocationCode(int locationCode) {
            this.locationCode = locationCode;
        }
        
        public void merge(JobConf a) throws IllegalArgumentException, IllegalAccessException {
            Field[] fields =  JobConf.class.getDeclaredFields();
//            System.out.println(fields.length);
            for (Field field : fields) {
                if (null == field.get(this) || field.get(this).equals(0) )
                    field.set(this, field.get(a));
                
            }
        }
        
        public void merge(Object obj, Object update){
            if(!obj.getClass().isAssignableFrom(update.getClass())){
                return;
            }

            Method[] methods = obj.getClass().getMethods();

            for(Method fromMethod: methods){
                if(fromMethod.getDeclaringClass().equals(obj.getClass())
                        && fromMethod.getName().startsWith("get")){

                    String fromName = fromMethod.getName();
                    String toName = fromName.replace("get", "set");

                    try {
                        Method toMetod = obj.getClass().getMethod(toName, fromMethod.getReturnType());
                        Object value = fromMethod.invoke(update, (Object[])null);
                        if(value != null){
                            toMetod.invoke(obj, value);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 
                }
            }
        }
        
        public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            JobConf a = new JobConf();
            a.setUrl("http://www.baidu.com");
            
            JobConf b = new JobConf();
            b.setAutoUrl(false);
            b.setIp("192.168.3.23");
            b.setType("baidu");
            b.setSectionId(11);
            
//            a.merge(b);
           a.merge(a, b);
            
            System.out.println(a.toString());
            
        }
}
