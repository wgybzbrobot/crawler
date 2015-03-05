package com.zxsoft.crawler.master;

import org.junit.Test;

public class NetworkSearchThreadTest {

        
        @Test
        public void test() throws InterruptedException {
                new Thread(new NetworkSearchThread(10)).start();
                Thread.sleep(600000);
        }
}
