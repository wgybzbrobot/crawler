package com.zxsoft.crawler.cache.proxy.ehcache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import com.zxsoft.crawler.cache.proxy.DefaultProxyCacheEntrySerializer;
import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.cache.proxy.ProxyCacheEntrySerializer;
import com.zxsoft.crawler.cache.proxy.ProxyCacheStorage;

public class EhcacheProxyCacheStorage implements ProxyCacheStorage {

	private final Ehcache cache;
	private final ProxyCacheEntrySerializer serializer;

	public EhcacheProxyCacheStorage(final Ehcache cache) {
		this(cache, new DefaultProxyCacheEntrySerializer());
	}

	public EhcacheProxyCacheStorage(final Ehcache cache, final ProxyCacheEntrySerializer serializer) {
		this.cache = cache;
		this.serializer = serializer;
	}

	public synchronized void putEntry(String key, Proxy entry) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		serializer.writeTo(entry, bos);
		cache.put(new Element(key, bos.toByteArray()));
	}

	public Proxy getEntry(String key) throws IOException {
		final Element e = cache.get(key);
		if (e == null) {
			return null;
		}

		final byte[] data = (byte[]) e.getValue();
		return serializer.readFrom(new ByteArrayInputStream(data));
	}

	public void removeEntry(String key) throws IOException {
		cache.remove(key);
	}

}
