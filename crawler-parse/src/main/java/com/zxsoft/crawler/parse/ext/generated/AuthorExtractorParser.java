// Generated from AuthorExtractor.g4 by ANTLR 4.3

	package com.zxsoft.crawler.parse.ext.generated;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AuthorExtractorParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		FILTER=1, WITHESPACE=2, WS=3, Author_Token1=4;
	public static final String[] tokenNames = {
		"<INVALID>", "FILTER", "WITHESPACE", "WS", "Author_Token1"
	};
	public static final int
		RULE_extractAuthor = 0;
	public static final String[] ruleNames = {
		"extractAuthor"
	};

	@Override
	public String getGrammarFileName() { return "AuthorExtractor.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public AuthorExtractorParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ExtractAuthorContext extends ParserRuleContext {
		public TerminalNode Author_Token1() { return getToken(AuthorExtractorParser.Author_Token1, 0); }
		public ExtractAuthorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extractAuthor; }
	}

	public final ExtractAuthorContext extractAuthor() throws RecognitionException {
		ExtractAuthorContext _localctx = new ExtractAuthorContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_extractAuthor);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2); match(Author_Token1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\6\7\4\2\t\2\3\2\3"+
		"\2\3\2\2\2\3\2\2\2\5\2\4\3\2\2\2\4\5\7\6\2\2\5\3\3\2\2\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}