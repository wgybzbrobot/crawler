package com.zxsoft.crawler.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.StringUtils;

/**
 * 格式化url, 有些版块url地址是动态变化的, 例如根据日期变化的, http://www.secretchina.com/news/yy/MM/dd.
 * 本类的目的是生成实际的url.
 * 
 * @author xiayun
 *
 */
public class URLFormatter {

        private static Logger LOG = LoggerFactory.getLogger(URLFormatter.class);

        /**
         * 
         * @param url
         * @return
         * @throws UnsupportedEncodingException
         */
        public static String format(String url) throws UnsupportedEncodingException {
                Date date = new Date();
                int count = StringUtils.countOccurrencesOf(url, "%t");
                if (count == 0) {
                        return url;
                }
                List<Date> list = new ArrayList<Date>();
                for (int i = 0; i < count; i++) {
                        list.add(date);
                }

                Date[] strs = list.toArray(new Date[] {});
                try {
                        url = String.format(url, strs);
                } catch (Exception e) {
                        LOG.error("Format url error.", e);
                }
                return url;
        }
        
        /**
         * 
         * @param url
         * @param keyword 关键字
         * @return
         * @throws UnsupportedEncodingException
         */
        public static String format(String url, String keyword) throws UnsupportedEncodingException {
                Date date = new Date();
                int count = StringUtils.countOccurrencesOf(url, "%t");
                if (count == 0) {
                        return url;
                }
                List<Object> list = new ArrayList<Object>();
                list.add(keyword);
                for (int i = 0; i < count; i++) {
                        list.add(date);
                }
                
                Object[] strs = list.toArray(new Object[] {});
                try {
                        url = String.format(url, strs);
                } catch (Exception e) {
                        LOG.error("Format url error.", e);
                }
                return url;
        }

}
