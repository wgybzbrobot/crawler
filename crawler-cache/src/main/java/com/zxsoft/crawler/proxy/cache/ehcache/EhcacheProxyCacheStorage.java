package com.zxsoft.crawler.proxy.cache.ehcache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.util.StringUtils;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import com.zxsoft.crawler.cache.DefaultProxyCacheEntrySerializer;
import com.zxsoft.crawler.cache.ProxyCache;
import com.zxsoft.crawler.cache.ProxyCacheEntrySerializer;
import com.zxsoft.crawler.cache.ehcache.EhcacheManager;
import com.zxsoft.crawler.proxy.cache.Proxy;
import com.zxsoft.crawler.proxy.cache.ProxyCacheStorage;

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

	// public ProxyEhcache() {
	// ProxyReader proxyReader = new ProxyReader();
	// List<Proxy> proxies = proxyReader.getProxies();
	// for (Proxy proxy : proxies) {
	// put(proxy.getHost() + proxy.getPort(), proxy);
	// }
	// }

//	public void put(String key, Element entry) throws IOException {
//		cache.put(new Element(key, entry));
//	}
//
//	public Element get(String key) throws IOException {
//		if (StringUtils.isEmpty(key)) {
//			List keys = cache.getKeys();
//			int i = (int) (Math.random() * keys.size());
//			return cache.get(keys.get(i));
//		}
//		return cache.get(key);
//	}
//
//	public void remove(String key) throws IOException {
//		cache.remove(key);
//	}

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
