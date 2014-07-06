package com.zxsoft.crawler.cache.ehcache;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.zxsoft.crawler.cache.entry.Proxy;

public class ProxyReader {

	private static final String PROXY_FILE = "proxy.ini";
	
	public List<Proxy> getProxies() throws IOException {
		List<Proxy> proxies = new ArrayList<Proxy>();
		BufferedReader reader = new BufferedReader(new FileReader(PROXY_FILE));
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0)
				continue;
			
			if (line.startsWith("#"))
				continue;
			String[] ini = line.split(":");
			if (ini == null || ini.length != 4) continue;
			String username = ini[0];
			String password = ini[1];
			String host = ini[2];
			String portStr = ini[3];
			int port = 80;
			try {
				port = Integer.valueOf(portStr);
			} catch (Exception e) {
				
			}
			Proxy proxy = new Proxy(username, password, host, port);
			
			proxies.add(proxy);
		}
		reader.close();
		
		return proxies;
	}
}
