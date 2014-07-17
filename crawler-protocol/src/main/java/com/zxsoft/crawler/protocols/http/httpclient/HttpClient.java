package com.zxsoft.crawler.protocols.http.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.metadata.Metadata;
import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocols.http.HttpBase;

@Component
@Scope("prototype")
public class HttpClient extends HttpBase {

	public static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);

	private int retryNum = 5;

	private static PoolingHttpClientConnectionManager connectionManager = new ConnectionManager()
	        .getManager();

	private CloseableHttpClient client /*
										 * = HttpClients.custom()
										 * .setConnectionManager
										 * (connectionManager).build()
										 */;

	public CloseableHttpClient getClient() {
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
					return new InetAddress[] { InetAddress
					        .getByAddress(new byte[] { 127, 0, 0, 1 }) };
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
		MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200)
		        .setMaxLineLength(2000).build();
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
		RequestConfig defaultRequestConfig = RequestConfig.custom()
		        .setCookieSpec(CookieSpecs.BEST_MATCH).setExpectContinueEnabled(true)
		        .setStaleConnectionCheckEnabled(true)
		        .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
		        .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();

		// Create an HttpClient with the given custom dependencies and
		// configuration.
		client = HttpClients.custom().setConnectionManager(connManager)
		        .setDefaultCookieStore(cookieStore)
		        .setDefaultCredentialsProvider(credentialsProvider)
		        .setDefaultRequestConfig(defaultRequestConfig).build();

		return client;
	}

	String ip = "";
	
	@Override
	protected Response getResponse(URL url, Proxy proxy, boolean followRedirects)
	        throws ProtocolException, IOException {
		
		int code;
		Metadata headers = new Metadata();
		byte[] content = null;

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
				if (host.equalsIgnoreCase("localhost")) {
					return new InetAddress[] { InetAddress
					        .getByAddress(new byte[] { 127, 0, 0, 1 }) };
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
		MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200)
		        .setMaxLineLength(2000).build();
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
		RequestConfig defaultRequestConfig = RequestConfig.custom()
		        .setCookieSpec(CookieSpecs.BEST_MATCH).setExpectContinueEnabled(true)
		        .setStaleConnectionCheckEnabled(true)
		        .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
		        .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();

		// Create an HttpClient with the given custom dependencies and
		// configuration.
		CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connManager)
		        .setDefaultCookieStore(cookieStore)
		        .setDefaultCredentialsProvider(credentialsProvider)
		        .setDefaultRequestConfig(defaultRequestConfig).build();

		try {
			HttpGet httpget = new HttpGet(url.toString());

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
			CloseableHttpResponse httpResponse = null;
			try {
				httpResponse = httpclient.execute(httpget, context);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				HttpEntity entity = httpResponse.getEntity();
				System.out.println(httpResponse.getStatusLine());
				if (entity != null) {
					System.out.println("Response content length: " + entity.getContentLength());
				}

				code = httpResponse.getStatusLine().getStatusCode();
				Header[] heads = httpResponse.getAllHeaders();
				for (int i = 0; i < heads.length; i++) {
					headers.set(heads[i].getName(), heads[i].getValue());
				}
				
				long contentLength = Long.MAX_VALUE;
				InputStream in = null;
				in = httpResponse.getEntity().getContent();
				byte[] buffer = new byte[HttpBase.BUFFER_SIZE];
				int bufferFilled = 0;
				int totalRead = 0;
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				while ((bufferFilled = in.read(buffer, 0, buffer.length)) != -1
				        && totalRead + bufferFilled <= contentLength) {
					totalRead += bufferFilled;
					out.write(buffer, 0, bufferFilled);
				}
				content = out.toByteArray();

				if (content != null) {
					// check if we have to uncompress it
					String contentEncoding = headers.get(Response.CONTENT_ENCODING);
					if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
						content = processGzipEncoded(content, url);
					} else if ("deflate".equals(contentEncoding)) {
						content = processDeflateEncoded(content, url);
					}
				}
				
			} finally {
				try {
					httpResponse.close();
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
		System.out.println(headers.get(Response.LOCATION));
		return new Response(url, code, headers, content, headers.get(Response.CONTENT_ENCODING));
	}
}