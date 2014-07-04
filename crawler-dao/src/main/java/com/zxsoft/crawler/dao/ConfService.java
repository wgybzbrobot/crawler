package com.zxsoft.crawler.dao;

import java.net.MalformedURLException;
import java.util.List;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.carson.pojo.ListConf;
import com.zxsoft.carson.seed.ForumDetailConf;
import com.zxsoft.carson.seed.NewsDetailConf;
import com.zxsoft.carson.seed.SeedConf;
import com.zxsoft.carson.util.Utils;

public class ConfService {

	private Logger LOG = LoggerFactory.getLogger(ConfService.class);

	private ConfDao confMapper;

	private static WeakHashMap<String, SeedConf> cache = new WeakHashMap<String, SeedConf>();

	/**
	 * 获取列表配置信息
	 * @param url
	 * @return
	 */
	public ListConf getListConf(String url) {
		return confMapper.getListConf(url);
	}

	/**
	 * 获取列表配置信息
	 */
	public List<ListConf> getListConfs() {
		return confMapper.getListConfs();
	}
	
	/**
	 * 获取论坛详细页配置信息
	 */
	public ForumDetailConf getForumDetailConf(String url) throws MalformedURLException {
		url = Utils.getHost(url);
		if (cache.get(url) != null) {
			return (ForumDetailConf) cache.get(url);
		}
		ForumDetailConf detailConf = confMapper.getForumDetailConf(url);
		cache.put(url, detailConf);
		return detailConf;
	}

	/**
	 * 获取新闻资讯详细页配置信息
	 * @param host
	 * @return
	 */
	public NewsDetailConf getNewsDetailConf(String host) {
		return confMapper.getNewsDetailConf(host);
	}

	/*
	 * Getter and Setter
	 */
	public ConfDao getConfMapper() {
		return confMapper;
	}
	public void setConfMapper(ConfDao confMapper) {
		this.confMapper = confMapper;
	}

}
