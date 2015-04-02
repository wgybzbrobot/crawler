package com.zxsoft.crawler.test;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.common.DetailRule;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.common.ListRule;
import com.zxsoft.crawler.parse.NetworkInspectParserController;
import com.zxsoft.crawler.storage.WebPage;

public class TestASectionRelease {

    private static Logger LOG = LoggerFactory.getLogger(TestASectionRelease.class);
    private volatile static boolean stop = false;

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        TestASectionRelease.stop = stop;
    }

    public static void main(String[] args) throws MalformedURLException,
                    UnknownHostException {
            NetworkInspectParserController controller = new NetworkInspectParserController();

            int source_id = 4;
            String source_name = "合肥论坛发帖排序";
            String url = "http://bbs.hefei.cc/forum.php?mod=forumdisplay&fid=196&filter=author&orderby=dateline";

            String type = "合肥专区发帖排序";
            int sectionId = 807;
 
            ListRule listRule = new ListRule( false, "forum", "div#threadlist ", "tbody[id^=normalthread]", "a.xst", "td.by em span", "td.by em a", "", "");
            DetailRule detailRule = new DetailRule("bbs.hefei.cc", "div#ct  p.hm  span:eq(1)  em:eq(1)", 
                            "div#ct  p.hm  span:eq(1)  em:eq(0)", "", "", true, false, "div#postlist div[id^=post_]", 
                            "div.pi div.authi  a.xw1", "", "div.pcb table td", "div#postlist > div[id^=post_]:gt(0)", 
                            "div.pi div.authi  a.xw1", "div.authi  em[id^=authorpost]", "div.pcb > div.t_fsz  table  td", "", "", "", "");
            Set<DetailRule> detailRules = new HashSet<DetailRule>();
            detailRules.add(detailRule);
            
            JobConf jobConf = new JobConf(JobType.NETWORK_INSPECT, url, source_name, source_id, sectionId, type, listRule, detailRules);
            
            jobConf.setPrevFetchTime( System.currentTimeMillis() - 24 * 60 * 60 * 1000L);
            jobConf.setLocation( "江苏省南京市 电信");
            jobConf.setIp("221.231.141.168");
            jobConf.setLocationCode(320000);
            jobConf.setWorkerId(321);
            jobConf.setCountry_code(1);
            jobConf.setProvince_code(340000);
            jobConf.setCity_code(340100);
            
            LOG.info("合肥专区发帖排序 starting");
            while (!TestASectionRelease.stop) {
                try {
                    controller.parse(jobConf);
                    TimeUnit.MINUTES.sleep(20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LOG.info("合肥专区发帖排序 stopped.");
        }
}
