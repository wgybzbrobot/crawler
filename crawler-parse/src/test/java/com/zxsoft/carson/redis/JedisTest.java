package com.zxsoft.carson.redis;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisTest {

	@Test
	public void testJedis () throws InterruptedException {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
		Jedis jedis = pool.getResource();
		
		jedis.set("foo", "bar");
		String foobar = jedis.get("foo");
		assert foobar.equals("bar");
		
		// 设置过期时间
		jedis.set("gone", "bady, gone");
		jedis.expire("gone", 10);
		String there = jedis.get("gone");
		assert there.equals("baby, gone");
		
		Thread.sleep(4000);
		
		String notThere = jedis.get("gone");
		assert notThere == null;
		
		// Redis 列表
		jedis.rpush("people", "Mary");
		assert jedis.lindex("people", 0).equals("Mary");
		
		jedis.rpush("people", "Jack", "xiayun");
		assert jedis.lindex("people", 2).equals("xiayun");
		
		pool.returnResource(jedis);
		pool.destroy();
	}
}
