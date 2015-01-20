package com.zxsoft.crawler.parse;

import org.junit.Test;

public class TestLocationUtils {

        LocationUtils locationUtils = new LocationUtils();
        
        @Test
        public void testGetLocation() {
                String location = locationUtils.getLocation("220.178.13.114");
                System.out.println(location);
        }

        @Test
        public void testGetLocationCode() {
                int code = locationUtils.getLocationCode("220.178.13.114");
                System.out.println("code:" + code);
        }
}
