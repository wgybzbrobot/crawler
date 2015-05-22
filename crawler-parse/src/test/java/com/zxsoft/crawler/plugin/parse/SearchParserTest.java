package com.zxsoft.crawler.plugin.parse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import org.jsoup.nodes.Document;
import org.junit.Test;

import com.zxsoft.crawler.plugin.parse.ext.DateExtractor2;
import com.zxsoft.crawler.plugin.parse.ext.TextExtract;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.storage.WebPage;

public class SearchParserTest {

//    @Test
//    public void test2() throws MalformedURLException, IOException {
//        String[] urls = new String[] {
//        // "http://m.tianya.cn/bbs/art.jsp?item=45&id=1686087&vu=28456309174",
//        // "http://m.tianya.cn/bbs/art.jsp?item=45&id=1686069&vu=28456309174"
//        // "http://bj.crntt.com/doc/1037/3/3/9/103733985.html?coluid=50&kindid=1110&docid=103733985"
//        // "http://lib.cnki.net/cdmd/10270-2010084526.html"
//        // "http://www.chinaso.com/search/link?url=kaVK3z12tTrkGIrigexQNhUGeOOP8N0wZQN+3KZ9OQKSm4ciXKocEi+zjUL82oKOMGActw5SAtOu4nMBSuNwWMD+zQ95Hv/w8wzAhZ02kwY=&_typeid_=130"
//        "http://www.chinaso.com/search/link?url=3c96J9zT1P%2Fb5MW7dnF%2FLmBJe4orNfPlz5iKow2vMoaLPcmUOj2q3RRqstb%2BclDe&pos=0&wd=%E5%8F%B0%E6%B9%BE%E5%8D%8E%E8%88%AA%E5%AE%A2"
//        // "http://www.baidu.com/s?rtt=2&tn=baiduwb&rn=20&cl=2&wd=%CE%F7%C4%CF%BF%C6%B4%F3%BA%CF%B3%AA%D7%DF%BA%EC",
//        // "http://yantai.dzwww.com/2010sy/gdxw/201505/t20150513_12371942.htm"
//        };
//        for (String url : urls) {
//            HttpFetcher fetcher = new HttpFetcher();
//            WebPage page = new WebPage(url, false);
//
//            ProtocolOutput output = fetcher.fetch(page);
//            Document _d = output.getDocument();
//            // Document _d = Jsoup.parse(new URL(url), 10000);
//
//            System.out.println(_d.html());
//            _d.select("ul > li").remove();
//            String t = TextExtract.parse(_d.html());
//            System.out.println(url + "\t" + t);
//        }
//    }

    @Test
    public void testTime() throws Exception {
        String url = "http://slide.news.sina.com.cn/s/slide_1_2841_84597.html#p=1";
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
