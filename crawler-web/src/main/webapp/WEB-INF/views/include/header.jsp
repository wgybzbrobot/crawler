<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>
<html>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<link href="<c:url value="/resources/css/header.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/jquery-easyui/themes/default/easyui.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/jquery-easyui/themes/icon.css" />" rel="stylesheet" type="text/css" />
<%-- <link href="<c:url value="/resources/jquery-easyui/demo/demo.css" />" rel="stylesheet" type="text/css" /> --%>
<script type="text/javascript" src="<c:url value="/resources/jquery-easyui/jquery.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/jquery-easyui/jquery.easyui.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/jquery-easyui/locale/easyui-lang-zh_CN.js" />"></script>
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
				<li><a id="crawler_info" href="slaves">爬虫信息</a></li>
				<li>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;</li>
				<li><a id="proxy_info" href="proxyInfo">代理信息</a></li>
			</ul>
		</div>
	</div>
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
			$('#crawler_info').click(function() {
				console.log('Loading crawler status...');
				loadSlaves();
			});
		});
		
		function loadSlaves() {
			$('#loading').show();
			$('#slaves').html('');
			$.ajax({
				type: 'GET',
				url: 'slaves/list',
				dataType: 'json',
				success: function(data) {
					$('#loading').hide();
					console.log('load success');
					if (data.code == '2000') {
						
						$('#slaves').datagrid({
							title : '爬虫信息',
							height : 500,
							nowrap : false,
							fitColumns: true,
							collapsible : false,//是否可折叠的  
							data : data.slaves,
							sortName: 'slaveId',  
							sortOrder: 'desc',  
							singleSelect : true,//是否单选  
							rownumbers : true,//行号  
							columns : [[ 
					            {field : 'slaveId', title : '编号', width : 30}, 
					            {field : 'runningNum', title : '正在运行任务个数', width : 30}, 
								{field : 'historyNum', title : '完成任务个数', width : 20},
								{field : 'state', title : '状态', width : 20}, 
								{field : 'code', title : '状态返回码', width : 20}, 
								{field : 'msg', title : '说明'}
					        ]]
						});
						
					} else {
						$('#slaves').html(data.msg);
					}
				},
				error: function(xhr, status, error) {
					$('#loading').hide();
					$('#slaves').html('failure');
					console.log('failure');
				}
			});
		};
	</script>
</body>
</html>