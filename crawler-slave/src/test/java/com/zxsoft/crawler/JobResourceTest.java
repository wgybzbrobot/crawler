package com.zxsoft.crawler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.resource.ClientResource;

import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.api.SlaveServer;
import com.zxsoft.crawler.common.DetailRule;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.common.ListRule;

public class JobResourceTest {

	private static String baseUrl = "http://localhost:8989/slave/jobs";
	private static SlaveServer server;
	@BeforeClass
    public static void before() throws Exception {
        server = new SlaveServer(8989);
        server.start();
    }
    
    @AfterClass
    public static void after() throws Exception {
        if (!server.stop(false)) {
            for (int i = 1; i < 15; i++) {
                System.err.println("Waiting for jobs to complete - " + i + "s");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                };
                server.stop(false);
                if (!SlaveServer.isRunning()) {
                    break;
                }
            }
        }
        if (SlaveServer.isRunning()) {
            System.err.println("Forcibly stopping server...");
            server.stop(true);
        }
    }
    
	@Test
	public void testCreateNetworkInspectJob() throws IOException {
		
        int source_id = 4;
        String source_name = "合肥论坛";
        String url = "http://bbs.hefei.cc/forum-196-1.html";
        String type = "合肥专区";
        int sectionId = 807;
        ListRule listRule = new ListRule(false, "forum", "div#threadlist ", "tbody[id^=normalthread]", "a.xst", "td.by em span", "td.by em a", "", "");
        DetailRule detailRule = new DetailRule("bbs.hefei.cc", "div#ct  p.hm  span:eq(1)  em:eq(1)", 
                        "div#ct  p.hm  span:eq(1)  em:eq(0)", "", "", true, false, "div#postlist div[id^=post_]", 
                        "div.pi div.authi  a.xw1", "", "div.pcb table td", "div#postlist > div[id^=post_]:gt(0)", 
                        "div.pi div.authi  a.xw1", "div.authi  em[id^=authorpost]", "div.pcb > div.t_fsz  table  td", "", "", "", "");
        Set<DetailRule> detailRules = new HashSet<DetailRule>();
        detailRules.add(detailRule);
        
        JobConf job = new JobConf(JobType.NETWORK_INSPECT, url, source_name, source_id, sectionId, type, listRule, detailRules);
        job.setPrevFetchTime( System.currentTimeMillis() - 24 * 60 * 60 * 1000L);
        job.setLocation( "江苏省南京市 电信");
        job.setIp("221.231.141.168");
        job.setLocationCode(320000);
        job.setWorkerId(321);
        job.setCountry_code(1);
        job.setProvince_code(340000);
        job.setCity_code(340100);
        
        ClientResource client = new ClientResource(baseUrl);
		client.post(job);
	}

	@Test
	public void testCreateNetworkSearchJob() throws IOException {
		
        int source_id = 10;
        String source_name = "百度搜索";
        String url = "http://www.baidu.com/s?wd=%s";
        String type = "百度搜索吸毒";
        int sectionId = 51;
        ListRule listRule = new ListRule(false, "search", "div#content_left ", "div.c-container", "h3.t > a", "div.f13 span", "", "div.c-abstract", "");
        Set<DetailRule> detailRules = null;
        
        JobConf job = new JobConf(JobType.NETWORK_SEARCH, url, source_name, source_id, sectionId, type, listRule, detailRules);
        job.setKeyword("吸毒");
        job.setPrevFetchTime( System.currentTimeMillis() - 24 * 60 * 60 * 1000L);
        job.setLocation( "江苏省南京市 电信");
        job.setIp("221.231.141.168");
        job.setLocationCode(320000);
        job.setWorkerId(321);
        job.setCountry_code(1);
        job.setProvince_code(340000);
        job.setCity_code(340100);
        
        ClientResource client = new ClientResource(baseUrl);
        client.post(job);
        
	}
}
