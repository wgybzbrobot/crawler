package com.zxsoft.crawler.util.parse;

import java.io.IOException;
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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.storage.Seed;
import com.zxsoft.crawler.storage.WebPageMy;
import com.zxsoft.crawler.tools.Tool;
import com.zxsoft.crawler.tools.Tools;
import com.zxsoft.crawler.util.Utils;

public class ParseUtil implements Tool {

	private static Logger LOG = LoggerFactory.getLogger(ParseUtil.class);

	private Tools tools;
	private Configuration conf;

	private ThreadPoolExecutor pool = null;

	private AtomicBoolean continuePage = new AtomicBoolean(true);
	private AtomicInteger pageNum = new AtomicInteger(1);

	public void setTools(Tools tools) {
		this.tools = tools;
	}

	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	public ParseStatus parse(WebPageMy page) throws ParserNotFoundException {
		ParserFactory factory = new ParserFactory();
		factory.setConf(conf);
		Parser parser = factory.getParserByCategory(page.getListConf().getCategory());
		parser.setTools(tools);
		return runParser(page, parser);
	}

	public ParseStatus runParser(WebPageMy page, Parser parser) {
		ParseStatus status = null;
		Seed seed = page.getSeed();
		if (seed.getType() == Category.LIST_PAGE) { // 列表页
		// pool = Executors.newFixedThreadPool(3);
			int numThreads = page.getListConf().getNumThreads();
			if (numThreads <= 0)
				numThreads = 1;
			int maxThreads = conf.getInt("spider.parse.thread.max", 10);
			if (numThreads > maxThreads)
				numThreads = maxThreads;

			pool = newFixedThreadPool(numThreads);
			status = parseListPage(page, parser);
		} else if (seed.getType() == Category.DETAIL_PAGE) { // 详细页首页丢失
			LOG.info("详细页首页丢失: " + seed.getTitle());
			status = parseDetailPage(page, parser);
		} else if (seed.getType() == Category.REPLY_PAGE) { // 详细页回复页丢失
			LOG.info("详细页回复页丢失: " + seed.getTitle());
			status = parseDetailPage(page, parser);
		} else {
			LOG.error("Configuration ERROR occur.");
		}
		return status;
	}

	/**
	 * 解析列表页()
	 */
	public ParseStatus parseListPage(WebPageMy page, Parser parser) {
		ParseStatus status = null;
		ListConf listConf = page.getListConf();
		Document document = page.getDocument();
		String indexUrl = page.getSeed().getIndexUrl();
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
				Date lastupdatedate = null;
				if (!StringUtils.isEmpty(listConf.getUpdatedatedom())
				        && !CollectionUtils.isEmpty(line.select(listConf.getUpdatedatedom()))) {
					lastupdatedate = Utils.formatDate(line.select(listConf.getUpdatedatedom()).first().text());
					if (lastupdatedate.before(page.getSeed().getLimitDate())) {
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
				LOG.info(title + lastupdatedate);
				WebPageMy wp = new WebPageMy();
				Seed detailPageSeed = new Seed(curl, indexUrl, 0, Category.DETAIL_PAGE, title, releasedate, curl, page.getSeed().getLimitDate());
				JsoupLoader loader = new JsoupLoader();
				loader.setTools(tools);
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
				wp.setListConf(page.getListConf());

				try {
					// parser.parse(wp);
					ParseCallable pc = new ParseCallable(parser, wp);
					// FutureTask<ParseStatus> task = new
					// FutureTask<ParseStatus>(pc);
					// pool.submit(task);
					// new Thread(task).start();
					Future<ParseStatus> future = pool.submit(pc);
					// future.get();
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
						tools.getInfoService().addSeed(seedCopy);
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

	public Tools getTools() {
		return tools;
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
