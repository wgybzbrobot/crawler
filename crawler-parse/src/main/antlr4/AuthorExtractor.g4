/**
 * Define a grammar called Source, 来源
 */
grammar AuthorExtractor;

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
Author_Token1: ('作者' | '编辑')  WITHESPACE* ('：' | ':') WITHESPACE* ~[ \t\r\n\u00A0]+  WITHESPACE*;
extractAuthor: Author_Token1 ;

