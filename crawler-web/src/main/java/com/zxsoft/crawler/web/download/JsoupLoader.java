package com.zxsoft.crawler.web.download;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

/**
 * jsoup download page.
 */
public final class JsoupLoader {

	private static Logger LOG = LoggerFactory.getLogger(JsoupLoader.class);

	public Document load(String url) {
		Document document = null;
		Response response = null;
		try {
			Connection connection = Jsoup.connect(url);
			response = connection.userAgent(randUserAgent()).execute();
//			response = connection.userAgent("Baiduspider").execute();
//			response.statusCode()
			document = response.parse();
		} catch (IllegalArgumentException iae) {
		} catch (SocketTimeoutException ste) {
		} catch (HttpStatusException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (document == null || document.text().length() < 1000) {
			AjaxLoader ajaxloader = new AjaxLoader();
			try {
				document = ajaxloader.load(url);
			} catch (FailingHttpStatusCodeException | IOException e) {
				e.printStackTrace();
			}
		}
		return document;
	}

	private String randUserAgent() {
		String[] agents = {
		        "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36",
		        "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:29.0) Gecko/20100101 Firefox/29.0" };

		int i = (int) (Math.random() * 2);
		// System.out.println(i);
		return agents[i];

	}

}
