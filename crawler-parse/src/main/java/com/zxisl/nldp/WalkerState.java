package com.zxisl.nldp;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WalkerState {

        private static Logger LOG = LoggerFactory.getLogger(WalkerState.class);
        
        private GregorianCalendar calendar;
        
        public WalkerState() {
                calendar = new GregorianCalendar();
        }
        
        /**
         * 设置月,日, 年份为当年
         * @param m
         * @param d
         */
        public void setMD(String m, String d) {
                calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                calendar.set(Calendar.MONTH, Integer.valueOf(m) - 1);
                calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(d));
        }
        /**
         * 设置年月日
         * @param y
         * @param m
         * @param d
         */
        public void setYMD(String y, String m, String d) {
                calendar.set(Calendar.YEAR, Integer.valueOf(y));
                calendar.set(Calendar.MONTH, Integer.valueOf(m) - 1);
                calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(d));
        }

        /**
         * 设置时分秒
         * @param h
         * @param m
         * @param s
         */
        public void setHMS(String h, String m, String s) {
                calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(h));
                calendar.set(Calendar.MINUTE, Integer.valueOf(m));
                calendar.set(Calendar.SECOND, Integer.valueOf(s));
        }
        /**
         * 设置时分
         * @param h
         * @param m
         */
        public void setHM(String h, String m) {
                calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(h));
                calendar.set(Calendar.MINUTE, Integer.valueOf(m));
        }
        
//        public void setTime(String hour, String minute, String seconds) {
//                int h = hour == null ? 0 :Integer.valueOf(hour);
//                int min = minute == null ? 0 : Integer.valueOf(minute);
//                int s = seconds == null ? 0 : Integer.valueOf(seconds);
//                
//                calendar.set(Calendar.HOUR_OF_DAY, h);
//                calendar.set(Calendar.MINUTE, min);
//                calendar.set(Calendar.SECOND, s);
//        }
        
//        public void setYear(String year) {
//                int y = Integer.valueOf(year);
//                calendar.set(Calendar.YEAR, y);
//        }
//        public void setMonth(String month) {
//                int m = Integer.valueOf(month);
//                calendar.set(Calendar.MONTH, m - 1);
//        }
//        public void setDay(String day) {
//                int d = Integer.valueOf(day);
//                calendar.set(Calendar.DAY_OF_MONTH, d);
//        }
        public void setToYesterday() {
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1);
        }
        /**
         * 设置日期为前天
         */
        public void setToBefore() {
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 2);
        }
        /**
         * h小时前
         * @param h
         */
        public void setHourAgo(String h) {
                calendar.set(Calendar.HOUR_OF_DAY,  Calendar.getInstance().get(Calendar.HOUR_OF_DAY) - Integer.valueOf(h.trim()));
        }
        /**
         * m分钟前
         * @param m
         */
        public void setMinuteAgo(String m) {
                calendar.set(Calendar.MINUTE,  Calendar.getInstance().get(Calendar.MINUTE) - Integer.valueOf(m.trim()));
        }
        /**
         * s秒前
         * @param s
         */
        public void setSecondsAgo(String s) {
                calendar.set(Calendar.SECOND,  Calendar.getInstance().get(Calendar.SECOND) - Integer.valueOf(s.trim()));
        }
        
        
//        public void setHour(String hour) {
//                calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
//        }
//        public void setMin(String min) {
//                calendar.set(Calendar.MINUTE, Integer.valueOf(min));
//        }
//        public void setSec(String sec) {
//                calendar.set(Calendar.SECOND, Integer.valueOf(sec));
//        }
        
        
        public Date getDate() {
                return calendar.getTime();
        }
        public long getTimeInMillis () {
                return calendar.getTimeInMillis();
        }
}
