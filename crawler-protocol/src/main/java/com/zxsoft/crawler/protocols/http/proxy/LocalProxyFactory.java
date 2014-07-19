package com.zxsoft.crawler.protocols.http.proxy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.zxsoft.crawler.cache.proxy.Proxy;

@Component
public class LocalProxyFactory implements ProxyFactory {

	private static Logger LOG = LoggerFactory.getLogger(LocalProxyFactory.class);
	private static final String PROXY_FILE = "proxy.ini";
	
	private static List<Proxy> proxies = new ArrayList<Proxy>();
	
	static {
		BufferedReader reader = null;
		try {
			Resource resource = new ClassPathResource(PROXY_FILE);
			InputStream is = resource.getInputStream();
			
			reader = new BufferedReader(new InputStreamReader(is));

			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0)
					continue;
				
				if (line.startsWith("#"))
					continue;
				
				String[] ini = line.split(":");
				if (ini == null || ini.length != 6) continue;
				if (line.indexOf("#") != -1) {
					line = line.substring(0, line.indexOf("#"));
				}
				String realm = ini[0];
				String username = ini[1];
				String password = ini[2];
				String host = ini[3];
				String portStr = ini[4];
				int port = 80;
				try {
					port = Integer.valueOf(portStr);
				} catch (Exception e) {
					
				}
				String type = ini[5];
				Proxy proxy = new Proxy(type, username, password, host, port,realm);
				LOG.info("Put proxy in cache: " + proxy);
				proxies.add(proxy);
			}
			reader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	@Cacheable(value = { "proxyCache" })
	public List<Proxy> getProxies() {
		
//		List<Proxy> proxies = new ArrayList<Proxy>();
//		BufferedReader reader = null;
//		try {
//			Resource resource = new ClassPathResource(PROXY_FILE);
//			InputStream is = resource.getInputStream();
//			
//			reader = new BufferedReader(new InputStreamReader(is));
//
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				line = line.trim();
//				if (line.length() == 0)
//					continue;
//				
//				if (line.startsWith("#"))
//					continue;
//				
//				String[] ini = line.split(":");
//				if (ini == null || ini.length != 6) continue;
//				if (line.indexOf("#") != -1) {
//					line = line.substring(0, line.indexOf("#"));
//				}
//				String realm = ini[0];
//				String username = ini[1];
//				String password = ini[2];
//				String host = ini[3];
//				String portStr = ini[4];
//				int port = 80;
//				try {
//					port = Integer.valueOf(portStr);
//				} catch (Exception e) {
//					
//				}
//				String type = ini[5];
//				Proxy proxy = new Proxy(type, username, password, host, port,realm);
//				LOG.info("Put proxy in cache: " + proxy);
//				proxies.add(proxy);
//			}
//			reader.close();
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//		}
		return proxies;
	}
}
