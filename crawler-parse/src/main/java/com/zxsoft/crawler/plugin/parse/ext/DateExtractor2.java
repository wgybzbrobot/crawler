package com.zxsoft.crawler.plugin.parse.ext;

import java.util.Date;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxisl.nldp.Nldp;

public class DateExtractor2 {

    private long timeInMs;
    private float weight;

    public void extract(Document doc) {
        String[] tags = new String[]{"script", "style", "a", "img", "form",
                        "textarea","dd","footer"};
        for (String tag : tags) {
            if (!CollectionUtils.isEmpty(doc.getElementsByTag(tag)))
                doc.getElementsByTag(tag).remove();
        }
        if (null != doc.getElementById("top"))
            doc.getElementById("top").remove();
        if (!CollectionUtils.isEmpty(doc.getElementsByClass("menu")))
            doc.getElementsByClass("menu").remove();
        if (!CollectionUtils.isEmpty(doc.getElementsMatchingOwnText("客户|联系|Copyright|电话|热线")))
            doc.getElementsMatchingOwnText("客户|联系|Copyright|电话|热线").remove();
        
        if (doc == null || doc.body() == null)
            return;
//        System.out.println(doc.body());
        Elements eles = doc.body().getAllElements();
        int size = eles.size();
        for (int i = 0; i < size; i++) {
            Element ele = eles.get(i);
            String text = ele.text();
            if (StringUtils.isEmpty(text) || text.length()<3)
                continue;
            text = text.replaceAll("\u00A0", " ");
            text = text.replaceAll("&nbsp;", " ");
            float _weight = 0f;
            
            if (i < size / 3)
                _weight = 0.5f;
            else if (i >= size / 3 && i < 2 * size /3)
                _weight = 0.1f;
            
            if (!CollectionUtils.isEmpty(ele.getElementsMatchingOwnText("日期|发布时间|来源|发表于"))) {
               _weight += 0.9;
            }
            long time = 0L;
            try {
                Nldp nldp = new Nldp(text);
                time = nldp.extractDateInMillis();
                // 去除和当前时间一样的时间
                if (time <= 315504000000L || time / 60000L == System.currentTimeMillis() / 60000l)
                    continue;
                if (nldp.getWeight() + _weight > weight) {
                    weight = nldp.getWeight() + _weight;
                    timeInMs = time;
                }
            } catch (Exception e) {
            }
        }
    }

    public long getTimeInMs() {
        return timeInMs;
    }

    public float getWeight() {
        return weight;
    }

}
