package com.zxsoft.crawler.parse;

import java.util.Date;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.Assert;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.parse.FetchStatus.Status;
import com.zxsoft.crawler.plugin.parse.ext.DateExtractor;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.store.OutputException;

/**
 * 调用相应的解析器解析网页
 */
public final class NetworkInspectParserController extends ParseTool {

	private static Logger LOG = LoggerFactory.getLogger(NetworkInspectParserController.class);
	private static final int _pageNum = 2;

	public FetchStatus parse(WebPage page) throws ParserNotFoundException {
		Assert.notNull(page);
		String indexUrl = page.getBaseUrl();
		LOG.debug("indexUrl: " + indexUrl);
		ListConf listConf = confDao.getListConf(indexUrl);
		if (listConf == null) {
		        LOG.error("No List Configuration, url: " + indexUrl);
			return new FetchStatus(indexUrl, 43, Status.CONF_ERROR);
		}

		page.setAjax(listConf.isAjax());
		page.setAuth(listConf.isAuth());
		ParserFactory factory = new ParserFactory();
		
		page.setBaseUrl(indexUrl);
		page.setListUrl(page.getBaseUrl());
		ProtocolOutput output = fetch(page);

		if (!output.getStatus().isSuccess())
			return new FetchStatus(indexUrl, 51, Status.PROTOCOL_FAILURE);

		Document document = output.getDocument();

		LOG.info("【" + listConf.getComment() + "】抓取开始");

		String listDom = listConf.getListdom(), lineDom = listConf.getLinedom(), updateDom = listConf.getUpdatedom();
		String urlDom = listConf.getUrldom();
		boolean hasUpdate = false, continuePage = true;
		int sum = 0, pageNum = 1;
		Status status = Status.SUCCESS;
		String msg = "";
		while (true) {
			Elements list = document.select(listDom);
			if (CollectionUtils.isEmpty(list)) {
				return new FetchStatus(indexUrl, 44, Status.CONF_ERROR);
			}

			Elements lines = list.first().select(lineDom);
			// 没有更新日期时
			if (!hasUpdate && pageNum > _pageNum) {
				continuePage = false;
				msg = StringUtils.concat(msg, "没有获取列表页中记录的更新时间，抓完设定的页数" + _pageNum + ", 若数量为0,则可能配置有误.");
				break;
			} else if (pageNum > _pageNum + 3) { // 有更新日期
			        continuePage = false;
                                msg += "抓完设定的页数" + (_pageNum + 3);
                                break;
			}
			
			LOG.info("【" + listConf.getComment() + "】第" + pageNum + "页,　记录数: " + lines.size());
			int count = 0;
			for (Element line : lines) {
				Date update = null;
				if (!StringUtils.isEmpty(updateDom) && !CollectionUtils.isEmpty(line.select(updateDom))) {
					update = DateExtractor.extract(line.select(updateDom).first().html());
					if (update != null && update.getTime() + 60000L < page.getPrevFetchTime()) {
					        if (count > 5) {
        						msg += "截止时间" + new Date(page.getPrevFetchTime()).toLocaleString();
        						continuePage = false;
        						break;
					        }
					        count++;
					}
					hasUpdate = true;
				}

				Date releasedate = null; // NOTE:有些列表页面可能没有发布时间
				if (!StringUtils.isEmpty(listConf.getDatedom()) && !CollectionUtils.isEmpty(line.select(listConf.getDatedom()))) {
					releasedate = DateExtractor.extract(line.select(listConf.getDatedom()).first().html());
				}

				if (CollectionUtils.isEmpty(line.select(urlDom)) || StringUtils.isEmpty(line.select(urlDom).first().absUrl("href")))
					continue;

				String curl = "";
				Elements as = line.getElementsByTag("a");
				if (!CollectionUtils.isEmpty(as) && as.size() == 1) { // 行记录就是一条url
					curl = as.first().absUrl("href");
				} else {
					curl = line.select(urlDom).first().absUrl("href");
				}

				String title = line.select(urlDom).first().text();
				
				WebPage wp = page.clone();
				wp.setTitle(title);
				wp.setBaseUrl(curl);
				wp.setAjax(false); // detail page use normal load
				wp.setDocument(null);
				if (update != null) {
				        wp.setUpdateTime(update.getTime());
				} else {
				        wp.setUpdateTime(0);
				}
		                
				try {
					Parser parser = factory.getParserByCategory(listConf.getCategory());
					/*
					 * 重要! 设置通用字段值
					 */
					parser.setup(page);
					FetchStatus _status = parser.parse(wp);
					sum += _status.getCount();
					msg = StringUtils.concat(msg, _status.getMessage());
				} catch (OutputException e) {
			                msg = StringUtils.concat(msg, e.getMessage());
                                        LOG.error(msg);
                                        status = Status.OUTPUT_FAILURE;
				}catch (Exception e) {
				        msg = StringUtils.concat(msg, e.getMessage());
					LOG.error(msg, e);
				}
				if (!continuePage) {
					break;
				}
			}

			if (!continuePage) {
				break;
			} else { // 翻页
				WebPage np = page.clone();
				np.setBaseUrl(document.location());
				np.setDocument(document);
				ProtocolOutput ptemp = fetchNextPage(pageNum, np);
				if (ptemp == null || !ptemp.getStatus().isSuccess()) {
					break;
				}
				document = ptemp.getDocument();
				if (document == null) {
					break;
				}
				pageNum++;
			}
		}
		LOG.info("【" + listConf.getComment() + "】total fetch record number:" + sum + ", msg:" + msg);
		return new FetchStatus(indexUrl, 21, status, sum, msg);
	}
}
