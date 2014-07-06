package com.zxsoft.crawler.protocols.httpclient;

import java.nio.charset.CodingErrorAction;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpHost;
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
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class ConnectionManager {

	private static PoolingHttpClientConnectionManager connManager;
	private DnsResolver dnsResolver;

	public static PoolingHttpClientConnectionManager getManager() {

		return connManager;
	}

	private ConnectionManager() {
		Registry<ConnectionSocketFactory> socketFactoryRegistry = createRegistry();
		HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = createConnectionFactory();
		connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, connFactory,
		        dnsResolver);

		
		// Create socket configuration
		SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
		// Configure the connection manager to use socket configuration either
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

		// Configure total max or per route limits for persistent connections
		// that can be kept in the pool or leased by the connection manager.
		connManager.setMaxTotal(100);
		connManager.setDefaultMaxPerRoute(10);
		connManager.setMaxPerRoute(new HttpRoute(new HttpHost("somehost", 80)), 20);
	}

	public Registry<ConnectionSocketFactory> createRegistry() {
		SSLContext sslcontext = SSLContexts.createSystemDefault();
		// Use custom hostname verifier to customize SSL hostname verification.
		X509HostnameVerifier hostnameVerifier = new BrowserCompatHostnameVerifier();

		// Create a registry of custom connection socket factories for supported
		// protocol schemes.
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
		        .<ConnectionSocketFactory> create()
		        .register("http", PlainConnectionSocketFactory.INSTANCE)
		        .register("https", new SSLConnectionSocketFactory(sslcontext, hostnameVerifier))
		        .build();
		return socketFactoryRegistry;
	}

	public HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> createConnectionFactory() {
		HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory();
		return connFactory;
	}

}
