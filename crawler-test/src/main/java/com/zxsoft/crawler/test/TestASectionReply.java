package com.zxsoft.crawler.test;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.parse.NetworkInspectParserController;
import com.zxsoft.crawler.storage.WebPage;

public class TestASectionReply {

    private static Logger LOG = LoggerFactory.getLogger(TestASectionReply.class);
    private volatile static boolean stop = false;

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        TestASectionReply.stop = stop;
    }

    public static void main(String[] args) throws MalformedURLException,
                    UnknownHostException {
            NetworkInspectParserController controller = new NetworkInspectParserController();

            int region = 1, provinceId = 340000, cityId = 340100;
            int source_id = 4;
            String source_name = "合肥论坛回复排序";
            int server_id = 2571;
            int source_type = JobType.NETWORK_INSPECT.getValue();
            int locationCode = 320000;
            String url = "http://bbs.hefei.cc/forum-196-1.html";
            String ip = "221.231.141.168";

            // 初始上次抓取时间是一天前
            long prevFetchTime = System.currentTimeMillis() - 24 * 60 * 60
                            * 1000L;
            String comment = "合肥专区回复排序", location = "江苏省南京市 电信";
            int sectionId = 807;
            WebPage page = new WebPage(url, sectionId, comment, prevFetchTime,
                            region, provinceId, cityId, locationCode, location,
                            ip, JobType.NETWORK_INSPECT, source_id,
                            source_name, server_id, source_type);

            LOG.info("合肥专区回复排序 starting");
            while (!TestASectionReply.stop) {
                try {
                    controller.parse(page);
                    TimeUnit.MINUTES.sleep(20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LOG.info("合肥专区回复排序 stopped.");
        }
}
