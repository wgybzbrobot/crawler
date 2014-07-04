package com.zxsoft.crawler.web.download;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.zxsoft.framework.utils.Utils;

/**
 * <p>Ajax download page.
 * <p>Note: Each thread has its own <code>AjaxLoader</code> object
 */
public final class AjaxLoader {

    private WebClient webClient = new WebClient();
    private HtmlPage htmlPage;
    
    public AjaxLoader() {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(50000);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
//        webClient.waitForBackgroundJavaScriptStartingBefore(10000);
    }
    
    /**
     * 加载当前页
     * @return page html
     */
    public Document load(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        this.htmlPage= webClient.getPage(url);
        
        if (htmlPage.getAnchors().size() < 10) {
        	this.htmlPage = webClient.getPage(htmlPage.getUrl());
        }
		
        Document document = Jsoup.parse(this.htmlPage.asXml(), Utils.getHost(url));
		return document;
    }
        
    /**
     * 根据配置加载下一页或上一页
     * @return page html
     * @throws IOException 
     * @throws MalformedURLException 
     * @throws FailingHttpStatusCodeException 
     */
    public Document loadPage (String url, String page) {
    	
        HtmlAnchor nextaAnchor = htmlPage.getAnchorByText(page);
        
        try {
            htmlPage = nextaAnchor.click();
        } catch (UnknownHostException he) { 
            he.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String pageXml = htmlPage.asXml(); 
        Document document = null;
        try {
	        document = Jsoup.parse(pageXml, Utils.getHost(url));
        } catch (MalformedURLException e) {
	        e.printStackTrace();
        }
        
        return document;
    }
    
    /**
     * 加载上一页
     * @return page html
     */
    public String loadPrePage (String url) {
       
        return null;
    }
}
