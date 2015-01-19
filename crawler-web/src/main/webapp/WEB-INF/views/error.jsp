<%@ page language="java" contentType="text/html; charset=UTF-8"
	isErrorPage="true" pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.*"%>
<%
	response.setStatus(HttpServletResponse.SC_OK);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title></title>
	<link rel="stylesheet" type="text/css" href="/css/css.css" />
</head>
<body>
<div class="tleft"></div>
<div class="tright"></div>
<div class="tbg">程序错误页面</div>
	
		程序发生了错误，有可能该页面正在调试或者是设计上的缺陷.
		<br />
		你可以选择
		<br />
		<a href=<%=request.getContextPath() + "#"%>>反馈</a> 提醒我 或者
		<br />
		<a href="javascript:history.go(-1)">返回上一页</a>
		<hr width=80%>
		<h2>
			<font color=#DB1260>JSP Error Page</font>
		</h2>
		<p>
			An exception was thrown:
			<b> <%=exception.getClass()%>:<%=exception.getMessage()%></b>
		</p>
		<p>
<%-- 			With the following stack trace: <%=exception.printStackTrace() %> --%>
		</p>
		<pre>
</pre>
		<hr width=80%>
	</body>
</html>