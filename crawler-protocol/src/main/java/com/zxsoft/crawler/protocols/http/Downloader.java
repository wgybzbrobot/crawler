package com.zxsoft.crawler.protocols.http;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.storage.Seed;
import com.zxsoft.crawler.storage.WebPageMy;
import com.zxsoft.crawler.util.Tool;
import com.zxsoft.crawler.util.Tools;
import com.zxsoft.crawler.util.Utils;

public class Downloader implements Tool {

	private static Logger LOG = LoggerFactory.getLogger(Downloader.class);

	private Tools tools;

	public Downloader(Tools tools) {
		this.tools = tools;
	}

	/**
	 * 访问URL
	 * 
	 * @return document object
	 * @throws MalformedURLException
	 */
	public WebPageMy connect(Seed seed) throws IOException, MalformedURLException {

		String url = seed.getUrl();
		LOG.info("connecting " + url);

		WebPageMy page = new WebPageMy();
		page.setSeed(seed);
		page.setHost(Utils.getHost(url));

		// 通过种子首页获取列表详细配置
		if (StringUtils.isEmpty(seed.getIndexUrl())) {
			LOG.error(url + "种子的首页URL没有配置.");
			return null;
		}
		ListConf listConf = tools.getDomService().getListConf(seed.getIndexUrl());
		if (listConf == null) {
			LOG.error("Cannot find information for " + seed.getIndexUrl() + " from database.");
			return null;
		}
		page.setListConf(listConf);

		/*if (listConf.isAjax()) {
			AjaxLoader loader = new AjaxLoader();
			Document document = loader.load(url);
			page.setDocument(document);
			page.setAjaxLoader(loader);
		} else {
			JsoupLoader loader = new JsoupLoader();
			loader.setTools(tools);
			page.setDocument(loader.load(seed, true));
		}*/
		SmartLoader loader = new SmartLoader();
		String dom = listConf.getListdom() + " " + listConf.getLinedom();
		page.setDocument(loader.load(url, dom));
		return page;
	}

	public Tools getTools() {
		return tools;
	}

	public void setTools(Tools tools) {
		this.tools = tools;
	}

}
