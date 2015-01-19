package com.zxsoft.crawler.web.controller.crawler;

import org.junit.Test;

public class TestJobController {

        @Test
        public void testJobExist() {
                String url = "http://www.epochtimes.com/gb/ncChineseCommunity.htm";
                JobController controller = new JobController();
                boolean exist = controller.jobExist(url);
                
        }
}
