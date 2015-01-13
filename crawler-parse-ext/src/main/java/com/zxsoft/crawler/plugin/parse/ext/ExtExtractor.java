package com.zxsoft.crawler.plugin.parse.ext;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.plugin.parse.ext.source.SourceLexer;
import com.zxsoft.crawler.plugin.parse.ext.source.SourceParser;
import com.zxsoft.crawler.plugin.parse.ext.source.SourceParser.ExtractSourceContext;

/**
 * 抽取资讯来源
 * @author xiayun
 *
 */
public class SourceExtractor {

        private static Logger LOG = LoggerFactory.getLogger(SourceExtractor.class);
        
        public static String extract(String text) {
                ANTLRInputStream ais = new ANTLRInputStream(text);
                SourceLexer rlexer = new SourceLexer(ais);
                CommonTokenStream rTokenStream = new CommonTokenStream(rlexer);
                SourceParser rparser = new SourceParser(rTokenStream);
                ExtractSourceContext  rContext = rparser.extractSource();
                String str = rContext.getText();
                if (str.contains("missing") || str.contains("EOF")) {
                        // do not find use antlr4
                        str = text;
                } else {
                        int pos = str.indexOf(":") == -1 ? str.indexOf("：") : str.indexOf(":");
                        str = str.substring(pos + 1);
                }
                return str;
        }
        
        public static void main(String[] args) {
                System.out.println(SourceExtractor.extract("2014-12-22 8:57:16   来源：安徽财经网  评论("));
                System.out.println(SourceExtractor.extract("来源：苹果日报"));
        }
        
}
