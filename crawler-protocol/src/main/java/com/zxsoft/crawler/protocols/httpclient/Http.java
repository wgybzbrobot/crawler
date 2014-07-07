package com.zxsoft.crawler.protocols.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;



// HTTP Client imports
import org.apache.hadoop.conf.Configuration;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
// Nutch imports
import org.apache.nutch.protocol.ProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
public class Http extends HttpBase {

	public static final Logger LOG = LoggerFactory.getLogger(Http.class);

	private static PoolingHttpClientConnectionManager connectionManager = ConnectionManager
	        .getManager();

	// Since the Configuration has not yet been set,
	// then an unconfigured client is returned.
	// private static HttpClient client = new HttpClient(connectionManager);
	private CloseableHttpClient client = HttpClients.custom().setConnectionManager(connectionManager)
	        .build();
	
	private static Configuration conf;

	int maxThreadsTotal = 10;

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
	public Http() {
		super(LOG);
	}

	/**
	 * Reads the configuration from the Nutch configuration files and sets the
	 * configuration.
	 * 
	 * @param conf
	 *            Configuration
	 */
	public void setConf(Configuration conf) {
		super.setConf(conf);
		Http.conf = conf;
		this.maxThreadsTotal = conf.getInt("fetcher.threads.fetch", 10);
	}

	public static void main(String[] args) throws Exception {
		Http http = new Http();
		http.setConf(CrawlerConfiguration.create());
		main(http, args);
	}

	protected Response getResponse(URL url, WebPage page, boolean redirect)
	        throws ProtocolException, IOException {
		try {
	        return new WebClient(this, url, page, redirect);
        } catch (Exception e) {
	        e.printStackTrace();
        }
		return null;
	}
}