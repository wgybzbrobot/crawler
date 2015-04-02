/**
 * Define a grammar called Source, 来源
 */
grammar ReadNumExtractor;

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

Read_Num_Token1: '查看數'  WITHESPACE* ('：' | ':') WITHESPACE* [0-9]+  WITHESPACE*;
extractReadNum: Read_Num_Token1;
