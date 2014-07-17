package com.zxsoft.crawler.protocols.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;

import com.gargoylesoftware.htmlunit.BinaryPage;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.zxsoft.crawler.cache.proxy.Proxy;
import com.zxsoft.crawler.protocols.http.proxy.ProxyRandom;
import com.zxsoft.crawler.storage.WebPageMy;
import com.zxsoft.crawler.util.Utils;

/**
 * <p>Ajax download page.
 * <p>Note: Each thread has its own <code>AjaxLoader</code> object
 */
public final class AjaxLoader {

    private WebClient webClient = new WebClient();
    private HtmlPage htmlPage;
    private long contentLength = Long.MAX_VALUE;
    
    public AjaxLoader() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(20000);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
    }
    
    /**
     * 加载当前页
     * @return page html
     */
    public Document load(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        this.htmlPage= webClient.getPage(url);
        BinaryPage binaryPage = webClient.getPage(url);
        WebResponse response = binaryPage.getWebResponse();
        String charset = response.getContentCharset();
        int statusCode = response.getStatusCode();
        String contentType = response.getContentType();
        List<NameValuePair> headers =  response.getResponseHeaders();
        
        InputStream in = response.getContentAsStream();
        byte[] buffer = new byte[HttpBase.BUFFER_SIZE];
		int bufferFilled = 0;
		int totalRead = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while ((bufferFilled = in.read(buffer, 0, buffer.length)) != -1
		        && totalRead + bufferFilled <= contentLength) {
			totalRead += bufferFilled;
			out.write(buffer, 0, bufferFilled);
		}

		byte[] content = out.toByteArray();
		Document document = Jsoup.parse(this.htmlPage.asXml(), Utils.getHost(url));
		return document;
    }
        
    /**
     * 根据配置加载下一页
     * @return page html
     * @throws IOException 
     * @throws MalformedURLException 
     * @throws FailingHttpStatusCodeException 
     */
    public Document loadNextPage (int pageNum, Document currentDoc) throws IOException {
    	String url = currentDoc.location();
     	if (htmlPage == null) {
    		try {
	            htmlPage = webClient.getPage(url);
            } catch (FailingHttpStatusCodeException | IOException e) {
	            e.printStackTrace();
	            return null;
            }
    	}
     	String host = "";
        try {
	        host = Utils.getHost(url);
        } catch (MalformedURLException e1) {
	        e1.printStackTrace();
	        return null;
        }
     	String pageXml = htmlPage.asXml(); 
     	Document document = Jsoup.parse(pageXml, host);
     	Elements elements = document.select("a:matchesOwn(下一页|下页|下一页>)");
		if (!CollectionUtils.isEmpty(elements)) {
	    	HtmlAnchor nextaAnchor = htmlPage.getAnchorByText(elements.first().text());
	        htmlPage = nextaAnchor.click();
	        pageXml = htmlPage.asXml(); 
	        document = Jsoup.parse(pageXml, host);
	        return document;
		} else { // no "下一页|下页|下一页>"
			Element pagebar = PageHelper.getPageBar(currentDoc);
			Elements achors = pagebar.getElementsByTag("a");
			if (pagebar != null || !CollectionUtils.isEmpty(achors)) {
				for (int i = 0; i < achors.size(); i++) {
					if (Utils.isNum(achors.get(i).text()) && Integer.valueOf(achors.get(i).text().trim()) == pageNum + 1) {
						return load(achors.get(i).absUrl("href"));
					}
				}
			}
		}
		return null;
    }
    
    /**
     * 加载上一页
     * @return page html
     */
    public String loadPrePage (String url) {
       
        return null;
    }
}
