// Generated from SourceExtractor.g4 by ANTLR 4.3

	package com.zxsoft.crawler.parse.ext.generated;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SourceExtractorLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		FILTER=1, WITHESPACE=2, WS=3, Source_Token1=4;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'"
	};
	public static final String[] ruleNames = {
		"FILTER", "WITHESPACE", "WS", "Source_Token1"
	};


	public SourceExtractorLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SourceExtractor.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\6\66\b\1\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\3\2\3\2\3\2\3\2\3\3\3\3\3\4\6\4\23\n\4\r\4\16"+
		"\4\24\3\4\3\4\3\5\3\5\3\5\3\5\5\5\35\n\5\3\5\7\5 \n\5\f\5\16\5#\13\5\3"+
		"\5\3\5\7\5\'\n\5\f\5\16\5*\13\5\3\5\6\5-\n\5\r\5\16\5.\3\5\7\5\62\n\5"+
		"\f\5\16\5\65\13\5\2\2\6\3\3\5\4\7\5\t\6\3\2\4\6\2\13\f\17\17\"\"\u00a2"+
		"\u00a2\4\2<<\uff1c\uff1c;\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\3\13\3\2\2\2\5\17\3\2\2\2\7\22\3\2\2\2\t\34\3\2\2\2\13\f\13\2\2\2"+
		"\f\r\3\2\2\2\r\16\b\2\2\2\16\4\3\2\2\2\17\20\t\2\2\2\20\6\3\2\2\2\21\23"+
		"\5\5\3\2\22\21\3\2\2\2\23\24\3\2\2\2\24\22\3\2\2\2\24\25\3\2\2\2\25\26"+
		"\3\2\2\2\26\27\b\4\2\2\27\b\3\2\2\2\30\31\7\u6767\2\2\31\35\7\u6e92\2"+
		"\2\32\33\7\u4f88\2\2\33\35\7\u81ec\2\2\34\30\3\2\2\2\34\32\3\2\2\2\35"+
		"!\3\2\2\2\36 \5\5\3\2\37\36\3\2\2\2 #\3\2\2\2!\37\3\2\2\2!\"\3\2\2\2\""+
		"$\3\2\2\2#!\3\2\2\2$(\t\3\2\2%\'\5\5\3\2&%\3\2\2\2\'*\3\2\2\2(&\3\2\2"+
		"\2()\3\2\2\2),\3\2\2\2*(\3\2\2\2+-\n\2\2\2,+\3\2\2\2-.\3\2\2\2.,\3\2\2"+
		"\2./\3\2\2\2/\63\3\2\2\2\60\62\5\5\3\2\61\60\3\2\2\2\62\65\3\2\2\2\63"+
		"\61\3\2\2\2\63\64\3\2\2\2\64\n\3\2\2\2\65\63\3\2\2\2\t\2\24\34!(.\63\3"+
		"\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}