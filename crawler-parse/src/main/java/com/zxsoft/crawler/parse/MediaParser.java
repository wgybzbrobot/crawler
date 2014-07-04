package com.zxsoft.crawler.parse;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * 解析图片、音频、视频
 */
public class MediaParser {

    /**
     * image 标签
     */
    private static final String[] IMG_TAGS = {"img"}; 
    private static final String[] AUDIO_TAGS = {"img"}; 
    private static final String[] VIDEO_TAGS = {"img"}; 
    
    /**
     * 获取图片链接
     */
    public String getImg (Elements content) {
        StringBuilder sb = new StringBuilder();
        for (String tag : IMG_TAGS) {
            Elements imgs  = content.select(tag);
            StringBuilder imgUrlSb = new StringBuilder();
            for (Element img : imgs) {
                imgUrlSb.append(img.attr("abs:src")).append(" ");// 多个url用空格隔开
            }
        }
        return sb.toString();
    }
    
    /**
     * 获取音频链接
     */
    public String getAudio (Elements content) {
        StringBuilder sb = new StringBuilder();
        
        return sb.toString();
    }
    
    /**
     * 获取视频链接
     */
    public String getVideo (Elements content) {
        StringBuilder sb = new StringBuilder();
        
        return sb.toString();
    }
}
