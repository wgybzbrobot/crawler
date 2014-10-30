package com.zxsoft.crawler.master.impl;

import org.junit.Test;
import org.thinkingcloud.framework.util.Assert;

import com.zxsoft.crawler.master.Prey;
import com.zxsoft.crawler.master.PreyFrontier;
import com.zxsoft.crawler.master.SlaveManager;

public class TestRedisPreyFrontier {

	@Test
	public void testGetPrey() {
		PreyFrontier frontier = new RedisPreyFrontier();
		Prey prey = (Prey) frontier.requestPrey().get("prey");
		Assert.notNull(prey);
	}

	@Test
	public void testAddPrey() {
		PreyFrontier frontier = new RedisPreyFrontier();
		// 将上次抓取时间设置为当前时间，供下次抓取使用
		Prey prey = new Prey("http://www.baidu.com", "http://tieba.baidu.com/f?ie=utf-8&kw=%E8%9A%8C%E5%9F%A0", "百度贴吧蚌埠吧",
		        SlaveManager.JobType.NETWORK_INSPECT.toString(), "001", 16);
		prey.setStart(System.currentTimeMillis());
		frontier.pushPrey(prey);
		prey = new Prey("http://www.sina.com.cn", "http://roll.news.sina.com.cn/s/channel.php", "新浪新闻滚动",
		        SlaveManager.JobType.NETWORK_INSPECT.toString(),"001",  12);
		prey.setStart(System.currentTimeMillis());
		frontier.pushPrey(prey);
		prey = new Prey("http://bbs.anhuinews.com", "http://bbs.anhuinews.com/forum.php?mod=forumdisplay&fid=319&filter=lastpost&orderby=lastpost",
		        "徽风论坛", SlaveManager.JobType.NETWORK_INSPECT.toString(), "001", 47);
		prey.setStart(System.currentTimeMillis());
		frontier.pushPrey(prey);
	}
	
	@Test
	public void testRemoveAll() {
		PreyFrontier frontier = new RedisPreyFrontier();
		frontier.removeAll();
	}
}
