<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.zxsoft.crawler.entity.Account"%>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<html>
<head>
<title>舆情网络爬虫</title>
<link href="<c:url value="/resources/form.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/index.css" />" rel="stylesheet" type="text/css" />
</head>
<body>
<% Account account = (Account) session.getAttribute("account");
if (account == null) { %>
	<div>
		<div class="form-wrapper" style="display: block;">
			<div class="form-wrapper-title">登录网络爬虫管理帐号</div>
			<div class="form-wrapper-center">
				<form action="login" method="post">
					<div>
						<label class="form-label">帐号</label><input name="username" class="easyui-validatebox form-input" type="text" />
					</div>
					<div>
						<label class="form-label">密码</label><input name="password" class="easyui-validatebox form-input" type="password" />
					</div>
					<div>
						<input class="form-btn" type="submit" value="登录" />
					</div>
<<<<<<< HEAD
					<div>
						${msg }
					</div>
=======
>>>>>>> d5111f79183f76bad129b5773eed2f64dd1669f5
				</form>
			</div>
		</div>
	</div>
<% } else { %>
	<jsp:forward page="/"></jsp:forward>
<% } %>
</body>