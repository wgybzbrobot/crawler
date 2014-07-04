package com.zxsoft.crawler.web.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.zxsoft.crawler.web.dao.ConfDao;
import com.zxsoft.crawler.web.model.ForumDetailConf;
import com.zxsoft.crawler.web.model.ListConf;
import com.zxsoft.crawler.web.model.NewsDetailConf;
import com.zxsoft.crawler.web.model.Seed;
import com.zxsoft.framework.utils.Page;

@Service
public class ConfService {

	@Autowired
	private ConfDao confMapper;

	@Transactional
	public void saveForumConf(ListConf listConf, ForumDetailConf detailConf) {

		if (confMapper.getListConf(listConf.getUrl()) == null) {
			confMapper.saveListConf(listConf);
		} else {
			confMapper.delListConf(listConf);
			confMapper.saveListConf(listConf);
		}

		if (confMapper.getForumDetailConf(detailConf.getHost()) == null) {
			confMapper.saveForumDetailConf(detailConf);
		} else {
			confMapper.delForumDetailConf(detailConf);
			confMapper.saveForumDetailConf(detailConf);
		}

		Seed seed = new Seed(listConf.getUrl(), 1, listConf.getUrl(), listConf.getFetchinterval(), listConf.getFetchinterval(),
		        false);
		if (confMapper.getSeed(seed) == null) {
			confMapper.addSeed(seed);
		} else {
			confMapper.delSeed(seed);
			confMapper.addSeed(seed);
		}
	}
	@Transactional
	public void saveNewsConf(ListConf listConf, NewsDetailConf detailConf) {
		
		if (confMapper.getListConf(listConf.getUrl()) == null) {
			confMapper.saveListConf(listConf);
		} else {
			confMapper.delListConf(listConf);
			confMapper.saveListConf(listConf);
		}
		
		if (confMapper.getForumDetailConf(detailConf.getHost()) == null) {
			confMapper.saveNewsDetailConf(detailConf);
		} else {
			confMapper.delNewsDetailConf(detailConf);
			confMapper.saveNewsDetailConf(detailConf);
		}
		
		Seed seed = new Seed(listConf.getUrl(), 1, listConf.getUrl(), listConf.getFetchinterval(), listConf.getFetchinterval(),
				false);
		if (confMapper.getSeed(seed) == null) {
			confMapper.addSeed(seed);
		} else {
			confMapper.delSeed(seed);
			confMapper.addSeed(seed);
		}
	}

	/**
	 * 获取列表配置信息
	 * 
	 * @param url
	 * @return
	 */
	public ListConf getListConf(String url) {
		return confMapper.getListConf(url);
	}
	
	/**
	 * 获取列表配置信息
	 */
	public Page getListConfs(ListConf listConf, int pageNo, int pageSize) {
		List<ListConf> res = new ArrayList<ListConf>();
		String keyword = "";
		if (listConf == null || StringUtils.isEmpty(keyword = listConf.getComment())) {
			res = confMapper.getListConfs(pageNo - 1, pageSize);
		} else {
			res = confMapper.findListConfs(keyword, pageNo - 1 , pageSize);
		}
		Page page = new Page(10, res);
		return page;
	}

	/**
	 * 获取新闻资讯详细页配置信息
	 * 
	 * @param host
	 * @return
	 */
	public NewsDetailConf getNewsDetailConf(String host) {
		return confMapper.getNewsDetailConf(host);
	}

}
