package com.zxsoft.crawler.parse;

import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassLoader extends URLClassLoader {

	/**
	 * Construtor
	 * 
	 * @param urls
	 *            Array of urls with own libraries and all exported libraries of
	 *            plugins that are required to this plugin
	 * @param parent
	 */
	public PluginClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}
}
