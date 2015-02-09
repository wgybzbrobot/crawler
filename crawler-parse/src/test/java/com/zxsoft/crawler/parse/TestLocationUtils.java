package com.zxsoft.crawler.parse;

import org.junit.Test;

public class TestLocationUtils {

        LocationUtils locationUtils = new LocationUtils();
        
        @Test
        public void testGetLocation() {
                 // http://zlzg.yqteam.cc/   美国 新泽西州(merck公司)  999999
                String location = locationUtils.getLocation("54.178.75.106");
                System.out.println(location);
        }

        @Test
        public void testGetLocationCode() {
                int code = locationUtils.getLocationCode("54.178.75.106");
                System.out.println("code:" + code);
        }
       
}
