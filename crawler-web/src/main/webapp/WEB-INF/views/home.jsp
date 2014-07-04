<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<html>
<head>
<title>网络爬虫配置</title>
<link href="<c:url value="/resources/form.css" />" rel="stylesheet"
	type="text/css" />
<link
	href="<c:url value="/resources/jqueryui/1.8/themes/base/jquery.ui.all.css" />"
	rel="stylesheet" type="text/css" />
<!--
		Used for including CSRF token in JSON requests
		Also see bottom of this file for adding CSRF token to JQuery AJAX requests
	-->
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<style type="text/css">
body {
	background-color: #fff;
	font-family: Verdana, Helvetica, Arial, sans-serif;
	margin-left: auto;
	margin-right: auto;
	background-repeat: repeat-y;
	font-size: 13px;
	padding: 0px;
}

body ul {
	list-style-type: square;
}

* {
	padding: 0;
	margin: 0;
}

.logo {
	width: 982px;
	height: 74px;
	overflow: hidden;
	margin: 0 auto;
}

ul {
	display: block;
	list-style-type: disc;
	-webkit-margin-before: 1em;
	-webkit-margin-after: 1em;
	-webkit-margin-start: 0px;
	-webkit-margin-end: 0px;
	-webkit-padding-start: 40px;
}

h5 {
	display: block;
	font-size: 0.83em;
	-webkit-margin-before: 1.67em;
	-webkit-margin-after: 1.67em;
	-webkit-margin-start: 0px;
	-webkit-margin-end: 0px;
	font-weight: bold;
}

#bodyColumn {
	margin-left: 250px;
	min-width: 800px;
}

div.clear {
	clear: both;
	visibility: hidden;
}

#footer {
	padding: 10px;
	margin: 20px 0px 20px 0px;
	border-top: solid #ccc 1px;
	color: #333333;
	font-size: x-small;
}
</style>
</head>
<body>
	<jsp:include page="include/header.jsp"></jsp:include>
	<div id="bodyColumn">
		<div id="contentBox">
			
		</div>
	</div>
	<jsp:include page="include/footer.jsp"></jsp:include>