package com.zxsoft.crawler.plugin.parse.ext;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.plugin.parse.ext.generated.AuthorExtractorLexer;
import com.zxsoft.crawler.plugin.parse.ext.generated.AuthorExtractorParser;
import com.zxsoft.crawler.plugin.parse.ext.generated.AuthorExtractorParser.ExtractAuthorContext;
import com.zxsoft.crawler.plugin.parse.ext.generated.ReadNumExtractorLexer;
import com.zxsoft.crawler.plugin.parse.ext.generated.ReadNumExtractorParser;
import com.zxsoft.crawler.plugin.parse.ext.generated.ReadNumExtractorParser.ExtractReadNumContext;
import com.zxsoft.crawler.plugin.parse.ext.generated.ReplyNumExtractorLexer;
import com.zxsoft.crawler.plugin.parse.ext.generated.ReplyNumExtractorParser;
import com.zxsoft.crawler.plugin.parse.ext.generated.ReplyNumExtractorParser.ExtractReplyNumContext;
import com.zxsoft.crawler.plugin.parse.ext.generated.SourceExtractorLexer;
import com.zxsoft.crawler.plugin.parse.ext.generated.SourceExtractorParser;
import com.zxsoft.crawler.plugin.parse.ext.generated.SourceExtractorParser.ExtractSourceContext;
import com.zxsoft.crawler.util.Utils;

/**
 * 抽取资讯来源
 * 
 * @author xiayun
 *
 */
public class ExtExtractor {

        private static Logger LOG = LoggerFactory.getLogger(ExtExtractor.class);

        /**
         * 抽取来源
         * @param text
         * @return
         */
        public static String extractSource(String text) {
                ANTLRInputStream ais = new ANTLRInputStream(text);
                SourceExtractorLexer rlexer = new SourceExtractorLexer(ais);
                CommonTokenStream rTokenStream = new CommonTokenStream(rlexer);
                SourceExtractorParser rparser = new SourceExtractorParser(rTokenStream);
                ExtractSourceContext rContext = rparser.extractSource();
                String str = rContext.getText();
                if (str.contains("missing") || str.contains("EOF")) {
                        // do not find use antlr4
                        str = text;
                } else {
                        int pos = str.indexOf(":") == -1 ? str.indexOf("：") : str.indexOf(":");
                        str = str.substring(pos + 1);
                }
                if (str == null || str.trim().length() > 30) str = "";
                return str;
        }

        /**
         * 抽取浏览数
         * @param text
         * @return
         */
        public static int extractReadNum(String text) {
                int num = 0;
                try {
                        ANTLRInputStream ais = new ANTLRInputStream(text);
                        ReadNumExtractorLexer rlexer = new ReadNumExtractorLexer(ais);
                        CommonTokenStream rTokenStream = new CommonTokenStream(rlexer);
                        ReadNumExtractorParser rparser = new ReadNumExtractorParser(rTokenStream);
                        ExtractReadNumContext context = rparser.extractReadNum();
                        String str = context.getText();
                        if (str.contains("missing") || str.contains("EOF")) {
                                // do not find use antlr4
                                str = text;
                        } else {
                                int pos = str.indexOf(":") == -1 ? str.indexOf("：") : str.indexOf(":");
                                str = str.substring(pos + 1);
                        }
                        num = Integer.valueOf(str.trim());
                } catch (Exception e) {
                        try {
                                num = Utils.extractNum(text);
                                return num;
                        } catch (NumberFormatException e1) {
                                LOG.debug(e1.getMessage());
                        }
                }
                return num;
        }

        /**
         * 抽取回复数
         * @param text
         * @return
         */
        public static int extractReplyNum(String text) {
                int num = 0;
                try {
                        ANTLRInputStream ais = new ANTLRInputStream(text);
                        ReplyNumExtractorLexer rlexer = new ReplyNumExtractorLexer(ais);
                        CommonTokenStream rTokenStream = new CommonTokenStream(rlexer);
                        ReplyNumExtractorParser rparser = new ReplyNumExtractorParser(rTokenStream);
                        ExtractReplyNumContext context = rparser.extractReplyNum();
                        String str = context.getText();
                        if (str.contains("missing") || str.contains("EOF")) {
                                // do not find use antlr4
                                str = text;
                        } else {
                                int pos = str.indexOf(":") == -1 ? str.indexOf("：") : str.indexOf(":");
                                str = str.substring(pos + 1);
                        }
                        num = Integer.valueOf(str.trim());
                } catch (Exception e) {
                        try {
                                // 直接抽取
                                num = Utils.extractNum(text);
                                return num;
                        } catch (NumberFormatException e1) {
                                LOG.debug(e1.getMessage());
                        }
                }
                return num;
        }
        
        public static String extractAuthor(String text) {
                ANTLRInputStream ais = new ANTLRInputStream(text);
                AuthorExtractorLexer rlexer = new AuthorExtractorLexer(ais);
                CommonTokenStream rTokenStream = new CommonTokenStream(rlexer);
                AuthorExtractorParser rparser = new AuthorExtractorParser(rTokenStream);
                ExtractAuthorContext rContext = rparser.extractAuthor();
                String str = rContext.getText();
                if (str.contains("missing") || str.contains("EOF")) {
                        // do not find use antlr4
                        str = text;
                } else {
                        int pos = str.indexOf(":") == -1 ? str.indexOf("：") : str.indexOf(":");
                        str = str.substring(pos + 1);
                }
                if (str == null || str.trim().length() > 30) str = "";
                return str.trim();
        }

}
