<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<html>
<head>
<title>网络爬虫配置</title>
<link href="<c:url value="/resources/form.css" />" rel="stylesheet"
	type="text/css" />
<%-- <link
	href="<c:url value="/resources/jqueryui/1.8/themes/base/jquery.ui.all.css" />"
	rel="stylesheet" type="text/css" /> --%>
<!--
		Used for including CSRF token in JSON requests
		Also see bottom of this file for adding CSRF token to JQuery AJAX requests
	-->
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
</head>
<body>
<div id="minMax">
	<jsp:include page="include/header.jsp"></jsp:include>
	<div id="bodyColumn">
		<div id="contentBox">
			
		</div>
	</div>
	<jsp:include page="include/footer.jsp"></jsp:include>
</div>