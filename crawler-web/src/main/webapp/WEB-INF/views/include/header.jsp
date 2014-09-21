<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page session="false"%>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<html>
<head>
</head>
<body>
	<header>
		<div id="logo">
				舆情网络爬虫
		</div>
		<div id="navigation">
			<ul>
				<li><a id="website_info" href="websiteInfo">网站信息</a></li>
				<li>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;</li>
				<li><a id="crawler_info" href="slaves">爬虫信息</a></li>
				<li>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;</li>
				<li><a id="proxy_info" href="proxyInfo">代理信息</a></li>
			</ul>
		</div>
	</header>
	<div style="margin-top: 120px;"></div>
	<script type="text/javascript">
		$(function() {
			$("#navigation a").click(function(e) {
					$("div#navcolumn  a").parent().removeClass('highlight');
					$(this).parent().addClass('highlight');
					e.preventDefault();
					$.get($(this).attr("href"), function(html) {
						$("#contentBox").replaceWith("<div id='contentBox'>" + html + "</div>");
					});
			});
			$('#website_info').click();
			/* $('#crawler_info').click(function() {
				console.log('Loading crawler status...');
				loadSlaves();
			}); */
		});
		
	
	</script>
</body>
</html>