package com.zxsoft.crawler.util;

import java.text.ParseException;

import org.junit.Test;
import org.thinkingcloud.framework.util.Assert;

public class UtilsTest {

    @Test
    public void testFormatDate() throws ParseException {

            String str0 = "2014-4-16 21:08:48";
            String str1 = "发表于 2014-4-16 21:08:48";
            String str2 = "发表于 4 天前";
            String str3 = "发表于 3 小时前 ";
            String str4 = "发表于 3 分钟前 ";
            String str5 = "发表于 3 秒前 ";
            String str6 = "发表于 前天08:27 ";
            String str7 = "发表于 今天08:27 ";
            String str8 = "2014年05月22日 09:08";
            String str9 = "2014年09月15日 21:19:26"; 
            String str10 = "2014-6-30 10:08";
            String s11 = "发表于 2013-3-9 18:51";
            String s2 = "2014-11-08 12:41:32";
            String s3 = "博讯北京时间2014年11月01日 转载";
            String s4 = "发表于 7 天前";
            String s5 = "今天 13:40:01";
            String s6 = "发表于 昨天 11:25";
            String s7 = "发表于 半小时前";
//            Assert.isTrue(s4.equals(str2));
            System.out.println(s7 + "\t\t" +  Utils.formatDate(s7));
            System.out.println(s6 + "\t\t" +  Utils.formatDate(s6));
            System.out.println(s5 + "\t\t" +  Utils.formatDate(s5));
            System.out.println(s4 + "\t\t" +  Utils.formatDate(s4));
            System.out.println(s3 + "\t\t" +  Utils.formatDate(s3));
            System.out.println(s2 + "\t\t" +  Utils.formatDate(s2));
            System.out.println(s11 + "\t\t" +  Utils.formatDate(s11));
            System.out.println(str0 + "\t\t" + Utils.formatDate(str0));
            System.out.println(str1 + "\t\t" + Utils.formatDate(str1));
            System.out.println(str2 + "\t\t" + Utils.formatDate(str2));
            System.out.println(str3 + "\t\t" + Utils.formatDate(str3));
            System.out.println(str4 + "\t\t" + Utils.formatDate(str4));
            System.out.println(str5 + "\t\t" + Utils.formatDate(str5));
            System.out.println(str6 + "\t\t" + Utils.formatDate(str6));
            System.out.println(str7 + "\t\t" + Utils.formatDate(str7));
            System.out.println(str8 + "\t\t" + Utils.formatDate(str8));
            System.out.println(str9 + "\t\t" + Utils.formatDate(str9));
            System.out.println(str10 + "\t\t" +Utils.formatDate(str10));
    }
    
    @Test
    public void testIsNum() {
    	Assert.isTrue(Utils.isNum("3"));
    	Assert.isTrue(Utils.isNum("33"));
    	Assert.isTrue(Utils.isNum("3njb"));
    	Assert.isTrue(Utils.isNum(""));
    	Assert.isTrue(Utils.isNum(null));
    }

    @Test
    public void testExtractNum() {
    	Assert.isTrue(Utils.extractNum("Hello3Two") == 3);
    	Assert.isTrue(Utils.extractNum("33 HelloTwo") == 33);
    	Assert.isTrue(Utils.extractNum("HelloTwo 4 5") == 4);
    }
}
