/**
 * Define a grammar called Source, 来源
 */
grammar SourceExtractor;

@header {
	package com.zxsoft.crawler.parse.ext.generated;
}

options {
	language = Java;
}

// 忽略掉其他文本
FILTER: . -> skip ;

WITHESPACE: [ \t\r\n\u00A0];
WS : WITHESPACE+ -> skip ; // skip spaces, tabs, newlines

// 来源：安徽财经网 
Source_Token1: ('来源' | '來自')  WITHESPACE* ('：' | ':') WITHESPACE* ~[ \t\r\n\u00A0]+  WITHESPACE*;
extractSource: Source_Token1 ;

