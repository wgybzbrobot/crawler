//package com.zxsoft.crawler.master;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import redis.clients.jedis.Transaction;
//
//import com.zxsoft.crawler.master.impl.RedisPreyFrontier;
//
///**
// * URL管理
// * <p>
// * 可用不同的存储机制存储URL
// * 
// * @see RedisPreyFrontier
// */
//public abstract class PreyFrontier {
//
//	private static Logger LOG = LoggerFactory.getLogger(PreyFrontier.class);
//
//	/**
//	 * 获取Prey, 得到Prey后对prey从新计算抓取时间再存入urlbase库
//	 * 
//	 * @return prey
//	 */
//	public  Map<String, Object> requestPrey() {
//		Map<String, Object> map = new HashMap<String, Object>();
//
//		Prey prey = popPrey();
//		long interval = System.currentTimeMillis() - prey.getPrevFetchTime();
//		long realInterval = prey.getFetchinterval() * 60 * 1000;
//		if (interval >= realInterval) {
//			removePrey(prey);
//			// 将上次抓取时间设置为当前时间，供下次抓取使用
//			prey.setPrevFetchTime(System.currentTimeMillis());
//			pushPrey(prey);
//			LOG.info("next prey: " + prey.toString());
//		}
//
//		map.put("prey", prey);
//		map.put("wait", realInterval - interval);
//		return map;
//	}
//
//	/**
//	 * 清空URL库
//	 */
//	public abstract void removeAll();
//
//	/**
//	 * 获取URL相关信息
//	 */
//	protected abstract Prey popPrey();
//
//	/**
//	 * 添加URL相关信息
//	 */
//	public abstract void pushPrey(Prey prey);
//
//	/**
//	 * 删除URL相关信息
//	 */
//	public abstract void removePrey(Prey prey);
//}
