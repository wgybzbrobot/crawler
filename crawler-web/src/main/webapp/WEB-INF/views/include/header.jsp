<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<link href="<c:url value="/resources/css/header.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/jquery-easyui/themes/default/easyui.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/jquery-easyui/themes/icon.css" />" rel="stylesheet" type="text/css" />
<%-- <link href="<c:url value="/resources/jquery-easyui/demo/demo.css" />" rel="stylesheet" type="text/css" /> --%>
<script type="text/javascript" src="<c:url value="/resources/jquery-easyui/jquery.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/jquery-easyui/jquery.easyui.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/jquery-easyui/locale/easyui-lang-zh_CN.js" />"></script>
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
</head>
<body>
	<div id="header">
		<div id="logo">
				舆情网络爬虫
		</div>
		<div id="navigation">
			<ul>
				<li><a id="website_info" href="websiteInfo">网站信息</a></li>
				<li>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;</li>
				<li><a id="proxy_info" href="proxyInfo">代理信息</a></li>
				<li>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;</li>
				<li><a id="crawler_info" href="crawlerInfo">爬虫信息</a></li>
			</ul>
		</div>
	</div>
	<script type="text/javascript">
		$(function() {
			$("#navigation a").click(
				function(e) {
					$("div#navcolumn  a").parent().removeClass('highlight');
					$(this).parent().addClass('highlight');
					e.preventDefault();
					$.get($(this).attr("href"), function(html) {
						$("#contentBox").replaceWith("<div id='contentBox'>" + html + "</div>");
					});
			});

			$('#website_info').click();
		});
	</script>
</body>
</html>