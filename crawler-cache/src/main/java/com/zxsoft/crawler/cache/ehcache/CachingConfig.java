//package com.zxsoft.crawler.cache.ehcache;
//
//import net.sf.ehcache.config.CacheConfiguration;
//
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.CachingConfigurer;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.interceptor.KeyGenerator;
//import org.springframework.cache.interceptor.SimpleKeyGenerator;
//import org.springframework.cache.ehcache.EhCacheCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@EnableCaching
//public class CachingConfig implements CachingConfigurer {
//	
//    @Bean(destroyMethod="shutdown")
//    public net.sf.ehcache.CacheManager ehCacheManager() {
//    	
//        CacheConfiguration cacheConfiguration1 = new CacheConfiguration();
//        cacheConfiguration1.setName("listConfCache");
//        cacheConfiguration1.setMemoryStoreEvictionPolicy("LRU");
//        cacheConfiguration1.setMaxEntriesLocalHeap(1000);
//        
//        CacheConfiguration cacheConfiguration2 = new CacheConfiguration();
//        cacheConfiguration2.setName("forumDetailConf");
//        cacheConfiguration2.setMemoryStoreEvictionPolicy("LRU");
//        cacheConfiguration2.setMaxEntriesLocalHeap(1000);
//        
//        CacheConfiguration cacheConfiguration3 = new CacheConfiguration();
//        cacheConfiguration3.setName("forumDetailConf");
//        cacheConfiguration3.setMemoryStoreEvictionPolicy("LRU");
//        cacheConfiguration3.setMaxEntriesLocalHeap(1000);
//        
//        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
//        config.addCache(cacheConfiguration1);
//        config.addCache(cacheConfiguration2);
//        config.addCache(cacheConfiguration3);
//
//        return net.sf.ehcache.CacheManager.newInstance(config);
//    }
//
//    @Bean
//    public CacheManager cacheManager() {
//        return new EhCacheCacheManager(ehCacheManager());
//    }
//
//    @Bean
//    public KeyGenerator keyGenerator() {
//        return new SimpleKeyGenerator();
//    }
//}