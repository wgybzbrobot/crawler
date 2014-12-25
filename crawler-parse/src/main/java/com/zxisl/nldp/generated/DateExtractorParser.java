// Generated from DateExtractor.g4 by ANTLR 4.3
package com.zxisl.nldp.generated;

import com.zxisl.nldp.WalkerState;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DateExtractorParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__1=1, T__0=2, FILTER=3, WHITESPACE=4, WS=5, M_D=6, Y_M_D=7, Hour_Min_Sec=8, 
		HourAgo=9, MinuteAgo=10, SecondsAgo=11;
	public static final String[] tokenNames = {
		"<INVALID>", "'昨天'", "'前天'", "FILTER", "WHITESPACE", "WS", "M_D", "Y_M_D", 
		"Hour_Min_Sec", "HourAgo", "MinuteAgo", "SecondsAgo"
	};
	public static final int
		RULE_y_m_d = 0, RULE_m_d = 1, RULE_matchTime = 2, RULE_yesterday = 3, 
		RULE_beforeyesterday = 4, RULE_hourAgo = 5, RULE_minuteAgo = 6, RULE_secodnsAgo = 7, 
		RULE_search = 8;
	public static final String[] ruleNames = {
		"y_m_d", "m_d", "matchTime", "yesterday", "beforeyesterday", "hourAgo", 
		"minuteAgo", "secodnsAgo", "search"
	};

	@Override
	public String getGrammarFileName() { return "DateExtractor.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


		private WalkerState walkerState = new WalkerState();
		public WalkerState getWalkerState() {
			return walkerState;
		}

	public DateExtractorParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class Y_m_dContext extends ParserRuleContext {
		public Token ymd;
		public TerminalNode Y_M_D() { return getToken(DateExtractorParser.Y_M_D, 0); }
		public Y_m_dContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_y_m_d; }
	}

	public final Y_m_dContext y_m_d() throws RecognitionException {
		Y_m_dContext _localctx = new Y_m_dContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_y_m_d);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18); ((Y_m_dContext)_localctx).ymd = match(Y_M_D);
				String text = (((Y_m_dContext)_localctx).ymd!=null?((Y_m_dContext)_localctx).ymd.getText():null); 
					String[] strs = text.split("[年 | 月 | 日 | \\- ]");
					if (strs.length == 3) {
						walkerState.setYMD(strs[0], strs[1], strs[2]);
					} else {
						walkerState.setMD(strs[0], strs[1]);
					}
				
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

	public static class M_dContext extends ParserRuleContext {
		public Token md;
		public TerminalNode M_D() { return getToken(DateExtractorParser.M_D, 0); }
		public M_dContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_m_d; }
	}

	public final M_dContext m_d() throws RecognitionException {
		M_dContext _localctx = new M_dContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_m_d);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(21); ((M_dContext)_localctx).md = match(M_D);
				String text = (((M_dContext)_localctx).md!=null?((M_dContext)_localctx).md.getText():null);
					System.out.println("Text: " + text);
					String[] strs = text.split("[月 | 日 | \\- ]");
					walkerState.setMD(strs[0], strs[1]);
				
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

	public static class MatchTimeContext extends ParserRuleContext {
		public Token hms;
		public TerminalNode Hour_Min_Sec() { return getToken(DateExtractorParser.Hour_Min_Sec, 0); }
		public MatchTimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matchTime; }
	}

	public final MatchTimeContext matchTime() throws RecognitionException {
		MatchTimeContext _localctx = new MatchTimeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_matchTime);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24); ((MatchTimeContext)_localctx).hms = match(Hour_Min_Sec);
				String text = (((MatchTimeContext)_localctx).hms!=null?((MatchTimeContext)_localctx).hms.getText():null);
					String[] strs = text.split("[时 | 分 | 秒 | : | 点 ]");
					if (strs.length == 3) {
						walkerState.setHMS(strs[0], strs[1], strs[2]);
					} else {
						walkerState.setHM(strs[0], strs[1]);
					}
				
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

	public static class YesterdayContext extends ParserRuleContext {
		public YesterdayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_yesterday; }
	}

	public final YesterdayContext yesterday() throws RecognitionException {
		YesterdayContext _localctx = new YesterdayContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_yesterday);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(27); match(T__1);
			walkerState.setToYesterday();
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

	public static class BeforeyesterdayContext extends ParserRuleContext {
		public BeforeyesterdayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_beforeyesterday; }
	}

	public final BeforeyesterdayContext beforeyesterday() throws RecognitionException {
		BeforeyesterdayContext _localctx = new BeforeyesterdayContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_beforeyesterday);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30); match(T__0);
			walkerState.setToBefore();
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

	public static class HourAgoContext extends ParserRuleContext {
		public Token h;
		public TerminalNode HourAgo() { return getToken(DateExtractorParser.HourAgo, 0); }
		public HourAgoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hourAgo; }
	}

	public final HourAgoContext hourAgo() throws RecognitionException {
		HourAgoContext _localctx = new HourAgoContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_hourAgo);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(33); ((HourAgoContext)_localctx).h = match(HourAgo);
				String txt = (((HourAgoContext)_localctx).h!=null?((HourAgoContext)_localctx).h.getText():null);
					txt = txt.replace("小时前","");
					walkerState.setHourAgo(txt);
				
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

	public static class MinuteAgoContext extends ParserRuleContext {
		public Token m;
		public TerminalNode MinuteAgo() { return getToken(DateExtractorParser.MinuteAgo, 0); }
		public MinuteAgoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_minuteAgo; }
	}

	public final MinuteAgoContext minuteAgo() throws RecognitionException {
		MinuteAgoContext _localctx = new MinuteAgoContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_minuteAgo);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36); ((MinuteAgoContext)_localctx).m = match(MinuteAgo);
				String txt = (((MinuteAgoContext)_localctx).m!=null?((MinuteAgoContext)_localctx).m.getText():null);
					txt = txt.replace("分钟前","");
					walkerState.setMinuteAgo(txt);
				
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

	public static class SecodnsAgoContext extends ParserRuleContext {
		public Token s;
		public TerminalNode SecondsAgo() { return getToken(DateExtractorParser.SecondsAgo, 0); }
		public SecodnsAgoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_secodnsAgo; }
	}

	public final SecodnsAgoContext secodnsAgo() throws RecognitionException {
		SecodnsAgoContext _localctx = new SecodnsAgoContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_secodnsAgo);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(39); ((SecodnsAgoContext)_localctx).s = match(SecondsAgo);
				String txt = (((SecodnsAgoContext)_localctx).s!=null?((SecodnsAgoContext)_localctx).s.getText():null);
					txt = txt.replace("秒前","");
					walkerState.setSecondsAgo(txt);
				
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

	public static class SearchContext extends ParserRuleContext {
		public MatchTimeContext matchTime() {
			return getRuleContext(MatchTimeContext.class,0);
		}
		public HourAgoContext hourAgo() {
			return getRuleContext(HourAgoContext.class,0);
		}
		public Y_m_dContext y_m_d() {
			return getRuleContext(Y_m_dContext.class,0);
		}
		public SecodnsAgoContext secodnsAgo() {
			return getRuleContext(SecodnsAgoContext.class,0);
		}
		public BeforeyesterdayContext beforeyesterday() {
			return getRuleContext(BeforeyesterdayContext.class,0);
		}
		public YesterdayContext yesterday() {
			return getRuleContext(YesterdayContext.class,0);
		}
		public MinuteAgoContext minuteAgo() {
			return getRuleContext(MinuteAgoContext.class,0);
		}
		public M_dContext m_d() {
			return getRuleContext(M_dContext.class,0);
		}
		public SearchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_search; }
	}

	public final SearchContext search() throws RecognitionException {
		SearchContext _localctx = new SearchContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_search);
		int _la;
		try {
			setState(61);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(42); y_m_d();
				setState(43); matchTime();
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(45); m_d();
				setState(46); matchTime();
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(48); yesterday();
				setState(50);
				_la = _input.LA(1);
				if (_la==Hour_Min_Sec) {
					{
					setState(49); matchTime();
					}
				}

				}
				break;

			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(52); beforeyesterday();
				setState(54);
				_la = _input.LA(1);
				if (_la==Hour_Min_Sec) {
					{
					setState(53); matchTime();
					}
				}

				}
				break;

			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(56); matchTime();
				}
				break;

			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(57); y_m_d();
				}
				break;

			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(58); hourAgo();
				}
				break;

			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(59); minuteAgo();
				}
				break;

			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(60); secodnsAgo();
				}
				break;
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\rB\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2\3\2"+
		"\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3"+
		"\b\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\65\n\n\3\n\3\n\5\n"+
		"9\n\n\3\n\3\n\3\n\3\n\3\n\5\n@\n\n\3\n\2\2\13\2\4\6\b\n\f\16\20\22\2\2"+
		"B\2\24\3\2\2\2\4\27\3\2\2\2\6\32\3\2\2\2\b\35\3\2\2\2\n \3\2\2\2\f#\3"+
		"\2\2\2\16&\3\2\2\2\20)\3\2\2\2\22?\3\2\2\2\24\25\7\t\2\2\25\26\b\2\1\2"+
		"\26\3\3\2\2\2\27\30\7\b\2\2\30\31\b\3\1\2\31\5\3\2\2\2\32\33\7\n\2\2\33"+
		"\34\b\4\1\2\34\7\3\2\2\2\35\36\7\3\2\2\36\37\b\5\1\2\37\t\3\2\2\2 !\7"+
		"\4\2\2!\"\b\6\1\2\"\13\3\2\2\2#$\7\13\2\2$%\b\7\1\2%\r\3\2\2\2&\'\7\f"+
		"\2\2\'(\b\b\1\2(\17\3\2\2\2)*\7\r\2\2*+\b\t\1\2+\21\3\2\2\2,-\5\2\2\2"+
		"-.\5\6\4\2.@\3\2\2\2/\60\5\4\3\2\60\61\5\6\4\2\61@\3\2\2\2\62\64\5\b\5"+
		"\2\63\65\5\6\4\2\64\63\3\2\2\2\64\65\3\2\2\2\65@\3\2\2\2\668\5\n\6\2\67"+
		"9\5\6\4\28\67\3\2\2\289\3\2\2\29@\3\2\2\2:@\5\6\4\2;@\5\2\2\2<@\5\f\7"+
		"\2=@\5\16\b\2>@\5\20\t\2?,\3\2\2\2?/\3\2\2\2?\62\3\2\2\2?\66\3\2\2\2?"+
		":\3\2\2\2?;\3\2\2\2?<\3\2\2\2?=\3\2\2\2?>\3\2\2\2@\23\3\2\2\2\5\648?";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}