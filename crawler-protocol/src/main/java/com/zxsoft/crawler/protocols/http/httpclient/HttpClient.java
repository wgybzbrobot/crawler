package com.zxsoft.crawler.protocols.http.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocol.util.EncodingDetector;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.storage.WebPage;
import com.zxsoft.crawler.util.Utils;
import com.zxsoft.crawler.util.page.PageBarNotFoundException;
import com.zxsoft.crawler.util.page.PageHelper;
import com.zxsoft.crawler.util.page.PrevPageNotFoundException;

public class HttpClient extends HttpBase {

    public static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);
    private static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

    private static org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient(
                    connectionManager);
    private int maxThreadsTotal = 30;

    // 统计请求次数和超时次数
    // private AtomicLong requestCount = new AtomicLong();
    // private AtomicLong timeoutCount = new AtomicLong();

    public HttpClient() {
        // setup();
        configureClient();
    }

    private void configureClient() {
        ProtocolSocketFactory factory = new SSLProtocolSocketFactory();
        Protocol https = new Protocol("https", factory, 443);
        Protocol.registerProtocol("https", https);
        HttpConnectionManagerParams params = connectionManager.getParams();
        params.setConnectionTimeout(timeout); // 连接时间
        params.setSoTimeout(sotimeout); // 设置socket timeout, 读数据的时间
        params.setSendBufferSize(BUFFER_SIZE);
        params.setReceiveBufferSize(BUFFER_SIZE);
        params.setDefaultMaxConnectionsPerHost(32);
        params.setMaxTotalConnections(256);
        // params.setMaxTotalConnections(maxThreadsTotal);
        client.getParams().setConnectionManagerTimeout(timeout);
        HostConfiguration hostConf = client.getHostConfiguration();
        ArrayList<Header> headers = new ArrayList<Header>();
        headers.add(new Header("User-Agent", userAgent));
        headers.add(new Header("Accept-Language", acceptLanguage));
        headers.add(new Header("Accept-Charset", acceptCharset));
        headers.add(new Header("Accept", accept));
        headers.add(new Header("Connection", "keep-alive"));
        headers.add(new Header("Accept-Encoding", "x-gzip, gzip, deflate"));
        hostConf.getParams().setParameter("http.default-headers", headers);
        if (useProxy) {
            hostConf.setProxy(proxyHost, proxyPort);
            if (!StringUtils.isEmpty(proxyUsername)
                            && !StringUtils.isEmpty(proxyPassword)) {
                UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                                proxyUsername, proxyPassword);
                client.getState().setProxyCredentials(AuthScope.ANY, credentials);
            }
        }
    }

    @Override
    public Response getResponse(WebPage page) throws ProtocolException, IOException {

        int code = -1;
        Metadata metadata = new Metadata();
        byte[] content = null/* new byte[1024] */;

        URL url = null;
        try {
            url = new URL(page.getBaseUrl());
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return null;
        }

        GetMethod get = new GetMethod(url.toString());
        get.setFollowRedirects(false);
        HttpMethodParams params = get.getParams();
        params.makeLenient();
        params.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        params.setBooleanParameter(HttpMethodParams.SINGLE_COOKIE_HEADER, true);
        String _charset = "";
        try {
            code = client.executeMethod(get);

            
            Header[] heads = get.getResponseHeaders();
            for (int i = 0; i < heads.length; i++)
                metadata.set(heads[i].getName(), heads[i].getValue());

            if (code == 302 || code == 301) {
                String u = get.getResponseHeader("Location").getValue();
                if (!StringUtils.isEmpty(u))
                    url = new URL(u);
            }

            String contentType = metadata.get(Response.CONTENT_TYPE);
            InputStream in = get.getResponseBodyAsStream();
            try {
                content = IOUtils.toByteArray(in);
                if (StringUtils.isEmpty(page.getEncode())) {
                    EncodingDetector detector = new EncodingDetector();
                    detector.detect(contentType, content, url);
                    _charset = detector.getCharset();
                } else {
                    _charset = page.getEncode();
                }
            } catch (Exception e) {
                if (code == 200) {
                    LOG.error(e.getMessage() + ": " + url.toExternalForm());
                    throw new IOException(e.getMessage());
                }
            } finally {
                if (in != null)
                    in.close();
            }
            if (content != null) {
                // check if we have to uncompress it
                String contentEncoding = metadata.get(Response.CONTENT_ENCODING);
                if ("gzip".equalsIgnoreCase(contentEncoding) || "x-gzip".equals(contentEncoding)) {
                    content = processGzipEncoded(content, url);
                } else if ("deflate".equals(contentEncoding)) {
                    content = processDeflateEncoded(content, url);
                }
            }
        } catch (SocketException e) {
            code = -2;
            LOG.error("SocketException:" + e.getMessage() + ": " + url.toString());
        } catch (Exception e) {
            // timeoutCount.addAndGet(1);
            // LOG.debug("timeoutCount:" + timeoutCount.get());
            throw new IOException(e.getMessage());
        } finally {

            // requestCount.addAndGet(1);
            // LOG.debug("requestCount:" + requestCount.get());

            get.releaseConnection();
        }

        return new Response(url, code, metadata, content, _charset);
    }

    @Override
    public Response postForResponse(URL url, NameValuePair[] data) throws IOException {
        PostMethod post = new PostMethod(url.toString());
        post.setRequestBody(data);
        HttpMethodParams params = post.getParams();
        params.makeLenient();
        params.setContentCharset("UTF-8");
        params.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // params.setParameter("http.protocol.cookie-policy",
        // CookiePolicy.BROWSER_COMPATIBILITY);
        params.setBooleanParameter(HttpMethodParams.SINGLE_COOKIE_HEADER, true);

        String _charset = "";
        try {
            code = client.executeMethod(post);
            // post.getResponseBodyAsString();
            Header[] cookies = post.getResponseHeaders("Set-Cookie");
            StringBuilder sb = new StringBuilder();
            for (Header cookie : cookies) {
                sb.append(cookie.getValue());
            }
            // com.zxsoft.crawler.protocols.http.CookieStore.put(NetUtils.getHost(url),
            // sb.toString());

            headers.set("Cookie", sb.toString());

            Header[] heads = post.getRequestHeaders();
            for (int i = 0; i < heads.length; i++)
                headers.set(heads[i].getName(), heads[i].getValue());

            String contentType = headers.get(Response.CONTENT_TYPE);
            // charset = EncodingDetector.detect(contentType);
            InputStream in = post.getResponseBodyAsStream();
            try {
                content = IOUtils.toByteArray(in);
                // if (StringUtils.isEmpty(charset)) {
                // _charset = EncodingDetector.detect(contentType, content);
                EncodingDetector detector = new EncodingDetector();
                detector.detect(contentType, content, url);
                _charset = detector.getCharset();
                // LOG.debug("使用icu得到编码:" + charset);
                // }
            } catch (Exception e) {
                if (code == 200) {
                    LOG.error(e.getMessage(), e);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                post.abort();
            }
            if (content != null) {
                // check if we have to uncompress it
                String contentEncoding = headers.get(Response.CONTENT_ENCODING);
                if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
                    content = processGzipEncoded(content, url);
                } else if ("deflate".equals(contentEncoding)) {
                    content = processDeflateEncoded(content, url);
                }
            }
        } finally {
            post.releaseConnection();
        }
        return new Response(url, code, headers, content, _charset);
    }

    @Override
    protected Response loadPrevPage(int pageNum, final WebPage page)
                    throws ProtocolException, IOException, PrevPageNotFoundException,
                    PageBarNotFoundException {
        Document currentDoc = page.getDocument();
        Elements elements = null;
        elements = currentDoc.select("a:matchesOwn(上一页|上页|<上一页)");
        URL url = null;
        if (!CollectionUtils.isEmpty(elements)) {
            url = new URL(elements.first().absUrl("href"));
        } else if (pageNum > 1) {
            Element pagebar = getPageBar(currentDoc);
            if (pagebar != null) {
                Elements achors = pagebar.getElementsByTag("a");
                if (pagebar != null || !CollectionUtils.isEmpty(achors)) {
                    for (int i = 0; i < achors.size(); i++) {
                        if (Utils.isNum(achors.get(i).text())
                                        && Integer.valueOf(achors.get(i).text().trim()) == pageNum - 1) {
                            url = new URL(achors.get(i).absUrl("href"));
                        }
                    }
                }
            }
        } else {
            url = PageHelper.calculatePrevPageUrl(currentDoc);
        }
        if (url != null) {
            WebPage np = page;
            np.setUrl(url.toExternalForm());

            Response response = null;
            for (int i = 0; i < retryNum; i++) {
                try {
                    response = getResponse(np);
                } catch (IOException e) {
                    LOG.debug("IOException, try again");
                    continue;
                }
                break;
            }
            return response;
        }

        throw new PrevPageNotFoundException("Preview Page Not Found");
    }

    @Override
    protected Response loadNextPage(int pageNum, final WebPage page)
                    throws ProtocolException, IOException, PageBarNotFoundException {
        Document currentDoc = page.getDocument();
        Elements elements = null;
        elements = currentDoc.select("a:matchesOwn(下一页|下页|下一页>)");
        // System.out.println(currentDoc);
        if (!CollectionUtils.isEmpty(elements)) {
            WebPage np = page;
            String next = elements.first().absUrl("href");
            if (StringUtils.isEmpty(next)) {
                throw new PageBarNotFoundException();
            }
//            LOG.info(next);
//            next = URLDecoder.decode(next, "UTF-8");
            np.setUrl(next);

            Response response = null;
            for (int i = 0; i < retryNum; i++) {
                try {
                    response = getResponse(np);
                } catch (IOException e) {
                    LOG.debug("IOException, try again");
                    continue;
                }
                break;
            }

            return response;
        } else {
            Element pagebar = getPageBar(currentDoc);
            if (pagebar != null) {
                Elements achors = pagebar.getElementsByTag("a");
                if (pagebar != null || !CollectionUtils.isEmpty(achors)) {
                    for (int i = 0; i < achors.size(); i++) {
                        if (Utils.isNum(achors.get(i).text())
                                        && Integer.valueOf(achors.get(i).text().trim()) == pageNum + 1) {
                            WebPage np = page;
                            np.setUrl(achors.get(i).absUrl("href"));
                            return getResponse(np);
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected Response loadLastPage(WebPage page) throws ProtocolException, IOException,
                    PageBarNotFoundException {
        Document currentDoc = page.getDocument();
        Elements lastEles = currentDoc.select("a:matchesOwn(尾页|末页|最后一页)");
        if (!CollectionUtils.isEmpty(lastEles)) {
            WebPage np = page;
            np.setUrl(lastEles.first().absUrl("href"));
            Response response = null;
            for (int i = 0; i < retryNum; i++) {
                try {
                    response = getResponse(np);
                } catch (IOException e) {
                    LOG.debug("IOException, try again");
                    continue;
                }
                break;
            }
            return response;
        }

        // 1. get all links from page bar
        Element pagebar = getPageBar(currentDoc);
        if (pagebar == null)
            return null;
        Elements links = pagebar.getElementsByTag("a");
        if (CollectionUtils.isEmpty(links)) {
            return null;
        }

        // 2. get max num or contains something in all links, that is last page
        int i = 1;
        Element el = null;
        for (Element ele : links) {
            String v = ele.text();
            if ("18255266882".equals(v)) {
                continue;
            }
            if (Utils.isNum(v) && Integer.valueOf(v) > i) { // get max num
                i = Integer.valueOf(v);
                el = ele;
            }
        }
        if (el == null || StringUtils.isEmpty(el.absUrl("href"))) {
            return null;
        }
        // LOG.info("Last Page url: " + url.toString());
        WebPage np = page;
        np.setUrl(el.absUrl("href"));
        return getResponse(np);
    }
}