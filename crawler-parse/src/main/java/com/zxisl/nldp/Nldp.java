package com.zxisl.nldp;

import java.util.Date;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import com.zxisl.nldp.generated.DateExtractorLexer;
import com.zxisl.nldp.generated.DateExtractorParser;

public class Nldp {

        private String text;
        
        public Nldp(String text) {
                this.text = text;
        }
        
        private void preHandle() {
                String reg = "href=\"(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*(\\?\\S*)?";
                text = text.replaceAll(reg, "").replaceAll("[\\s+ | &nbsp;]", "");
        }
        
        public Date extractDate() {
                
                preHandle();
                ANTLRInputStream ais = new ANTLRInputStream(text);
                DateExtractorLexer lexer = new DateExtractorLexer(ais);
                CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                DateExtractorParser parser = new DateExtractorParser(tokenStream);
                parser.search();
                WalkerState state = parser.getWalkerState();
                return state.getDate();
        }

        public long extractDateInMillis() {
                preHandle();
                ANTLRInputStream ais = new ANTLRInputStream(text);
                DateExtractorLexer lexer = new DateExtractorLexer(ais);
                CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                DateExtractorParser parser = new DateExtractorParser(tokenStream);
                parser.search();
                WalkerState state = parser.getWalkerState();
                return state.getTimeInMillis();
        }
        
        public static void main(String[] args) {
                String date = "2014年12月24日   2015-1-2";
                String[] strs = date.split("[年 | 月 | 日 | \\- ]");
                int i = strs.length;
                for (String string : strs) {
                        System.out.println(string);
                }
        }
}
