package com.zxsoft.crawler.util.page;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.thinkingcloud.framework.util.StringUtils;

/**
 * 获取网页编码
 * 
 * @author xiayun
 *
 */
public class EncodingDetector {

        private static final String DEFAULT_CHARSET = "gb2312";

        public static String parseCharacterEncoding(String contentType, byte[] content) {
                if (StringUtils.isEmpty(contentType)) {
                        return "";
                }
                int start = contentType.indexOf("charset=");
                if (start < 0)
                        return "";
                
                String encoding = contentType.substring(start + 8);
                int end = encoding.indexOf(';');
                if (end >= 0)
                        encoding = encoding.substring(0, end);
                encoding = encoding.trim();
                if ((encoding.length() > 2) && (encoding.startsWith("\""))
                        && (encoding.endsWith("\"")))
                        encoding = encoding.substring(1, encoding.length() - 1);
                return (encoding.trim());

        }
        
        public static String detect(String text, Metadata metadata) {
                String charset = "";
                org.apache.tika.detect.EncodingDetector encodingDetector = new org.apache.tika.parser.html.HtmlEncodingDetector();
                InputStream input = IOUtils.toInputStream(text);
                Charset _charset = null;
                try {
                        _charset = encodingDetector.detect(input, metadata);
                        input.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                if (_charset != null) {
                        charset = _charset.displayName();
                } else {
                        charset = DEFAULT_CHARSET;
                }
                return charset;
        }
}
