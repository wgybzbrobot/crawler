package com.zxsoft.crawler.master.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.zxisl.commons.cache.ObjectCache;
import com.zxisl.commons.utils.StringUtils;

public class DNSCache {

        public static String getIp(String url)  {
                String ip = "";
                if (!url.endsWith("/")) {
                    url = url + "/";
                }
                String host = url.substring(url.indexOf("//") + 2, url.indexOf('/', url.indexOf("//") + 2));
                ObjectCache ipCache = ObjectCache.get("DNS");
                if (!StringUtils.isEmpty((ip = (String) ipCache.getObject(host)))) {
                        return ip;
                }

                InetAddress address;
                try {
                    address = InetAddress.getByName(host);
                    ip = address.getHostAddress();
                    if (!StringUtils.isEmpty(ip)) {
                        ipCache.setObject(host, ip);
                    }
                } catch (UnknownHostException e) {
                }

                return ip;
        }
        
        public static void main(String[] args) {
            String ip = DNSCache.getIp("http://www.sina.com.cn");
            System.out.println(ip);
        }
}
