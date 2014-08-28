package com.zxsoft.proxy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.thinkingcloud.framework.util.StringUtils;

public class LocalProxyFactory implements ProxyFactory {

	private static Logger LOG = LoggerFactory.getLogger(LocalProxyFactory.class);
	private static final String PROXY_FILE = "proxy.ini";

	private static final List<Proxy> proxies = new ArrayList<Proxy>();

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
				if (ini == null || ini.length != 3)
					continue;
				if (line.indexOf("#") != -1) {
					line = line.substring(0, line.indexOf("#"));
				}
				String type = ini[0];
				String host = ini[1];
				String portStr = ini[2];
				String username = "";
				String password = "";
				int port = 80;
				try {
					port = Integer.valueOf(portStr);
				} catch (Exception e) {

				}
				Proxy proxy = new Proxy(type,  host, port, username, password);
				LOG.debug("Put proxy in cache: " + proxy);
				proxies.add(proxy);
			}
			reader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public List<Proxy> getProxies(String type) {
		if (StringUtils.isEmpty(type))
			return proxies;

		List<Proxy> result = new LinkedList<Proxy>();
		for (Proxy proxy : proxies) {
			if (type.equals(proxy.getTargetType())) {
				result.add(proxy);
			}
		}
		return result;
	}
}
