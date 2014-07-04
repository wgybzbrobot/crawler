package com.zxsoft.carson.util;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

    @Test
    public void testFormatDate() {

            String str0 = "2014-4-16 21:08:48";

            String str1 = "发表于 2014-4-16 21:08:48";

            String str2 = "发表于 3 天前 ";
            String str3 = "发表于 3 小时前 ";
            String str4 = "发表于 3 分钟前 ";
            String str5 = "发表于 3 秒前 ";
            String str6 = "发表于 前天08:27 ";
            String str7 = "发表于 今天08:27 ";
            String str8 = "2014年05月22日09:08";

            System.out.println(str0 + "\t\t" + Utils.formatDate(str0));
            System.out.println(str1 + "\t\t" + Utils.formatDate(str1));
            System.out.println(str2 + "\t\t" + Utils.formatDate(str2));
            System.out.println(str3 + "\t\t" + Utils.formatDate(str3));
            System.out.println(str4 + "\t\t" + Utils.formatDate(str4));
            System.out.println(str5 + "\t\t" + Utils.formatDate(str5));
            System.out.println(str6 + "\t\t" + Utils.formatDate(str6));
            System.out.println(str7 + "\t\t" + Utils.formatDate(str7));
            System.out.println(str8 + "\t\t" + Utils.formatDate(str8));
    }
    
    @Test
    public void testFormatDate1() {
        String str = "05-23 13:52";
        System.out.println(Utils.formatDate(str));
    }
    
    @Test
    public void testFormatDate2() {
        String str = "5-23  9:59";
        System.out.println(Utils.formatDate(str));
    }

    @Test
    public void testFormatDate3() {
    	String str = "2013-07-27  05:55";
    	String str1 = "2013-7-7  05:55";
    	String str2 = "2014-6-7  09:23";
    	System.out.println(Utils.formatDate(str));
    	System.out.println(Utils.formatDate(str1));
    	System.out.println(Utils.formatDate(str2));
    }
    
    @Test
    public void testFormatDate4() {
    	String str = "12:49";
    	String str1 = "1:1";
    	System.out.println(Utils.formatDate(str));
    	System.out.println(Utils.formatDate(str1));
    }
    
    @Test
    public void testFormatDate5() {
    	String str = "5-27";
    	String str1 = "11-2";
    	System.out.println(Utils.formatDate(str));
    	System.out.println(Utils.formatDate(str1));
    }
    
    @Test
    public void testIsNum() {
    	Assert.assertTrue(Utils.isNum("3"));
    	Assert.assertTrue(Utils.isNum("33"));
    	Assert.assertFalse(Utils.isNum("3njb"));
    	Assert.assertFalse(Utils.isNum(""));
    	Assert.assertFalse(Utils.isNum(null));
    }

    @Test
    public void testExtractNum() {
    	Assert.assertEquals(Utils.extractNum("Hello3Two"), 3);
    	Assert.assertEquals(Utils.extractNum("33 HelloTwo"), 33);
    	Assert.assertEquals(Utils.extractNum("HelloTwo 4 5"), 4);
    }
}
