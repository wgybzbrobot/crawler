package com.zxsoft.carson;


import java.net.MalformedURLException;
import java.net.URL;

import redis.clients.jedis.JedisShardInfo;

public class AppTest {

	 	public static void main(String[] args) throws MalformedURLException {
            String str = "http://tieba.baidu.com/p/1011922232?see_lz=1#pn=1";
            URL url = new URL(str);
            System.out.println(url.getHost());
            
            System.out.println(str.substring(0, str.indexOf("#")));
            
        }
}
