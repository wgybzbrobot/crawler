package com.zxsoft.crawler.plugin.parse;

import java.net.MalformedURLException;
import java.net.URL;
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
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
import com.zxsoft.crawler.protocols.http.HttpFetcher;
import com.zxsoft.crawler.storage.WebPage;

import de.jetwick.snacktory.ArticleTextExtractor;
import de.jetwick.snacktory.HtmlFetcher;
import de.jetwick.snacktory.JResult;

public class SearchParserTest {

    @SuppressWarnings("deprecation")
    @Test
    public void test() throws Exception {
        // String url =
        // "http://www.chinaso.com/search/link?url=SVqFHp4HkY%2B5XiwakUkzaItzoYM8KrtnzZNXx2wDsC2idYE%2BiXvrPkIiNzhYYeR1VW4P8vtooF5421FR5kk8xVQHGv5DZ0AAtFe8TsNvSd%2FHxFtvYW9qf5%2FgqmOFohCZMP2LKT6qS8QsfSr18cp%2FDMWABNNxmt%2B6urJekLhVJtSXnGSc69mNRz9OyKQJ4dJB&pos=2&wd=%E4%BF%84%E7%BD%97%E6%96%AF%E5%B0%8F%E5%AD%A9";
        // String url =
        // "http://m.tianya.cn/bbs/art.jsp?item=funinfo&id=1204587&vu=28456309174";
        String url = "http://news.xinhuanet.com/politics/2013-12/07/c_118463079.htm";
        HttpFetcher fetcher = new HttpFetcher();
        WebPage page = new WebPage(url, false);

        ProtocolOutput output = fetcher.fetch(page);
        // if (STATUS_CODE.TEMP_MOVED.equals(output.getStatus().getCode())) {
        // url = output.getDocument().location();
        // page = new WebPage(url, false);
        // output = fetcher.fetch(page);
        // }
        Document _d = output.getDocument();
        // System.out.println(doc.html());

        // Elements hrefs =
        // doc.getElementsContainingText("window.location.href");
        // System.out.println("hrefs:" + hrefs.html());

        _d.getElementsByTag("script").remove();
        _d.getElementsByTag("style").remove();
        _d.getElementsByTag("a").remove();
        _d.getElementsByTag("img").remove();
        _d.getElementsByTag("form").remove();
        _d.getElementsByTag("textarea").remove();
        _d.getElementsByTag("h1").remove();
        _d.getElementsByTag("h2").remove();
        _d.getElementsByTag("h3").remove();
        _d.getElementsByTag("h4").remove();
        _d.getElementsByTag("h5").remove();
        _d.getElementsByTag("dd").remove();
        _d.getElementsByTag("footer").remove();
        _d.getElementsByClass("menu").remove();
        _d.getElementsMatchingOwnText("客户|联系|Copyright|电话|热线").remove();

        // Elements _eles = _d.getElements("Copyright");
        // Element _ele = _eles.last();

        System.out.println(_d.body().html());
        Elements eles = _d.body().getAllElements();

        for (Element ele : eles) {
            String _t = ele.ownText();
            if (StringUtils.isEmpty(_t))
                continue;
            System.out.println(_t);
            _t = _t.replaceAll("(昨|前天)", "");
            long _timeIn = DateExtractor.extractInMilliSecs(_t);

            if (_timeIn <= 0 || _timeIn / 60000L == System.currentTimeMillis() / 60000L)
                continue;
            System.out.println("nldp时间:" + new Date(_timeIn).toLocaleString());
            break;
        }

        // System.out.println(doc.text());

        ArticleTextExtractor extractor = new ArticleTextExtractor();
        JResult res = extractor.extractContent(_d.html());

        String text = res.getText();
        String title = res.getTitle();
        String imageUrl = res.getImageUrl();
        // System.out.println(text);
        // System.out.println(title);
        // System.out.println(imageUrl);

    }

    public static void main(String[] args) {

        String str0 = "<!DOCTYPE html PUBLIC -//IETF//DTD HTML 2.0//EN\"><html> <head>"
                        + "<script>window.opener=null;window.location.replace(\"http://baike.baidu.com/view/2025408.htm\");</script>"
                        + "<noscript><meta http-equiv=\"refresh\" content=\"0;URL='http://baike.baidu.com/view/2025408.htm'\" />"
                        + "</noscript></html>";
        String str1 = "<script>window.location.replace(\"http://baike.baidu.com/view/1.htm\");</script>";
        String str2 = "<script>window.location.href=\"http://baike.baidu.com/view/2.htm\";</script>";
        String str3 = "<script>self.location=\"http://baike.baidu.com/view/3.htm\";</script>";
        String str4 = "<script>top.location='http://baike.baidu.com/view/4.htm';</script>";
        String str5 = "<script>top.location=http://baike.baidu.com/view/5.htm;</script>";

        String[] strs = new String[] { str0, str1, str2, str3, str4, str5 };
        for (String str : strs) {
            Document _d = Jsoup.parse(str);
            Elements _eles = _d.getElementsByTag("script");
            if (!CollectionUtils.isEmpty(_eles)) {
                for (Element _ele : _eles) {
                    if (!StringUtils.isEmpty(_ele.html())) {
                        Pattern _pa = Pattern.compile("(window\\.location\\.replace\\(\"\\S+\"\\))"
                                        + "|(window\\.location\\.href=\"\\S+\")"
                                        + "|(self\\.location=\"\\S+\")"
                                        + "|(top\\.location=\"\\S+\")");
                        Matcher _ma = _pa.matcher(_ele.html());
//                        Matcher _ma = _pa.matcher(str);
                        if (_ma.find()) {
                            String _u = _ma.group(0);
                            System.out.println(_u);
                            Pattern _p = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
                            Matcher _m = _p.matcher(_u);
                            if (_m.find()) {
                                _u = _m.group(0);
                                System.out.println(_u);
                            }
                        }
                    }
                }
            }
        }

    }
}
