package com.zxsoft.crawler.dns;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.conn.DnsResolver;

/**
 * asynchronous resolve DNS,
 *
 */
public class MyDnsResolver implements DnsResolver {

	
	
	@Override
    public InetAddress[] resolve(String host) throws UnknownHostException {
	    
	    return null;
    }

}
