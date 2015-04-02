package com.zxsoft.crawler.urlbase;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolFactory implements Closeable {

    private static Map<String, JedisPool> jedisPoolMap;

    private JedisPoolFactory() {

    }

    /**
     * 通过Ip获取redis实例
     * @param ip
     * @return
     */
    public static synchronized JedisPool getInstance(String host, int port, String passwd) {
        if (jedisPoolMap == null) {
            jedisPoolMap = new HashMap<String, JedisPool>();
            JedisPool pool = init(jedisPoolMap, host, port, passwd);
            return pool;
        } else {
            JedisPool pool = jedisPoolMap.get(host+ ":" + port);
            if (pool == null) {
                pool = init(jedisPoolMap, host, port, passwd);
                jedisPoolMap.put(host + ":" + port, pool);
            }
            return pool;
        }
    }

    private static JedisPool init(Map<String, JedisPool> jedisPoolMap, String host, int port, String passwd) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool pool = null;
        if (null == passwd || passwd.length() == 0) {
            pool = new JedisPool(poolConfig, host, port, 5000);
        } else {
            pool = new JedisPool(poolConfig, host, port, 5000, passwd);
        }
        return pool;
    }

    @Override
    public void close() throws IOException {
        if (jedisPoolMap != null) {
            for (String ip : jedisPoolMap.keySet()) {
                JedisPool pool = jedisPoolMap.get(ip);
                if (pool != null) {
                    pool.destroy();
                }
            }
        }
    }

}
