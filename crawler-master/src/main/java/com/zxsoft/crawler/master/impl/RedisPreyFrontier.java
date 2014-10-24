package com.zxsoft.crawler.master.impl;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.CollectionUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.google.gson.Gson;
import com.zxsoft.crawler.master.Prey;
import com.zxsoft.crawler.master.PreyFrontier;

/**
 * 用Redis实现URL信息的存储. 用Redis zset实现优先级队列，
 * 以<code>1.0 / Prey.fetchinterval</code>作为score
 */
public class RedisPreyFrontier extends PreyFrontier {

	private static Logger LOG = LoggerFactory.getLogger(RedisPreyFrontier.class);
	
	private String url;
	private static final String URLBASE = "urlbase";
	
	private static PoolFactory factory;
	static {
		factory = new PoolFactory("127.0.0.1", 6379);
	}
	
	/**
	 * 获取score最大的
	 */
	@Override
	protected Prey popPrey() {
		Jedis jedis = factory.getJedis();
		Set<String> strs = jedis.zrevrange(URLBASE, 0, 0);
		if (CollectionUtils.isEmpty(strs)) {
			LOG.warn("No records in redis urlbase.");
			return null;
		}
		
		String json = strs.toArray(new String[0])[0];
		Prey prey = new Gson().fromJson(json, Prey.class);
		LOG.info("pop prey: " + json);
		return prey;
	}

	@Override
	public void pushPrey(Prey prey) {
		Jedis jedis = factory.getJedis();
		double score = 1.0d / (System.currentTimeMillis() / 60000 + prey.getFetchinterval());
		jedis.zadd(URLBASE, score, prey.toString());
		LOG.info("push prey: " + prey.toString() + ", score:" + score);
	}
	
	@Override
	public void removePrey(Prey prey) {
		Jedis jedis = factory.getJedis();
		Long count = jedis.zrem(URLBASE, prey.toString());
		LOG.info("remove count: " + count);
	}

	@Override
	public void removeAll() {
		Jedis jedis = factory.getJedis();
		jedis.zremrangeByRank(URLBASE, 0, -1);
	}

	private static final class PoolFactory {
		private JedisPool pool;
		public PoolFactory(String host, int port) {
			pool = new JedisPool(host, port); 
		}
		public Jedis getJedis() {
			return pool.getResource();
		}
	}
}
