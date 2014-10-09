<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp"%>
<%@ page import="com.zxsoft.crawler.entity.Account"%>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<html>
<head>
<style type="text/css">
#u1 {
	z-index: 2;
	color: white;
	position: absolute;
	right: 0;
	top: 0;
	margin: 19px 0 5px 0;
	padding: 0 96px 0 0;
}
#u1 a.mnav {
	float: left;
	color: #fff;
	font-weight: bold;
	margin-left: 20px;
	font-size: 13px;
	text-decoration: underline;
}
</style>
<script>

$(function() {
	var main = top.frames["main"];
	$('#navigation ul li a').click(function() {
		main.location = '../' + $(this).attr('id');	
	});
	$('a#j_login').click(function() {
		top.location = '../login';
	});
	$('a#j_logout').click(function() {
		top.location = '../logout';
	});
});
</script>
</head>
<body>
	<header>
		<div id="logo">舆情网络爬虫
			<div id="u1">
			<% 
			Account account = (Account) session.getAttribute("account");
			if (account == null) {
			%>
			<a class="mnav" id="j_login" href="javascript:void(0);">登录</a>
			<% } else { %>
			<a class="mnav" id="j_login" href="javascript:void(0);" onclick="return false;"><%=account.getUsername() %></a>
			<a class="mnav" id="j_logout" href="javascript:void(0);">退出</a>
			<%} %>
			</div>
		</div>
		<div id="navigation">
			<ul>
				<li><a id="website" href="javascript:void(0);">网站配置</a></li>
				<li>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;</li>
				<li><a id="slaves" href="javascript:void(0);">爬虫监控</a></li>
				<li>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;</li>
				<li><a id="proxy" href="javascript:void(0);">代理配置</a></li>
				<li>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;</li>
				<li><a id="help" href="javascript:void(0);">帮助</a></li>
			</ul>
		</div>
	</header>
	<!-- <div style="margin-top: 120px;"></div> -->
</body>
</html>