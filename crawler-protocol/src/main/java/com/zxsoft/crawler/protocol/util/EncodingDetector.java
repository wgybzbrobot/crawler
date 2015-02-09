package com.zxsoft.crawler.protocol.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.mozilla.intl.chardet.HtmlCharsetDetector;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;
//import org.mozilla.intl.chardet.HtmlCharsetDetector;
//import org.mozilla.intl.chardet.nsDetector;
//import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
//import org.mozilla.intl.chardet.nsPSMDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.zxisl.commons.cache.ObjectCache;
import com.zxisl.commons.utils.StringUtils;

/**
 * 获取网页编码
 * 
 * @author xiayun
 *
 */
public class EncodingDetector {

        private static Logger LOG = LoggerFactory.getLogger(EncodingDetector.class);

        private static final String DEFAULT_CHARSET = "gb2312";

        public boolean found = false;

        private String charset;

        public String getCharset() {
                return charset;
        }

        public void detect(String contentType, byte[] content, URL url/*, int i*/) {
                // String charset = "";
                try {
                        /*
                         * 在contentType中探测
                         */
                        if (!StringUtils.isEmpty(contentType)) {
//                                LOG.debug("在contentType中探测");
                                int start = contentType.indexOf("charset=");
                                if (start >= 0) {
                                        charset = contentType.substring(start + 8);
                                        int end = charset.indexOf(';');
                                        if (end >= 0)
                                                charset = charset.substring(0, end).trim();
                                        if ((charset.length() > 2) && (charset.startsWith("\"")) && (charset.endsWith("\"")))
                                                charset = charset.substring(1, charset.length() - 1).trim();
                                }
                        }

                        /*
                         * 使用universalchardet探测 ,
                         * 若出现bug则将universalchardet和icu换位置试试
                         */
                        if (StringUtils.isEmpty(charset)) {
//                                LOG.debug("使用universalchardet探测 ");
                                org.mozilla.universalchardet.UniversalDetector detector = new org.mozilla.universalchardet.UniversalDetector(
                                                                null);
                                detector.handleData(content, 0, content.length);
                                detector.dataEnd();
                                charset = detector.getDetectedCharset();
                                detector.reset();
                        }

                        /*
                         * 使用icu探测
                         */
                        if (StringUtils.isEmpty(charset)) {
//                                LOG.debug("使用icu探测");
                                CharsetDetector detector = new CharsetDetector();
                                detector.setText(content);
                                CharsetMatch charsetMatch = detector.detect();
                                int confidence = charsetMatch.getConfidence();
//                                LOG.debug("confidence: " + charsetMatch.getConfidence());
                                if (confidence > 60) {
                                        // LOG.info("Detect encoding confidence("
                                        // + confidence + ", encoding: " +
                                        // charsetMatch.getName()
                                        // +
                                        // ") is less than 70, use default_charset:"
                                        // + DEFAULT_CHARSET);
                                        LOG.debug("name: " + charsetMatch.getName());
                                        charset = charsetMatch.getName();
                                }
                        }

                        ObjectCache objectCache = ObjectCache.get("EncodingCache");
                        try {
                                String _encoding = (String) objectCache.getObject(url.getHost());
                                if (!StringUtils.isEmpty(_encoding)) 
                                        charset = _encoding;
                                else 
                                        return ;
                        } catch (Exception e) {
                                
                        }
                        
                        if (StringUtils.isEmpty(charset)) {
                                LOG.debug("use mozilla jchardet detect");
                                detect(url);
                        }

                        if (StringUtils.isEmpty(charset) || "isAscii".equalsIgnoreCase(charset)) {
                                charset = DEFAULT_CHARSET;
//                                if (i == 1) {
//                                        charset = DEFAULT_CHARSET;
//                                } else {
//                                        charset = "UTF-8";
//                                }
                        } else {
                                objectCache.setObject(url.getHost(), charset);
                        }
                        
                        // return DEFAULT_CHARSET;
                } catch (Exception e) {
                        LOG.error(e.getMessage());
                        // return DEFAULT_CHARSET;
                }
        }

        /**
         * 使用tika
         * 
         * @param text
         * @param metadata
         * @return
         */
        public void detect(String text, Metadata metadata) {
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
                } 
        }

        /**
         * 使用icu, 若confidence小于70, 则任务探测不准确,返回默认编码.
         * 
         * @param content
         * @return
         */
        public String detect(byte[] content) {
                CharsetDetector detector = new CharsetDetector();
                detector.setText(content);
                CharsetMatch charsetMatch = detector.detect();
                int confidence = charsetMatch.getConfidence();
                LOG.debug("confidence: " + charsetMatch.getConfidence());
                if (confidence < 70) {
                        LOG.info("Detect encoding confidence(" + confidence + ", encoding: " + charsetMatch.getName()
                                                        + ") is less than 70, use default_charset:" + DEFAULT_CHARSET);
                        return DEFAULT_CHARSET;
                }
                LOG.debug("name: " + charsetMatch.getName());
                return charsetMatch.getName();
        }

        /**
         * 使用Mozilla的探测方法
         * 
         * @param is
         * @throws IOException
         */
        public void detect(URL url) throws IOException {
                // Initalize the nsDetector() ;
                int lang = nsPSMDetector.ALL;
                nsDetector det = new nsDetector(lang);
                // Set an observer...
                // The Notify() will be called when a matching charset is found.
                det.Init(new nsICharsetDetectionObserver() {
                        public void Notify(String _charset) {
                                HtmlCharsetDetector.found = true;
                                charset = _charset;
                        }
                });

                BufferedInputStream imp = new BufferedInputStream(url.openStream());

                byte[] buf = new byte[1024];
                int len;
                boolean done = false;
                boolean isAscii = true;

                while ((len = imp.read(buf, 0, buf.length)) != -1) {
                        // Check if the stream is only ascii.
                        if (isAscii)
                                isAscii = det.isAscii(buf, len);
                        // DoIt if non-ascii and not done yet.
                        if (!isAscii && !done)
                                done = det.DoIt(buf, len, false);
                }
                det.DataEnd();
                if (isAscii) {
                        found = true;
                        charset = "ASCII";
                }
        }

        public String detectByUniversalchardet(byte[] bytes) {
                org.mozilla.universalchardet.UniversalDetector detector = new org.mozilla.universalchardet.UniversalDetector(null);
                detector.handleData(bytes, 0, bytes.length);
                detector.dataEnd();
                String encoding = detector.getDetectedCharset();
                detector.reset();
                return encoding;
        }
}
