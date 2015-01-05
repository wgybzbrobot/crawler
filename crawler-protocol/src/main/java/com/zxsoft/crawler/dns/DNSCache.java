package com.zxsoft.crawler.dns;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import com.zxisl.commons.cache.ObjectCache;
import com.zxisl.commons.utils.StringUtils;

public class DNSCache {

        public static String getIp(URL url) throws UnknownHostException {
                String ip = "";

                String host = url.getHost();
                ObjectCache ipCache = ObjectCache.get("DNS");
                if (!StringUtils.isEmpty((ip = (String) ipCache.getObject(host)))) {
                        return ip;
                }

                InetAddress address = InetAddress.getByName(url.getHost());
                ip = address.getHostAddress();
                if (!StringUtils.isEmpty(ip)) {
                        ipCache.setObject(host, ip);
                }

                return ip;
        }
}
