<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- <%@ include file="/common/include.jsp"%> --%>
<%@ page import="com.zxsoft.crawler.entity.Account"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"%>
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
<script type="text/javascript">
	$(function() {
		$('#menu li').hover(
	      function () { //appearing on hover
	        $('ul', this).fadeIn();
	      },
	      function () { //disappearing on hover
	        $('ul', this).fadeOut();
	      }
	    );
	});
</script>
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
				<a class="mnav" id="j_login" href="login">登录</a>
				<%
					} else {
				%>
				<a class="mnav" id="j_login" href="javascript:void(0);" onclick="return false;"><%=account.getUsername()%></a> <a
					class="mnav" id="j_logout" href="javascript:void(0);">退出</a>
				<%
					}
				%>
			</div>
		</div>
		<div id="navigation">
			<ul id="menu">
				<li><a id="website" href="<c:url value='/website' />">网站配置</a></li>
				<li>&nbsp;&nbsp;&nbsp;</li>
				<li><a href="javascript:void(0);">爬虫监控</a>
					<ul>
						<li><a href="<c:url value='/slaves' />">爬虫信息</a></li>
						<li><a href="<c:url value='/slaves/preys/20' />">任务队列</a></li>
					</ul>
				</li>
				<li>&nbsp;&nbsp;&nbsp;</li>
				<li><a id="proxy" href="<c:url value='/proxy' />">代理配置</a></li>
				<li>&nbsp;&nbsp;&nbsp;</li>
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
