package com.zxsoft.crawler.protocols.http;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


import com.zxsoft.crawler.parse.PageHelper;
//import com.zxsoft.carson.parse.PageHelper;
import com.zxsoft.crawler.storage.Seed;
//import com.zxsoft.crawler.util.Tool;
//import com.zxsoft.crawler.util.Tools;
import com.zxsoft.crawler.util.URLNormalizer;
import com.zxsoft.crawler.util.Utils;

/**
 * jsoup download page.
 */
public final class JsoupLoader /*implements Tool*/ {

	private static Logger LOG = LoggerFactory.getLogger(JsoupLoader.class);
//	private static Tools tools;

	/**
	 * 加载一页
	 * <p>
	 * set delComment as true for get url list of baidu tieba.
	 */
	public Document load(Seed seed, boolean delComment) {
		Document document = load(seed);

		if (document != null && delComment) {
			String htm = document.html().replaceAll("<!--", "");
			htm = htm.replaceAll("-->", "");
			document = Jsoup.parse(htm, document.baseUri());
		}

		return document;
	}

	public Document load(Seed seed) {
		Document document = null;
		String url = seed.getUrl();
		url = URLNormalizer.normalize(url);
		try {
			document = load(url);
		} catch (IllegalArgumentException iae) {
			LOG.error(url + "IllegalArgumentException: Invalid URL.");
		} catch (SocketTimeoutException ste) {
			LOG.error("SocketTimeoutException: Cannot connect to " + url);
			seed.setRemain(2);
			seed.setLose(true);
			if (!StringUtils.isEmpty(seed.getIndexUrl())) {
//				tools.getInfoService().addSeed(seed);
			} else {
				LOG.error("seed indexUrl is null. Please check..." + seed.getUrl());
			}
		} catch (HttpStatusException e) {
			seed.setRemain(2);
			seed.setLose(true);
			LOG.error("HttpStatusException: " + e.getMessage());
			if (!StringUtils.isEmpty(seed.getIndexUrl())) {
//				tools.getInfoService().addSeed(seed);
			} else {
				LOG.error("seed indexUrl is null. Please check..." + seed.getUrl());
			}
		} catch (IOException e) {
			LOG.error("IOException: Cannot connect to " + url);
			seed.setRemain(2);
			seed.setLose(true);
//			tools.getInfoService().addSeed(seed);
			e.printStackTrace();
		}

		return document;
	}

	public Document load(String url) throws IOException {
		url = URLNormalizer.normalize(url);
		Connection connection = Jsoup.connect(url);
		Response response = connection.userAgent(randUserAgent()).execute();
		Document document = response.parse();
		return document;
	}

	/**
	 * 加载下一页
	 * 
	 * @param currentDoc
	 *            当前页的Document
	 */
	@Deprecated
	public Document loadNextPage(Document currentDoc) throws IOException {
		Elements elements = currentDoc.select("a:matchesOwn(下一页|下页|下一页>)");
		if (!CollectionUtils.isEmpty(elements)) {
			String url = elements.first().absUrl("href");
			return load(url);
		} else {
			/*
			 * Find the position of current page url from page bar, get next
			 * achor as the next page url. However, there is a problem. It's not
			 * very Accurate, some url cannot find from page bar, because it
			 * changed when load it.
			 */
			Element pagebar = PageHelper.getPageBar(currentDoc);
			Elements achors = pagebar.getElementsByTag("a");
			if (pagebar != null || !CollectionUtils.isEmpty(achors)) {
				String url = currentDoc.location();
				for (int i = 0; i < achors.size(); i++) {
					if (url.equals(achors.get(i).absUrl("href"))) {
						if (i + 1 < achors.size()) {
							return load(achors.get(i + 1).absUrl("href"));
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 加载下一页
	 * 
	 * @param currentDoc
	 *            当前页的Document
	 */
	public Document loadNextPage(int pageNum, Document currentDoc) throws IOException {
		Elements elements = currentDoc.select("a:matchesOwn(下一页|下页|下一页>)");
		if (!CollectionUtils.isEmpty(elements)) {
			String url = elements.first().absUrl("href");
			return load(url);
		} else {
			/*
			 * Find the position of current page url from page bar, get next
			 * achor as the next page url.
			 */
			Element pagebar = PageHelper.getPageBar(currentDoc);
			if (pagebar != null) {
				Elements achors = pagebar.getElementsByTag("a");
				if (pagebar != null || !CollectionUtils.isEmpty(achors)) {
					for (int i = 0; i < achors.size(); i++) {
						if (Utils.isNum(achors.get(i).text()) && Integer.valueOf(achors.get(i).text().trim()) == pageNum + 1) {
							return load(achors.get(i).absUrl("href"));
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 加载上一页
	 */
	public Document loadPrePage(Document currentDoc) throws IOException {
		Elements elements = currentDoc.select("a:matchesOwn(上一页|上页|<上一页)");
		if (CollectionUtils.isEmpty(elements)) {
			return null;
		}
		String url = elements.first().absUrl("href");
		return load(url);
	}

	private String randUserAgent() {
		String[] agents = {
		        "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36",
		        "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:29.0) Gecko/20100101 Firefox/29.0" };

		int i = (int) (Math.random() * 2);
		// System.out.println(i);
		return agents[i];
	}

	public static void main(String[] args) {
        try {
        	Document document;
        	
	        document = Jsoup.connect("http://180.97.33.23/p/3142166246").get();
	        System.out.println(document.html());
        } catch (IOException e) {
	        e.printStackTrace();
        }
	}
}
