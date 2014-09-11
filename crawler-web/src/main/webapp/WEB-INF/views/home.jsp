<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<html>
<head>
<title>舆情网络爬虫</title>
<link href="<c:url value="/resources/form.css" />" rel="stylesheet" type="text/css" />
</head>
<body>
	<div id="minMax">
		<jsp:include page="include/header.jsp"></jsp:include>
		<div id="bodyColumn">
			<div id="contentBox"></div>
		</div>
		<jsp:include page="include/footer.jsp"></jsp:include>
	</div>
</body>