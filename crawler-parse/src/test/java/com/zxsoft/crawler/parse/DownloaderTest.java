package com.zxsoft.crawler.parse;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloaderTest {

    private Logger LOG = LoggerFactory.getLogger(DownloaderTest.class);
    
    public void testConnect() {
        String url = "http://tieba.baidu.com/p/1024737673";
        try {
            LOG.info("connecting " + url);
            Connection connection = Jsoup.connect(url);
            connection.followRedirects(false);
            connection.get();
            Response response =  connection.response();
            String charset = response.charset();   
            String lastModified = response.header("last-modified");
            int statusCode = response.statusCode();
            LOG.info("charset:" + charset);
            LOG.info("lastModified:" + lastModified);
            LOG.info("status code:" + statusCode);
            LOG.info("status message:" +  response.statusMessage());
            LOG.info("content type:" + response.contentType());
        } catch (Exception exception) {
            
        }
        
    }
    
    public void testHttpClent() throws HttpException, IOException {
        String str = "http://tieba.baidu.com/p/1024737673";
        HttpClient client = new HttpClient();
        int code = 0;
        
        HttpMethod get = new GetMethod(str);
        get.setFollowRedirects(false);
        get.setDoAuthentication(true);

        // Set HTTP parameters
        HttpMethodParams params = get.getParams();
        params.setVersion(HttpVersion.HTTP_1_1);
        params.makeLenient();
        params.setContentCharset("UTF-8");
        params.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        params.setBooleanParameter(HttpMethodParams.SINGLE_COOKIE_HEADER, true);
        code = client.executeMethod(get);
        
        LOG.info("response code: " + code);
    }
    
    public static void main(String[] args) throws HttpException, IOException {
        DownloaderTest test = new DownloaderTest();
        test.testConnect();
        
        System.out.println("-----------");
        
//        test.testHttpClent();
    }
    
    
}
