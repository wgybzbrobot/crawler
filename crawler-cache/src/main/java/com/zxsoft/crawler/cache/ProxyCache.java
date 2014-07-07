package com.zxsoft.crawler.cache;

import java.io.IOException;

import net.sf.ehcache.Element;

import com.zxsoft.crawler.entity.cache.CacheEntry;

/**
 * A cache, mostly works like a map in lookup, insert and delete. A cache may be
 * persistent over sessions. A cache may clean itself over time.
 *
 */
public interface ProxyCache {

	void put(String key, Element entry) throws IOException;

	/**
	 * Retrieves the cache entry stored under the given key or null if no entry
	 * exists under that key.
	 * 
	 * @param key
	 *            cache key
	 * @return an {@link HttpCacheEntry} or {@code null} if no entry exists
	 * @throws IOException
	 */
	Element get(String key) throws IOException;

	/**
	 * Deletes/invalidates/removes any cache entries currently stored under the
	 * given key.
	 * 
	 * @param key
	 * @throws IOException
	 */
	void remove(String key) throws IOException;

}
