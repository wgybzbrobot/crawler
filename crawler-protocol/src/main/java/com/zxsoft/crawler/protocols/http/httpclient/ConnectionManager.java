//package com.zxsoft.crawler.protocols.http.httpclient;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.net.Proxy;
//import java.net.Socket;
//import java.nio.charset.CodingErrorAction;
//
//import javax.net.ssl.SSLContext;
//
//import org.apache.http.Consts;
//import org.apache.http.config.ConnectionConfig;
//import org.apache.http.config.MessageConstraints;
//import org.apache.http.config.Registry;
//import org.apache.http.config.RegistryBuilder;
//import org.apache.http.config.SocketConfig;
//import org.apache.http.conn.DnsResolver;
//import org.apache.http.conn.HttpConnectionFactory;
//import org.apache.http.conn.ManagedHttpClientConnection;
//import org.apache.http.conn.routing.HttpRoute;
//import org.apache.http.conn.socket.ConnectionSocketFactory;
//import org.apache.http.conn.socket.PlainConnectionSocketFactory;
//import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLContexts;
//import org.apache.http.conn.ssl.X509HostnameVerifier;
//import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//import org.apache.http.protocol.HttpContext;
//
//public class ConnectionManager {
//
//	private PoolingHttpClientConnectionManager connManager;
//	private DnsResolver dnsResolver;
//
//	public PoolingHttpClientConnectionManager getManager() {
//		return connManager;
//	}
//
//	public ConnectionManager() {
//		Registry<ConnectionSocketFactory> socketFactoryRegistry = createRegistry();
//		HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = createConnectionFactory();
//		connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, connFactory,
//		        dnsResolver);
//		SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(3000).setTcpNoDelay(true).build();
//		connManager.setDefaultSocketConfig(socketConfig);
//		MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200)
//		        .setMaxLineLength(2000).build();
//		ConnectionConfig connectionConfig = ConnectionConfig.custom()
//		        .setMalformedInputAction(CodingErrorAction.IGNORE)
//		        .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8)
//		        .setMessageConstraints(messageConstraints).build();
//		connManager.setDefaultConnectionConfig(connectionConfig);
//		connManager.setMaxTotal(100);
//		connManager.setDefaultMaxPerRoute(10);
//		
//		
//	}
//
//	public Registry<ConnectionSocketFactory> createRegistry() {
//		SSLContext sslcontext = SSLContexts.createSystemDefault();
//		// Use custom hostname verifier to customize SSL hostname verification.
//		X509HostnameVerifier hostnameVerifier = new BrowserCompatHostnameVerifier();
//
//		// Create a registry of custom connection socket factories for supported
//		// protocol schemes.
//		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
//		        .<ConnectionSocketFactory> create()
//		        .register("http", /*new MyConnectionSocketFactory(SSLContexts.createSystemDefault()))*/PlainConnectionSocketFactory.INSTANCE)
//		        .register("https", /*new MyConnectionSocketFactory(SSLContexts.createSystemDefault()))*/new SSLConnectionSocketFactory(sslcontext, hostnameVerifier))
//		        .build();
//		return socketFactoryRegistry;
//	}
//
//	public HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> createConnectionFactory() {
//		HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory();
//		return connFactory;
//	}
//	
//	static class MyConnectionSocketFactory extends SSLConnectionSocketFactory {
//
//	    public MyConnectionSocketFactory(final SSLContext sslContext) {
//	        super(sslContext);
//	    }
//
//	    @Override
//	    public Socket createSocket(final HttpContext context) throws IOException {
//	        InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
//	        Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
//	        return new Socket(proxy);
//	    }
//
//	}
//
//}
