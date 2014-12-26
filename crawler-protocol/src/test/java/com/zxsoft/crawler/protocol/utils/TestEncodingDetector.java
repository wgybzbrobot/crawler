package com.zxsoft.crawler.protocol.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;

import com.zxsoft.crawler.protocol.util.EncodingDetector;

public class TestEncodingDetector {

        @Test
        public void testGetEncode() throws HttpException, IOException {
                HttpClient client = new HttpClient();
                List<String> urls = new ArrayList<String>();
                urls.add("http://www.qq.com");
                urls.add("http://bbs.ahwang.cn/forum-156-1.html");
                urls.add("http://www.sina.com.cn/");

                for (String url : urls) {
                        HttpMethod get = new GetMethod(url);
                        EncodingDetector detector = new EncodingDetector();
                        client.executeMethod(get);
                        InputStream is = get.getResponseBodyAsStream();
                        detector.detect(is);
                        get.releaseConnection();
                        System.out.println(url + " Charset: " + detector.getCharset());
                }

        }
}
