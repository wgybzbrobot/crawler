package com.zxsoft.crawler.plugin.parse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.plugin.parse.ext.DateExtractor;
import com.zxsoft.crawler.plugin.parse.ext.DateExtractor2;
import com.zxsoft.crawler.plugin.parse.ext.TextExtract;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.storage.WebPage;

public class SearchParserTest {

    // @SuppressWarnings("deprecation")
    // @Test
    // public void test() throws Exception {
    // String url = "http://www.pagx.cn/html/2014/jrgz_0723/17744.html";
    // HttpFetcher fetcher = new HttpFetcher();
    // WebPage page = new WebPage(url, false);
    //
    // ProtocolOutput output = fetcher.fetch(page);
    // Document _d = output.getDocument();
    //
    // // ArticleTextExtractor extractor = new ArticleTextExtractor();
    // // JResult res = extractor.extractContent(_d.html());
    // // String text = res.getText();
    // System.out.println("内容:" + text);
    // String[] tags = new String[]{"script", "style", "a", "img", "form",
    // "textarea", "h1","h2","h3","h4","dd","footer"};
    // for (String tag : tags) {
    // if (!CollectionUtils.isEmpty(_d.getElementsByTag(tag)))
    // _d.getElementsByTag(tag).remove();
    // }
    //
    // if (null != _d.getElementById("top"))
    // _d.getElementById("top").remove();
    // if (!CollectionUtils.isEmpty(_d.getElementsByClass("menu")))
    // _d.getElementsByClass("menu").remove();
    // if
    // (!CollectionUtils.isEmpty(_d.getElementsMatchingOwnText("客户|联系|Copyright|电话|热线")))
    // _d.getElementsMatchingOwnText("客户|联系|Copyright|电话|热线").remove();
    // Elements eles = _d.body().getAllElements();
    // for (Element ele : eles) {
    // String _t = ele.ownText();
    // if (StringUtils.isEmpty(_t))
    // continue;
    //
    // long _timeIn = DateExtractor.extractInMilliSecs(_t);
    // if (_timeIn <= 0 || _timeIn / 60000L == System.currentTimeMillis() /
    // 60000L)
    // continue;
    // System.out.println(_t +"\nnldp时间:" + new Date(_timeIn).toLocaleString());
    // break;
    // }
    // }

    @Test
    public void test2() throws MalformedURLException, IOException {
        String[] urls = new String[] {
        // "http://m.tianya.cn/bbs/art.jsp?item=45&id=1686087&vu=28456309174",
        // "http://m.tianya.cn/bbs/art.jsp?item=45&id=1686069&vu=28456309174"
        // "http://bj.crntt.com/doc/1037/3/3/9/103733985.html?coluid=50&kindid=1110&docid=103733985"
        // "http://lib.cnki.net/cdmd/10270-2010084526.html"
        // "http://www.chinaso.com/search/link?url=kaVK3z12tTrkGIrigexQNhUGeOOP8N0wZQN+3KZ9OQKSm4ciXKocEi+zjUL82oKOMGActw5SAtOu4nMBSuNwWMD+zQ95Hv/w8wzAhZ02kwY=&_typeid_=130"
        "http://www.chinaso.com/search/pagesearch.htm?q=台湾华航客机坠海"
        // "http://www.baidu.com/s?rtt=2&tn=baiduwb&rn=20&cl=2&wd=%CE%F7%C4%CF%BF%C6%B4%F3%BA%CF%B3%AA%D7%DF%BA%EC",
        // "http://yantai.dzwww.com/2010sy/gdxw/201505/t20150513_12371942.htm"
        };
        for (String url : urls) {
            HttpFetcher fetcher = new HttpFetcher();
            WebPage page = new WebPage(url, false);

            ProtocolOutput output = fetcher.fetch(page);
            Document _d = output.getDocument();
            // Document _d = Jsoup.parse(new URL(url), 10000);

            System.out.println(_d.html());
            _d.select("ul > li").remove();
            String t = TextExtract.parse(_d.html());
            System.out.println(url + "\t" + t);
        }
    }

    @Test
    public void test3() throws Exception {
        String url = "http://m.tianya.cn/bbs/art.jsp?item=news&id=132087&vu=28456309174";
        HttpFetcher fetcher = new HttpFetcher();
        WebPage page = new WebPage(url, false);

        ProtocolOutput output = fetcher.fetch(page);
        Document _d = output.getDocument();
//        System.out.println(_d.text());
        DateExtractor2 dateExtractor2 = new DateExtractor2();
        dateExtractor2.extract(_d);
        System.out.println(new Date(dateExtractor2.getTimeInMs()).toLocaleString() + "\t"
                        + dateExtractor2.getWeight());
    }

}
