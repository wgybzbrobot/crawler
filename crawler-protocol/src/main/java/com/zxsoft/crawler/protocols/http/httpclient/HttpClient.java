package com.zxsoft.crawler.protocols.http.httpclient;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

// HTTP Client imports
import org.apache.hadoop.conf.Configuration;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
// Nutch imports
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.storage.WebPage.Field;
import com.zxsoft.crawler.util.CrawlerConfiguration;

/**
 * This class is a protocol plugin that configures an HTTP client for Basic,
 * Digest and NTLM authentication schemes for web server as well as proxy
 * server. It takes care of HTTPS protocol as well as cookies in a single fetch
 * session.
 * 
 */
public class HttpClient extends HttpBase {

	public static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);

	private static PoolingHttpClientConnectionManager connectionManager = ConnectionManager
	        .getManager();

	private CloseableHttpClient client = HttpClients.custom().setConnectionManager(connectionManager)
	        .build();
	
	private static final Collection<WebPage.Field> FIELDS = new HashSet<WebPage.Field>();

	static {
		FIELDS.add(WebPage.Field.MODIFIED_TIME);
		FIELDS.add(WebPage.Field.HEADERS);
	}

	// @Override
	public Collection<Field> getFields() {
		return FIELDS;
	}

	/**
	 * Returns the configured HTTP client.
	 * 
	 * @return HTTP client
	 */
	public CloseableHttpClient getClient() {
		return client;
	}

	/**
	 * Constructs this plugin.
	 */
	public HttpClient() {
		super(LOG);
	}

	public static void main(String[] args) throws Exception {
		HttpClient http = new HttpClient();
		http.setConf(CrawlerConfiguration.create());
		Proxy proxy = new Proxy("", "", "", "", 8080, "SOCK");
		main(http, proxy, args);
	}

	@Override
    protected Response getResponse(URL url, Proxy proxy, WebPage page, boolean followRedirects)
            throws ProtocolException, IOException {
		try {
	        return new HttpClientResponse(this, url, proxy, page, followRedirects);
        } catch (Exception e) {
	        e.printStackTrace();
        }
	    return null;
    }
}