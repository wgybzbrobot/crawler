package com.zxsoft.crawler.util;

import java.util.Properties;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;

public class CrawlerConfiguration {

	public static final String UUID_KEY = "crawler.conf.uuid";

	private CrawlerConfiguration() {
	} // singleton

	/**
	 * Configuration.hashCode() doesn't return values that correspond to a
	 * unique set of parameters. This is a workaround so that we can track
	 * instances of Configuration created by Nutch.
	 */
	private static void setUUID(Configuration conf) {
		UUID uuid = UUID.randomUUID();
		conf.set(UUID_KEY, uuid.toString());
	}

	/**
	 * Retrieve a Nutch UUID of this configuration object, or null if the
	 * configuration was created elsewhere.
	 * 
	 * @param conf
	 *            configuration instance
	 * @return uuid or null
	 */
	public static String getUUID(Configuration conf) {
		return conf.get(UUID_KEY);
	}

	/**
	 * Create a {@link Configuration} for Nutch. This will load the standard
	 * Nutch resources, <code>nutch-default.xml</code> and
	 * <code>nutch-site.xml</code> overrides.
	 */
	public static Configuration create() {
		Configuration conf = new Configuration();
		setUUID(conf);
		addCrawlerResources(conf);
		return conf;
	}

	/**
	 * Create a {@link Configuration} from supplied properties.
	 * 
	 * @param addNutchResources
	 *            if true, then first <code>nutch-default.xml</code>, and then
	 *            <code>nutch-site.xml</code> will be loaded prior to applying
	 *            the properties. Otherwise these resources won't be used.
	 * @param nutchProperties
	 *            a set of properties to define (or override)
	 */
	public static Configuration create(boolean addNutchResources,
			Properties nutchProperties) {
		Configuration conf = new Configuration();
		setUUID(conf);
		if (addNutchResources) {
			addCrawlerResources(conf);
		}
		for (Entry<Object, Object> e : nutchProperties.entrySet()) {
			conf.set(e.getKey().toString(), e.getValue().toString());
		}
		return conf;
	}

	/**
	 * Add the standard Nutch resources to {@link Configuration}.
	 * 
	 * @param conf
	 *            Configuration object to which configuration is to be added.
	 */
	private static Configuration addCrawlerResources(Configuration conf) {
		conf.addResource("crawler-default.xml");
		return conf;
	}
}
