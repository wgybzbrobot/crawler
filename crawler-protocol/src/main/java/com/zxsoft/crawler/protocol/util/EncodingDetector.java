package com.zxsoft.crawler.protocol.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.mozilla.intl.chardet.HtmlCharsetDetector;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.StringUtils;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

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
        public static void main(String[] args) throws Exception {
                String[] strs = { "http://bbs.ahwang.cn/forum-156-1.html" };
                HtmlCharsetDetector.main(strs);
//                new URL("").openStream();
        }

        public static String detect(String contentType) {
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
                if ((encoding.length() > 2) && (encoding.startsWith("\"")) && (encoding.endsWith("\"")))
                        encoding = encoding.substring(1, encoding.length() - 1);
                return (encoding.trim());
        }

        /**
         * 使用tika
         * @param text
         * @param metadata
         * @return
         */
        @Deprecated
        public static String detect(String text, Metadata metadata) {
                String charset = "";
//                org.apache.tika.detect.EncodingDetector encodingDetector = new org.apache.tika.parser.html.HtmlEncodingDetector();
//                InputStream input = IOUtils.toInputStream(text);
//                Charset _charset = null;
//                try {
//                        _charset = encodingDetector.detect(input, metadata);
//                        input.close();
//                } catch (IOException e) {
//                        e.printStackTrace();
//                }
//                if (_charset != null) {
//                        charset = _charset.displayName();
//                } else {
                        charset = DEFAULT_CHARSET;
//                }
                return charset;
        }

        /**
         * 使用icu, 若confidence小于70, 则任务探测不准确,返回默认编码.
         * @param content
         * @return
         */
        public static String detect(byte[] content) {
                
                CharsetDetector detector = new CharsetDetector();
                detector.setText(content);
                CharsetMatch charsetMatch = detector.detect();
                int confidence = charsetMatch.getConfidence();
                LOG.debug("confidence: " + charsetMatch.getConfidence());
                if (confidence < 70) {
                        LOG.info("Detect encoding confidence(" + confidence + ", encoding: " + charsetMatch.getName() + ") is less than 70, use default_charset:" + DEFAULT_CHARSET);
                        return DEFAULT_CHARSET;
                }
                LOG.debug("name: " + charsetMatch.getName());
                return charsetMatch.getName();
        }
        
        /**
         * 使用Mozilla的探测方法
         * @param is
         * @throws IOException
         */
        public void detect(InputStream is) throws IOException {
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

                BufferedInputStream imp = new BufferedInputStream(is);

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
                if (!found) {
                        String prob[] = det.getProbableCharsets();
                        charset = prob[0];
                }
        }
}
