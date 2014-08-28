package org.thinkingcloud.framework.cache;

import java.util.HashMap;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectCache {

	private static final Logger LOG = LoggerFactory.getLogger(ObjectCache.class);

	private static final WeakHashMap<String, ObjectCache> CACHE = new WeakHashMap<String, ObjectCache>();

	private final HashMap<String, Object> objectMap;

	private ObjectCache() {
		objectMap = new HashMap<String, Object>();
	}

	public static ObjectCache get(String name) {
		ObjectCache objectCache = CACHE.get(name);
		if (objectCache == null) {
			LOG.debug("No object cache found for name=" + name
			        + ", instantiating a new object cache");
			objectCache = new ObjectCache();
			CACHE.put(name, objectCache);
		}
		return objectCache;
	}

	public Object getObject(String key) {
		return objectMap.get(key);
	}

	public void setObject(String key, Object value) {
		objectMap.put(key, value);
	}
}
