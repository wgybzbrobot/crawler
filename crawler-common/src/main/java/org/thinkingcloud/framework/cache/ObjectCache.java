package org.thinkingcloud.framework.cache;

import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ObjectCache {

        private static final Logger LOG = LoggerFactory.getLogger(ObjectCache.class);

        private static final WeakHashMap<String, ObjectCache> CACHE = new WeakHashMap<String, ObjectCache>();

        private final Cache<String, Object> objectMap;

        // private final HashMap<String, Object> objectMap;

//        class MyRemovalListener implements RemovalListener<String, Object> {
//                private Logger LOG = LoggerFactory.getLogger(MyRemovalListener.class);
//                @Override
//                public void onRemoval(RemovalNotification<String, Object> notification) {
//                        LOG.debug(notification.getKey() + " in cache is removed");
//                }
//        }


        /**
         * 初始化objectMap, 不过期
         */
        private ObjectCache() {
                // objectMap = new HashMap<String, Object>();
                objectMap = CacheBuilder.newBuilder().maximumSize(1000).concurrencyLevel(4).recordStats().build();
        }
        /**
         * 初始化objectMap, 指定过期时间.
         * @param timeout 过期时间, 分钟.
         */
        private ObjectCache(int timeout) {
                if (timeout < 0) {
                        timeout = 30;
                }
                objectMap = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(timeout, TimeUnit.MINUTES)
                                            .concurrencyLevel(4).recordStats().build();
        }

        /**
         * 获取缓存, 若不存在则创建缓存，创建的缓存键值对没有过期时间
         * @param name  缓存名
         * @return
         */
        public static ObjectCache get(String name) {
                ObjectCache objectCache = CACHE.get(name);
                if (objectCache == null) {
                        LOG.debug("No object cache found for name=" + name + ", instantiating a new object cache");
                        objectCache = new ObjectCache();
                        CACHE.put(name, objectCache);
                }
                return objectCache;
        }
        /**
         * 获取缓存<code>name</code>, 若不存在则创建缓存，并设置缓存的过期时间是<code>min</code>分钟．
         * @param name  缓存
         * @param timeout   分钟
         * @return
         */
        public static ObjectCache get(String name, int timeout) {
                ObjectCache objectCache = CACHE.get(name);
                if (objectCache == null) {
                        LOG.debug("No object cache found for name=" + name + ", instantiating a new object cache with timeout " + timeout);
                        objectCache = new ObjectCache(timeout);
                        CACHE.put(name, objectCache);
                }
                return objectCache;
        }

        public Object getObject(String key) {
                return objectMap.getIfPresent(key);
        }

        public void setObject(String key, Object value) {
                objectMap.put(key, value);
        }
}
