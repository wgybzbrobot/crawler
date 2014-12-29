package com.zxsoft.crawler.plugin.parse.ext;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zxisl.nldp.Nldp;

public class DateExtractor {

        private static Logger LOG = LoggerFactory.getLogger(DateExtractor.class);
        
        public String preHandle(String text) {
                String regExp = "(\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{2}:\\d{2} |  \\d{1,2}\\s*天前 | \\d{1,2}\\s*小时前)";
                Pattern pattern = Pattern.compile(regExp);
                Matcher matcher = pattern.matcher(text);
                StringBuilder sb = new StringBuilder();
                while(matcher.find())
                       sb.append(matcher.group());
                
                return sb.toString();
        }
        
        
        public static Date extract(String text)  {
                Date date = null;
                try {
                        Nldp nldp = new Nldp(text);
                        date = nldp.extractDate();
                } catch (Exception e) {
                        LOG.error("解析时间失败:" + text, e);
                }
                
                return date;
        }
        
        public static long extractInMilliSecs(String text) {
                Date date = extract(text);
                if (date != null) {
                        return date.getTime() ;
                }
                return 0L;
        }
}
