<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- <%@ include file="/common/include.jsp"%> --%>
<%@ page import="com.zxsoft.crawler.entity.Account"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%-- <%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"%> --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>网络與情爬虫-<decorator:title /></title>
<script type="text/javascript" src="<c:url value="/resources/jquery-easyui/jquery.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/jquery-easyui/jquery.easyui.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/jquery-easyui/locale/easyui-lang-zh_CN.js" />"></script>
<link href="<c:url value="/resources/jquery-easyui/themes/default/easyui.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/jquery-easyui/themes/icon.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/css/main.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/css/form.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/css/index.css" />" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<c:url value="/resources/superfish/js/superfish.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/superfish/js/hoverIntent.js" />"></script>
<link href="<c:url value="/resources/superfish/css/superfish.css" />" rel="stylesheet" type="text/css" />
<script type="text/javascript">
$(function() {
	$('ul.sf-menu').superfish();
	 
	$.get($('#moni').attr('href') + '/reptile/list', function(data) {
		
		   $.each(data,function(i, val) {
				var html = "<li style='height:35px;'>"
					+ val.name
					+ "<ul><li><a href='"
					+ $('#moni').attr('href')
					+ "worker/"
					+ val.id
					+ "'>爬虫信息</a></li><li><a href='"
					+ $('#moni').attr('href') + "job/search/" + val.id + "'>任务队列</a><li></ul></li>";
			$('#monitor').append(html);
		});
		   if (data == null)
			   alert('he');
	});
});
</script>
<style type="text/css">
.tree_1, .tree_2{display: none;margin: 0 0 0 10px;overflow: hidden;}
</style>
<decorator:head />
</head>
<body>
	<div id="header">
		<div id="logo">
			舆情网络爬虫
			<div id="u1">
				<%
					Account account = (Account) session.getAttribute("account");
					if (account == null) {
				%>
				<a class="mnav" id="j_login" href='<c:url value="login'"/>'>登录</a>
				<a class="mnav"  href='<c:url value="register'"/>'>注册</a>
				<%
					} else {
				%>
				<a class="mnav" id="j_login" href="javascript:void(0);" onclick="return false;"><%=account.getUsername()%></a> <a
					class="mnav" id="j_logout" href='<c:url value="logout'"/>'>退出</a>
				<%
					}
				%>
			</div>
		</div>
		<div id="navigation">
			<ul  class="sf-menu">
				<li><a href="javascript:void(0);">网站管理</a>
					<ul>
						<li><a id="website" href="<c:url value='/website' />">网站配置</a></li>
						<li><a id="website" href="<c:url value='/section/search' />">版块搜索</a></li>
					</ul>
				</li>
				<li><a id="moni" href="<c:url value='/' />" onclick="return false;">爬虫监控</a>
				    <ul id="monitor"></ul>
				</li>
				<li><a href="<c:url value='/reptile' />">区域爬虫信息管理</a></li>
				<li><a id="help" href="help">帮助</a></li>
			</ul>
		</div>
	</div>

	<div id="s_wrap">
		<decorator:body />
	</div>
	<div id="footer">
		<div class="copyright">
			<p>Copyright©2002-2014 Rights Reserved 中新软件 版权所有 皖ICP备05016981号-3</p>
		</div>
		<div class="clear"></div>
	</div>
</body>
</html>
