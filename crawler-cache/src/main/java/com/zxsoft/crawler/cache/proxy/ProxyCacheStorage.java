package com.zxsoft.crawler.cache.proxy;

import java.io.IOException;

public interface ProxyCacheStorage {

	 /**
     * Store a given cache entry under the given key.
     * @param key where in the cache to store the entry
     * @param entry cached response to store
     * @throws IOException
     */
    void putEntry(String key, Proxy entry) throws IOException;

    /**
     * Retrieves the cache entry stored under the given key
     * or null if no entry exists under that key.
     * @param key cache key
     * @return an {@link HttpCacheEntry} or {@code null} if no
     *   entry exists
     * @throws IOException
     */
    Proxy getEntry(String key) throws IOException;

    /**
     * Deletes/invalidates/removes any cache entries currently
     * stored under the given key.
     * @param key
     * @throws IOException
     */
    void removeEntry(String key) throws IOException;

}
