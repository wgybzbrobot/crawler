package com.zxsoft.crawler.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParser;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.util.Assert;

import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClient;
import com.zxsoft.crawler.storage.WebPage;

public class HttpClientTest {

	@Test
	public void testProxy() {
		HttpBase http = new HttpClient();
		Proxy proxy = new Proxy("HTTP", "", "", "192.168.31.244", 28080, "");
		WebPage page = new WebPage();
		Document document = http.getProtocolOutput("http://news.sohu.com/scroll/").getDocument();
		Assert.notNull(document);
		System.out.println(document.html());
	}

	@Test
	public void test() throws IOException, KeyStoreException, NoSuchAlgorithmException,
	        CertificateException, KeyManagementException {

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream instream = new FileInputStream(new File("my.keystore"));
		try {
			trustStore.load(instream, "nopassword".toCharArray());
		} finally {
			instream.close();
		}

		// Trust own CA and all self-signed certs
		SSLContext sslcontext = SSLContexts.custom()
		        .loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
		        new String[] { "TLSv1" }, null,
		        SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope("192.168.31.244", 28080),
		        new UsernamePasswordCredentials("username", "password"));

		int tryNum = 2;
		while (tryNum-- > 0) {
			CloseableHttpClient httpclient = HttpClients.custom()
			        .setDefaultCredentialsProvider(credsProvider).build();
			try {
				HttpHost target = new HttpHost("e.bank.ecitic.com/perbank5/signInCredit.do", 443,
				        "https");
				HttpHost target2 = new HttpHost("baidu.com", 80, "http");
				HttpHost proxy = new HttpHost("192.168.31.244", 28080);

				RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
				HttpGet request = new HttpGet("/");
				request.setConfig(config);

				System.out.println("Executing request " + request.getRequestLine() + " to "
				        + target + " via " + proxy);

				CloseableHttpResponse response = httpclient.execute(target, request);
				try {
					System.out.println("----------------------------------------");
					System.out.println(response.getStatusLine());
					EntityUtils.consume(response.getEntity());
				} finally {
					response.close();
				}
			} finally {
				httpclient.close();
			}
		}
	}

	@Test
	public void testSSL() {
		int trynum = 20;
		while (trynum-- > 0) {
			// Use custom message parser / writer to customize the way HTTP
			// messages are parsed from and written out to the data stream.
			HttpMessageParserFactory<HttpResponse> responseParserFactory = new DefaultHttpResponseParserFactory() {

				@Override
				public HttpMessageParser<HttpResponse> create(SessionInputBuffer buffer,
				        MessageConstraints constraints) {
					LineParser lineParser = new BasicLineParser() {

						@Override
						public Header parseHeader(final CharArrayBuffer buffer) {
							try {
								return super.parseHeader(buffer);
							} catch (ParseException ex) {
								return new BasicHeader(buffer.toString(), null);
							}
						}

					};
					return new DefaultHttpResponseParser(buffer, lineParser,
					        DefaultHttpResponseFactory.INSTANCE, constraints) {

						@Override
						protected boolean reject(final CharArrayBuffer line, int count) {
							// try to ignore all garbage preceding a status line
							// infinitely
							return false;
						}

					};
				}

			};
			HttpMessageWriterFactory<HttpRequest> requestWriterFactory = new DefaultHttpRequestWriterFactory();

			// Use a custom connection factory to customize the process of
			// initialization of outgoing HTTP connections. Beside standard
			// connection
			// configuration parameters HTTP connection factory can define
			// message
			// parser / writer routines to be employed by individual
			// connections.
			HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(
			        requestWriterFactory, responseParserFactory);

			// Client HTTP connection objects when fully initialized can be
			// bound to
			// an arbitrary network socket. The process of network socket
			// initialization,
			// its connection to a remote address and binding to a local one is
			// controlled
			// by a connection socket factory.

			// SSL context for secure connections can be created either based on
			// system or application specific properties.
			SSLContext sslcontext = SSLContexts.createSystemDefault();
			// Use custom hostname verifier to customize SSL hostname
			// verification.
			X509HostnameVerifier hostnameVerifier = new BrowserCompatHostnameVerifier();

			// Create a registry of custom connection socket factories for
			// supported
			// protocol schemes.
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
			        .<ConnectionSocketFactory> create()
			        .register("http", PlainConnectionSocketFactory.INSTANCE)
			        .register("https", new SSLConnectionSocketFactory(sslcontext, hostnameVerifier))
			        .build();

			// Use custom DNS resolver to override the system DNS resolution.
			DnsResolver dnsResolver = new SystemDefaultDnsResolver() {

				@Override
				public InetAddress[] resolve(final String host) throws UnknownHostException {
					if (host.equalsIgnoreCase("myhost")) {
						return new InetAddress[] { InetAddress.getByAddress(new byte[] { 127, 0, 0,
						        1 }) };
					} else {
						return super.resolve(host);
					}
				}

			};

			// Create a connection manager with custom configuration.
			PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
			        socketFactoryRegistry, connFactory, dnsResolver);

			// Create socket configuration
			SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
			// Configure the connection manager to use socket configuration
			// either
			// by default or for a specific host.
			connManager.setDefaultSocketConfig(socketConfig);
			connManager.setSocketConfig(new HttpHost("somehost", 80), socketConfig);

			// Create message constraints
			MessageConstraints messageConstraints = MessageConstraints.custom()
			        .setMaxHeaderCount(200).setMaxLineLength(2000).build();
			// Create connection configuration
			ConnectionConfig connectionConfig = ConnectionConfig.custom()
			        .setMalformedInputAction(CodingErrorAction.IGNORE)
			        .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8)
			        .setMessageConstraints(messageConstraints).build();
			// Configure the connection manager to use connection configuration
			// either
			// by default or for a specific host.
			connManager.setDefaultConnectionConfig(connectionConfig);
			connManager.setConnectionConfig(new HttpHost("somehost", 80), ConnectionConfig.DEFAULT);

			// Configure total max or per route limits for persistent
			// connections
			// that can be kept in the pool or leased by the connection manager.
			connManager.setMaxTotal(100);
			connManager.setDefaultMaxPerRoute(10);
			connManager.setMaxPerRoute(new HttpRoute(new HttpHost("somehost", 80)), 20);

			// Use custom cookie store if necessary.
			CookieStore cookieStore = new BasicCookieStore();
			// Use custom credentials provider if necessary.
			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			// Create global request configuration
			RequestConfig defaultRequestConfig = RequestConfig
			        .custom()
			        .setCookieSpec(CookieSpecs.BEST_MATCH)
			        .setExpectContinueEnabled(true)
			        .setStaleConnectionCheckEnabled(true)
			        .setTargetPreferredAuthSchemes(
			                Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
			        .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();

			// Create an HttpClient with the given custom dependencies and
			// configuration.
			CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connManager)
			        .setDefaultCookieStore(cookieStore)
			        .setDefaultCredentialsProvider(credentialsProvider)
			        .setDefaultRequestConfig(defaultRequestConfig).build();

			try {
				HttpGet httpget = new HttpGet("https://e.bank.ecitic.com/perbank5/signInCredit.do");

				// Request configuration can be overridden at the request level.
				// They will take precedence over the one set at the client
				// level.
				RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
				        .setSocketTimeout(5000).setConnectTimeout(5000)
				        .setConnectionRequestTimeout(5000)
				        .setProxy(new HttpHost("192.168.1.102", 28128)).build();
				httpget.setConfig(requestConfig);

				// Execution context can be customized locally.
				HttpClientContext context = HttpClientContext.create();
				// Contextual attributes set the local context level will take
				// precedence over those set at the client level.
				context.setCookieStore(cookieStore);
				context.setCredentialsProvider(credentialsProvider);

				System.out.println("executing request " + httpget.getURI());
				CloseableHttpResponse response = null;
				try {
					response = httpclient.execute(httpget, context);
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					HttpEntity entity = response.getEntity();

					System.out.println("----------------------------------------");
					System.out.println(response.getStatusLine());
					if (entity != null) {
						System.out.println("Response content length: " + entity.getContentLength());
					}
					System.out.println("----------------------------------------");

					// Once the request has been executed the local context can
					// be used to examine updated state and various objects
					// affected
					// by the request execution.

					// Last executed request
					context.getRequest();
					// Execution route
					context.getHttpRoute();
					// Target auth state
					context.getTargetAuthState();
					// Proxy auth state
					context.getTargetAuthState();
					// Cookie origin
					context.getCookieOrigin();
					// Cookie spec used
					context.getCookieSpec();
					// User security token
					context.getUserToken();

				} finally {
					try {
						response.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} finally {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}