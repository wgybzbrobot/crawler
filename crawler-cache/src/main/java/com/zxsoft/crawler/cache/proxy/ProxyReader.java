package com.zxsoft.crawler.cache.proxy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyReader {

	private static Logger LOG = LoggerFactory.getLogger(ProxyReader.class);
	private static final String PROXY_FILE = "src/main/resources/proxy.ini";
	
	public List<Proxy> getProxies() throws IOException   {
		List<Proxy> proxies = new ArrayList<Proxy>();
		BufferedReader reader = null;
        try {
	        reader = new BufferedReader(new FileReader(PROXY_FILE));
        } catch (FileNotFoundException e1) {
        	e1.printStackTrace();
        	return null;
        }
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0)
				continue;
			
			if (line.startsWith("#"))
				continue;
			
			String[] ini = line.split(":");
			if (ini == null || ini.length != 4) continue;
			if (line.indexOf("@") != -1) {
				line = line.substring(0, line.indexOf("@"));
			}
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
			LOG.info("Put proxy in cache: " + proxy);
			proxies.add(proxy);
		}
		reader.close();
		
		return proxies;
	}
}
