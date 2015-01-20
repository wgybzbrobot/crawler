//package com.zxsoft.crawler.store.impl;
//
//import java.util.List;
//
//import zx.soft.sent.solr.utils.RedisCache;
//import zx.soft.utils.json.JsonUtils;
//
//import com.zxsoft.crawler.storage.RecordInfo;
//import com.zxsoft.crawler.store.Output;
//import com.zxsoft.crawler.store.OutputException;
//
///**
// * 输出数据到redis
// * @author xiayun
// *
// */
//public class RedisOutput implements Output {
//
//        private static final RedisCache redisCache = new RedisCache();
//        
//        @Override
//        public int write(RecordInfo info) throws OutputException {
//                // TODO Auto-generated method stub
//                return 0;
//        }
//
//        @Override
//        public int write(List<RecordInfo> recordInfos) throws OutputException {
//                String[] data = new String[recordInfos.size()];
//                for (int i = 0;i<recordInfos.size();i++) {
//                        data[i] = JsonUtils.toJsonWithoutPretty(recordInfos.get(i));
//                }
//                redisCache.addRecord(data);
//                return recordInfos.size();
//        }
//
//}
