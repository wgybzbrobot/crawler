package com.zxsoft.crawler.master;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.io.ClassPathResource;

import com.zxsoft.crawler.api.Machine;


public class SlaveCache {

	private static Logger LOG = LoggerFactory.getLogger(SlaveCache.class);
	
	private static final String PROXY_FILE = "slaves.ini";

	public static final List<Machine> machines = new ArrayList<Machine>();

	static {
		BufferedReader reader = null;
		try {
			ClassPathResource resource = new ClassPathResource(PROXY_FILE);
			InputStream is = resource.getInputStream();
			
			reader = new BufferedReader(new InputStreamReader(is));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0)
					continue;
				
				if (line.startsWith("#"))
					continue;
				
				String[] ini = line.split(",");
				if (ini == null || ini.length != 4)
					continue;
				if (line.indexOf("#") != -1) {
					line = line.substring(0, line.indexOf("#"));
				}
				String id = ini[0].trim();
				String ip = ini[1].trim();
				String portStr = ini[2].trim();
				String comment = ini[3].trim();
				int port = 8989;
				port = Integer.valueOf(portStr.trim());

				Machine machine = new Machine(id, ip, port, comment);
				LOG.debug("Put machine in cache: " + machine);
				machines.add(machine);
			}
			reader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
