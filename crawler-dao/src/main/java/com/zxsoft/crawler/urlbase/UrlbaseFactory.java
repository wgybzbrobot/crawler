package com.zxsoft.crawler.urlbase;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import com.zxsoft.crawler.storage.WebPage;

public abstract class UrlbaseFactory {
	
	private static int DEFAULT_NUM = 10;
	
	public static BlockingQueue<WebPage> queue = new ArrayBlockingQueue<WebPage>(20);
	
	/**
	 * @return url
	 */
	public synchronized WebPage poll() {
		if (queue.size() == 0) {
			put();
		}
		return queue.poll();
	}
	
	public synchronized void put() {
		List<WebPage> pages = getWebPages(DEFAULT_NUM);
		queue.addAll(pages);
	}
	
	public abstract List<WebPage> getWebPages(int num);

}
