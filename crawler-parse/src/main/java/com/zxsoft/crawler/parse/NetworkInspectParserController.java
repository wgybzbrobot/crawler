package com.zxsoft.crawler.parse;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hadoop.conf.Configuration;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.Assert;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.StringUtils;

import com.zxsoft.crawler.parse.FetchStatus.Status;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.storage.Section;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.Utils;

/**
 * Control which parser to parse the page.
 */
public final class NetworkInspectParserController extends ParseTool {

	private static Logger LOG = LoggerFactory.getLogger(NetworkInspectParserController.class);
	private Configuration conf;
	private ThreadPoolExecutor pool = null;
	private AtomicBoolean continuePage = new AtomicBoolean(true);
	private AtomicInteger pageNum = new AtomicInteger(1);
	private AtomicInteger sum = new AtomicInteger(0);
	private static int _pageNum = 2;
	
	public NetworkInspectParserController(Configuration conf) {
		this.conf = conf;
		setConf(conf);
	}
	
	public FetchStatus parse(WebPage page) throws ParserNotFoundException {
		Assert.notNull(page);
		String indexUrl = page.getBaseUrl();
		ListConf listConf = confDao.getListConf(indexUrl);
		if (listConf == null) {
			throw new NullPointerException("没有列表页配置:" + page.getBaseUrl());
		}
		page.setAjax(listConf.isAjax());
		page.setAuth(listConf.isAuth());
		
		FetchStatus status = new FetchStatus(indexUrl);
		
		ParserFactory factory = new ParserFactory();
		factory.setConf(conf);
		Parser parser = factory.getParserByCategory(listConf.getCategory());
		
		int numThreads = Utils.getPositiveNumber(listConf.getNumThreads(), 1);
		int maxThreads = conf.getInt("spider.parse.thread.max", 10);
		if (numThreads > maxThreads)
			numThreads = maxThreads;
		pool = newFixedThreadPool(numThreads);
		
		page.setBaseUrl(indexUrl);
		page.setListUrl(page.getBaseUrl());
		ProtocolOutput output = fetch(page);
		if (!output.getStatus().isSuccess()) {
			status.setStatus(Status.PROTOCOL_FAILURE);
			status.setMessage(output.getStatus().getMessage());
			return status;
		}
		Document document = output.getDocument();
		
		LOG.debug("【" + listConf.getComment() + "】抓取开始");
		
		String listDom = listConf.getListdom();
		String lineDom = listConf.getLinedom();
		String updateDom = listConf.getUpdatedom();
		
		while (true) {
//			LOG.debug(document.html());
			Elements list = document.select(listDom);
			if (CollectionUtils.isEmpty(list)) {
				LOG.error("Cannot get thread list, main dom set error:" + indexUrl);
				throw new NullPointerException("得不到列表, 列表DOM[listDom]设置错误:" + indexUrl);
			}
			
			Elements lines = list.first().select(lineDom);

			if (pageNum.get() > _pageNum) {
				continuePage.set(false);
				status.setMessage("抓完设定的页数" + _pageNum);
				break;
			}

			LOG.debug("【" + listConf.getComment() + "】thread number in " + pageNum.get() + " page: " + lines.size());
			
			List<Callable<FetchStatus>> tasks = new ArrayList<Callable<FetchStatus>>();
			
			for (Element line : lines) {
				Date update = null;
				if (!StringUtils.isEmpty(updateDom) && !CollectionUtils.isEmpty(line.select(updateDom))) {
					try {
	                    update = Utils.formatDate(line.select(updateDom).first().text());
	                    if (update.getTime() < page.getPrevFetchTime()) {
	                    	status.setMessage("更新时间在上次抓取时间" + page.getPrevFetchTime() + "之前, 停止抓取");
	                    	LOG.info("更新时间在上次抓取时间" + page.getPrevFetchTime() + "之前, 停止抓取");
	                    	continuePage.set(false);
	                    	break;
	                    }
                    } catch (ParseException e) {
                    	LOG.error("Cannot parse date: " + update + " in page " + indexUrl, e);
                    }
				}

				Date releasedate = null; // NOTE:有些列表页面可能没有发布时间
				if (!StringUtils.isEmpty(listConf.getDatedom())
				        && !CollectionUtils.isEmpty(line.select(listConf.getDatedom()))) {
					try {
						releasedate = Utils.formatDate(line.select(listConf.getDatedom()).first()
						        .text());
					} catch (ParseException e) {
						LOG.error("Cannot parse date: " + releasedate + " in page " + indexUrl, e);
					}
				}
				
				if (CollectionUtils.isEmpty(line.select(listConf.getUrldom()))
				        || StringUtils.isEmpty(line.select(listConf.getUrldom()).first().absUrl("href")))
					continue;

				String curl = "";
				Elements as = line.getElementsByTag("a");
				if (!CollectionUtils.isEmpty(as) && as.size() == 1) { // 防止行记录就是一条url
					curl = as.first().absUrl("href");
				} else { 
					curl = line.select(listConf.getUrldom()).first().absUrl("href");
				}
				
				String title = line.select(listConf.getUrldom()).first().text();
				
				LOG.info(title);
				WebPage wp = page.clone();
				wp.setTitle(title);
				wp.setBaseUrl(curl);
				wp.setAjax(false);// detail page use normal load 
				wp.setDocument(null);
				
				try {
					ParseCallable pc = new ParseCallable(parser, wp);
					tasks.add(pc);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}

				if (continuePage.get() == false) {
					break;
				}
			}
			
			try {
				LOG.debug("task size: " + tasks.size());
	            List<Future<FetchStatus>> result = pool.invokeAll(tasks);
	            for (Future<FetchStatus> future : result) {
	                try {
	                    FetchStatus parseStatus = future.get();
//	                    if (parseStatus.getStatus() != Status.SUCCESS) {
	                    	status.setStatus(parseStatus.getStatus());
//	                    }
	                    LOG.info(parseStatus.getUrl() + ":数量(" + parseStatus.getCount() + "):消息 (" + parseStatus.getMessage() + ")");
	                    sum.addAndGet(parseStatus.getCount());
                    } catch (ExecutionException e) {
	                    e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
	            e.printStackTrace();
            }

			if (!continuePage.get()) {
				break;
			} else { // 翻页
				WebPage np = page.clone();
				np.setBaseUrl(document.location());
				np.setDocument(document);
				ProtocolOutput ptemp = fetchNextPage(pageNum.get(), np);
				if (ptemp ==null || !ptemp.getStatus().isSuccess()) {
					LOG.debug("No next page, exit.");
					break;
				}
				document = ptemp.getDocument();
				if (document == null) {
					LOG.debug("document == null or current page is same to next page，break");
					break;
				}
				pageNum.incrementAndGet();
			}
		}
		// pool.shutdown();
		LOG.info("【" + listConf.getComment() + "】抓取结束, 共抓取数据数量:" + sum.get());
//		status.setStatus(FetchStatus.Status.SUCCESS);
		status.setCount(sum.get());
		return status;
	}

	public ThreadPoolExecutor newFixedThreadPool(int nThreads) {
		final ThreadPoolExecutor result = new ThreadPoolExecutor(nThreads, nThreads + 10, 20, TimeUnit.SECONDS,
		        new ArrayBlockingQueue<Runnable>(20), new ThreadPoolExecutor.CallerRunsPolicy());
		result.setThreadFactory(new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					public void uncaughtException(Thread t, Throwable e) {
						LOG.error("Thread exception: " + t.getName(), e);
						result.shutdown();
					}
				});
				return t;
			}
		});
		return result;
	}

}
