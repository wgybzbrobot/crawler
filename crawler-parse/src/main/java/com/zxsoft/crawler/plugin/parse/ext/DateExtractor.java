package com.zxsoft.crawler.plugin.parse.ext;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.StringUtils;
import com.zxisl.nldp.Nldp;

public class DateExtractor {

    private static Logger LOG = LoggerFactory.getLogger(DateExtractor.class);

    public static Date extract(String text) {
        if (StringUtils.isEmpty(text))
            return null;

        if (text.trim().startsWith("http://")) {
            text = text.replaceAll("http://", "");
        }
        
        text = text.replaceAll("\u00A0", " ");
        text = text.replaceAll("&nbsp;", " ");
        Date date = null;
        try {
            Nldp nldp = new Nldp(text);
            date = nldp.extractDate();
        } catch (Exception e) {
            LOG.error("解析时间失败:" + text, e);
        }

        return date;
    }

    public static long extractInMilliSecs(String text) {
        Date date = extract(text);
        if (date != null) {
            return date.getTime();
        }
        return 0L;
    }
}
