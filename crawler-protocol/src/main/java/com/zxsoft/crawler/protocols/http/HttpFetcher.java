package com.zxsoft.crawler.protocols.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.crawler.protocol.ProtocolStatus;
import com.zxsoft.crawler.protocol.ProtocolStatus.STATUS_CODE;
import com.zxsoft.crawler.protocols.http.htmlunit.HtmlUnit;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClient;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

public class HttpFetcher {

    private static Logger LOG = LoggerFactory.getLogger(HttpFetcher.class);

    private static HttpBase htmlUnit = new HtmlUnit();
    private static HttpBase httpClient = new HttpClient();

    public ProtocolOutput fetch(String url, NameValuePair[] data) throws IOException {
        return httpClient.post(url, data);
    }

    public ProtocolOutput fetch(WebPage page) {

        ProtocolOutput protocolOutput = new ProtocolOutput();
        String url = page.getBaseUrl();

//        if (!url.contains("%")) {
            try {
                URL u = new URL(url);
                try {
                    url = URLDecoder.decode(url, "UTF-8");
                    url = URIUtil.encodePathQuery(url, "UTF-8");
                    page.setUrl(url);
                } catch (URIException  |UnsupportedEncodingException e) {
                    LOG.error("URIUtil.encodePathQuery(url, utf-8) error.", e);
                }
            } catch (MalformedURLException e1) {
                ProtocolStatus status = new ProtocolStatus();
                status.setCode(STATUS_CODE.INVALID_URL);
                protocolOutput = new ProtocolOutput(null, status);
                return protocolOutput;
            }
//        }

        try {
            if (page.isAjax()) {
                protocolOutput = htmlUnit.getProtocolOutput(page);
            } else {
                protocolOutput = httpClient.getProtocolOutput(page);
            }

            while (protocolOutput.getStatus().getCode().equals(STATUS_CODE.TEMP_MOVED)
                            && !StringUtils.isEmpty(protocolOutput.getStatus().getU())
                            && !protocolOutput.getStatus().getU().equals(url)) {
                page.setUrl(protocolOutput.getStatus().getU());
                if (page.isAjax()) {
                    protocolOutput = htmlUnit.getProtocolOutput(page);
                } else {
                    protocolOutput = httpClient.getProtocolOutput(page);
                }
            }
            
            if (STATUS_CODE.NOTFOUND.equals(protocolOutput.getStatus().getCode()) 
                            || STATUS_CODE.ACCESS_DENIED.equals(protocolOutput.getStatus().getCode())
                            || protocolOutput.getDocument() == null) {
            } else {
                if (protocolOutput.getDocument() != null) {
                    Document _d = protocolOutput.getDocument();
                    Elements scripts = _d.getElementsByTag("script");
                    if (!CollectionUtils.isEmpty(scripts) && scripts.size() < 5) {
                        for (Element script : scripts) {
                            if (!StringUtils.isEmpty(script.html())) {
                                Pattern _pa = Pattern.compile("(window\\.location\\.replace\\(\"\\S+\"\\))"
                                                + "|(window\\.location\\.href=\"\\S+\")"
                                                + "|(self\\.location=\"\\S+\")"
                                                + "|(top\\.location=\"\\S+\")");
                                Matcher _ma = _pa.matcher(script.html());
                                if (_ma.find()) {
                                    String _u = _ma.group(0);
                                    Pattern _p = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
                                    Matcher _m = _p.matcher(_u);
                                    if (_m.find()) {
                                        _u = _m.group(0);
                                        if (!StringUtils.isEmpty(_u)) {
                                            try {
                                                URL _url = new URL(_u);
                                                page.setUrl(_u);
                                                protocolOutput = fetch(page);
                                            } catch (Exception e) {
                                                protocolOutput.setStatus(new ProtocolStatus(url, STATUS_CODE.FAILED, "Not valid url"));
                                            }
                                        }else {
                                            protocolOutput.setStatus(new ProtocolStatus(url, STATUS_CODE.FAILED, "url length is zero"));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ProtocolException e) {
            String msg = "Fetch " + url + " failed with protocol exception: "
                            + e.getMessage();
            protocolOutput.setStatus(new ProtocolStatus(url, STATUS_CODE.FAILED, msg));
        } catch (IOException e) {
            String msg = "Fetch " + url + " failed with io exception: " + e.getMessage();
            protocolOutput.setStatus(new ProtocolStatus(url, STATUS_CODE.FAILED, msg));
            LOG.error(msg);
        } catch (Exception e) {
            String msg = "Fetch " + url + " failed with the following exception:"
                            + e.getMessage();
            protocolOutput.setStatus(new ProtocolStatus(url, STATUS_CODE.FAILED, msg));
            LOG.error(msg, e);
        } finally {
            return protocolOutput;
        }
    }

    public ProtocolOutput fetchNextPage(int pageNum, WebPage page)
                    throws PageBarNotFoundException {
        if (!page.isAjax()) {
            return httpClient.getProtocolOutputOfNextPage(pageNum, page);
        } else {
            return htmlUnit.getProtocolOutputOfNextPage(pageNum, page);
        }
    }

    public ProtocolOutput fetchPrevPage(int pageNum, WebPage page)
                    throws PrevPageNotFoundException, PageBarNotFoundException {
        if (!page.isAjax()) {
            return httpClient.getProtocolOutputOfPrevPage(pageNum, page);
        } else {
            return htmlUnit.getProtocolOutputOfPrevPage(pageNum, page);
        }
    }

    public ProtocolOutput fetchLastPage(WebPage page) throws PageBarNotFoundException {
        if (!page.isAjax()) {
            return httpClient.getProtocolOutputOfLastPage(page);
        } else {
            return htmlUnit.getProtocolOutputOfLastPage(page);
        }
    }
}
