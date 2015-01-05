package com.zxsoft.crawler.util;

import org.junit.Test;

import com.zxisl.commons.utils.Assert;

public class UtilsTest {

    @Test
    public void testIsNum() {
    	Assert.isTrue(Utils.isNum("3"));
    	Assert.isTrue(Utils.isNum("33"));
    	Assert.isTrue(!Utils.isNum("3njb"));
    	Assert.isTrue(!Utils.isNum(""));
    	Assert.isTrue(!Utils.isNum(null));
    }

    @Test
    public void testExtractNum() {
    	Assert.isTrue(Utils.extractNum("Hello3Two") == 3);
    	Assert.isTrue(Utils.extractNum("33 HelloTwo") == 33);
    	Assert.isTrue(Utils.extractNum("HelloTwo 4 5") == 4);
    }
}
