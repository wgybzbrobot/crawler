package com.zxsoft.crawler.parse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hadoop.conf.Configuration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.zxsoft.crawler.dao.ConfDao;
import com.zxsoft.crawler.protocols.http.SmartLoader;
import com.zxsoft.crawler.protocols.http.proxy.ProxyRandom;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.storage.Seed;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.storage.WebPageMy;
import com.zxsoft.crawler.util.Utils;

public class ParseUtil {

	private static Logger LOG = LoggerFactory.getLogger(ParseUtil.class);

	private Configuration conf;
	
	private ProxyRandom random;
	
	@Autowired
	public ParseUtil (ProxyRandom random) {
		this.random = random;
	}

	private ThreadPoolExecutor pool = null;

	private AtomicBoolean continuePage = new AtomicBoolean(true);
	private AtomicInteger pageNum = new AtomicInteger(1);

	private ConfDao confDao;
	
	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	public ParseStatus parse(WebPage page) throws ParserNotFoundException {
		ParserFactory factory = new ParserFactory();
		factory.setConf(conf);
		
		ListConf listConf = confDao.getListConf(page.getBaseUrl().toString());
		ParseStatus status = null;
		
		if (listConf != null) {
			Parser parser = factory.getParserByCategory(listConf.getCategory());
			int numThreads = Utils.getPositiveNumber(listConf.getNumThreads(), 1);
			int maxThreads = conf.getInt("spider.parse.thread.max", 10);
			if (numThreads > maxThreads)
				numThreads = maxThreads;
			pool = newFixedThreadPool(numThreads);
			status = parseListPage(page, parser, listConf);
			
		} 
		return status;
	}

	/**
	 * 解析列表页()
	 */
	public ParseStatus parseListPage(WebPage page, Parser parser, ListConf listConf) {
		ParseStatus status = null;
		InputStream inputStream = new ByteArrayInputStream(page.getContent());
		Document document = Jsoup.parse(inputStream, charsetName, baseUri);
		String indexUrl = page.getBaseUrl().toString();
		
		LOG.info("【" + listConf.getComment() + "】抓取开始");
		
		while (true) {
			Elements list = document.select(listConf.getListdom());
			if (CollectionUtils.isEmpty(list)) {
				LOG.warn("main dom set error.");
				return null;
			}
			Elements lines = list.first().select(listConf.getLinedom());

			if (pageNum.get() > listConf.getPageNum()) {
				continuePage.set(false);
				break;
			}

			LOG.info("【" + listConf.getComment() + "】thread number in " + pageNum.get() + " page: " + lines.size());
			for (Element line : lines) {
				Date lastupdate = null;
				if (!StringUtils.isEmpty(listConf.getUpdatedom())
				        && !CollectionUtils.isEmpty(line.select(listConf.getUpdatedom()))) {
					lastupdate = Utils.formatDate(line.select(listConf.getUpdatedom()).first().text());
					if (lastupdate.before(new Date(page.getPrevFetchTime()))) {
						continuePage.set(false);
						break;
					}
				}

				Date releasedate = null; // NOTE:有些列表页面可能没有发布时间
				if (!StringUtils.isEmpty(listConf.getDatedom())
				        && !CollectionUtils.isEmpty(line.select(listConf.getDatedom())))
					releasedate = Utils.formatDate(line.select(listConf.getDatedom()).first().text());

				if (CollectionUtils.isEmpty(line.select(listConf.getUrldom()))
				        || StringUtils.isEmpty(line.select(listConf.getUrldom()).first().absUrl("href")))
					continue;

				String curl = line.select(listConf.getUrldom()).first().absUrl("href");
				String title = line.select(listConf.getUrldom()).first().text();
				LOG.info(title + lastupdate);
				WebPageMy wp = new WebPageMy();
				Seed detailPageSeed = new Seed(curl, indexUrl, 0, Category.DETAIL_PAGE, title, releasedate, curl, page.getSeed().getLimitDate());
				
				
				
				Document dtemp = loader.load(detailPageSeed);
				if (dtemp == null) {
					continue;
				}
				wp.setDocument(dtemp);
				wp.setSeed(detailPageSeed);
				wp.getSeed().setLimitDate(page.getSeed().getLimitDate());
				wp.setAjaxLoader(page.getAjaxLoader());
				try {
					wp.setHost(Utils.getHost(curl));
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}

				try {
					ParseCallable pc = new ParseCallable(parser, wp);
					Future<ParseStatus> future = pool.submit(pc);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}

				if (continuePage.get() == false) {
					break;
				}
			}

			if (/* page.getSeed().isLose() || */continuePage.get() == false) { // 是丢失帖或符合停止翻页条件
				break;
			} else { // 翻页
				Document oldDoc = document;
				SmartLoader loader = new SmartLoader();
				try {
					document = loader.loadNexPage(pageNum.get(), document);
				} catch (IOException e) {
					LOG.error(indexUrl + "列表页翻页出错");
					try {
						Seed seedCopy = page.getSeed().clone();
						seedCopy.setUrl(document.location());
						seedCopy.setRemain(2);
						seedCopy.setLose(true);
						seedCopy.setType(Category.DETAIL_PAGE);
					} catch (CloneNotSupportedException ce) {
						ce.printStackTrace();
					}
					e.printStackTrace();
				}

				if (document == null || document.html().equals(oldDoc.html())) {
					LOG.info("document == null or current page is same to next page，break");
					break;
				}
				pageNum.incrementAndGet();
			}
		}
		// pool.shutdown();
		LOG.info("【" + listConf.getComment() + "】抓取结束");
		return status;
	}

	/**
	 * 解析丢失的详细页
	 */
	public ParseStatus parseDetailPage(WebPageMy page, Parser parser) {
		ParseStatus status = new ParseStatus();
		try {
			status = parser.parse(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	private static class ParseCallable implements Callable<ParseStatus> {
		private Parser parser;
		private WebPageMy page;

		public ParseCallable(Parser parser, WebPageMy page) {
			this.parser = parser;
			this.page = page;
		}

		public ParseStatus call() throws Exception {
			ParseStatus status = null;
			try {
				status = parser.parse(page);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return status;
		}
	}

	public ThreadPoolExecutor newFixedThreadPool(int nThreads) {

		final ThreadPoolExecutor result = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
		        new ArrayBlockingQueue<Runnable>(64), new ThreadPoolExecutor.CallerRunsPolicy());

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
