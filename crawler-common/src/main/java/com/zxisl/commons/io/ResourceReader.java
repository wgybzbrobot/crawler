package org.thinkingcloud.framework.io;

import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceReader {

	private static Logger LOG = LoggerFactory.getLogger(ResourceReader.class);

	private ClassLoader classLoader;
	{
		classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = ResourceReader.class.getClassLoader();
		}
	}
	
	private String resourceName;

	public ResourceReader(String resourseName) {
		this.resourceName = resourseName;
	}

	/**
	 * Get the {@link URL} for the named resource.
	 * 
	 * @return the url for the named resource.
	 */
	public URL getResource() {
		return classLoader.getResource(resourceName);
	}

	public InputStream getResourceAsInputStream() {
		try {
			URL url = getResource();

			if (url == null) {
				LOG.info(resourceName + " not found");
				return null;
			} else {
				LOG.info("found resource " + resourceName + " at " + url);
			}

			return url.openStream();
		} catch (Exception e) {
			return null;
		}
	}
}
