package com.zxsoft.crawler.cache.proxy;

import java.io.IOException;

/**
 * Thrown if serialization or deserialization of an {@link HttpCacheEntry}
 * fails.
 */
public class ProxyCacheEntrySerializationException extends IOException {

    private static final long serialVersionUID = 9219188365878433519L;

    public ProxyCacheEntrySerializationException(final String message) {
        super();
    }

    public ProxyCacheEntrySerializationException(final String message, final Throwable cause) {
        super(message);
        initCause(cause);
    }

}
