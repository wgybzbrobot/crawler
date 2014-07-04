<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<link href="<c:url value="/resources/form.css" />" rel="stylesheet"
	type="text/css" />
<link
	href="<c:url value="/resources/jqueryui/1.8/themes/base/jquery.ui.all.css" />"
	rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<c:url value="/resources/jquery/1.6/jquery.js" />"></script>
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

#banner {
	width: 100%;
	height: 74px;
	overflow: hidden;
	background: url(http://www.zxfirewall.com/images/topbg.jpg) repeat-x;
}

.logo {
	width: 982px;
	height: 74px;
	overflow: hidden;
	margin: 0 auto;
}

#leftColumn {
	margin: 10px 0 10px 0;
	border-top-color: #ccc;
	border-top-style: solid;
	border-top-width: 1px;
	border-right-color: #ccc;
	border-right-style: solid;
	border-right-width: 1px;
	border-bottom-color: #ccc;
	border-bottom-style: solid;
	border-bottom-width: 1px;
	padding-right: 5px;
	padding-left: 5px;
	width: 170px;
	float: left;
	overflow: auto;
	padding-bottom: 3px;
	overflow: auto;
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

#navcolumn ul {
	margin: 5px 0 15px -0em;
	padding: 0;
	font-size: 15px;
}

#navcolumn li {
	list-style-type: none;
	background-image: none;
	background-repeat: no-repeat;
	background-position: 0 0.4em;
	padding-left: 16px;
	list-style-position: outside;
	line-height: 1.2em;
}

#navcolumn li.none {
	text-indent: 0.1em;
	margin-left: 1em;
}

#navcolumn h5 {
	font-size: 20px;
	border-bottom: 1px solid #aaaaaa;
	padding-top: 2px;
	padding-left: 9px;
}

div.clear {
	clear: both;
	visibility: hidden;
}

</style>
</head>
<body>
	<div id="banner">
		<div class="logo">
			<a href="./" id="bannerLeft"> <img
				src="http://www.cnzxsoft.com/images/logo.png" alt="">
			</a>
		</div>
		<div class="clear">
			<hr>
		</div>
	</div>
	<div id="leftColumn">
		<div id="navcolumn">
			<ul>
				<li class="none"><a id="search" href="search" title="查找">查找</a></li>
			</ul>
			<h5>新增</h5>
			<ul><%-- id="<c:url value="/forumConf" />" --%>
				<li class="none"><a id="forumConf" href="forumConf" title="论坛类">论坛类</a></li>
				<li class="none"><a id="newsConf" href="newsConf" title="新闻资讯类">新闻资讯类</a></li>
				<li class="none"><a id="metasearch"  href="metasearchConf" title="元搜索">元搜索类</a></li>
				<li class="none"><a id="blog"  href="blogConf" title="博客类">博客类</a></li>
			</ul>
		</div>
	</div>
	<script type="text/javascript">
	$(document).ready(function() {
		$("div#navcolumn  a").click(function(e) {
			$("div#navcolumn  a").parent().removeClass('highlight');
			$(this).parent().addClass('highlight');
			e.preventDefault();
			$.get($(this).attr("href"),  function(html) {
					$("#contentBox").replaceWith("<div id='contentBox'>" + html + "</div>");
					/* $('html, body').animate({scrollTop : $("#message").offset().top}, 500); */
			});
		});
		$('#search').click();
	});
	</script>
</body>
</html>