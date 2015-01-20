//package com.zxsoft.crawler.store.impl;
//
//import java.util.LinkedList;
//import java.util.List;
//
//import org.junit.Test;
//
//import com.zxsoft.crawler.storage.RecordInfo;
//import com.zxsoft.crawler.store.Output;
//import com.zxsoft.crawler.store.OutputException;
//
//public class TestRedisOutput {
//
//        private Output output = new RedisOutput();
//        
//        @Test
//        public void testWrite() throws OutputException {
//                RecordInfo info1 = new RecordInfo("testforredis", "http://test1.org");
//                info1.setId("testforredisaaa");
//                info1.setPlatform(3);
//                info1.setContent("hello world redis test.");
//                info1.setTimestamp(System.currentTimeMillis());
//                info1.setLasttime(System.currentTimeMillis());
//                
//                
//                RecordInfo info2 = new RecordInfo("Test2", "http://test2.org");
//                info2.setId("Testforredis2");
//                List<RecordInfo> list = new LinkedList<RecordInfo>();
//                list.add(info1);
////                list.add(info2);
//                output.write(list);
//        }
//}
