<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<c:if test="${!ajaxRequest}">
	<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>爬虫信息</title>
<link href="<c:url	 value="/resources/form.css" />" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<c:url value="/resources/jquery/1.6/jquery.js" />"></script>
</head>
<body>
</c:if>
<div id="listToolbar">
	<table>
	 <tr><td>
	 <form id="listQueryForm">
		<table >
			<tr>
			<td><span>代理类别:</span><input id="comment" name="comment" style="line-height:16px;border:1px solid #ccc"></td>
			<td><a href="#" onclick="searchListConf();" class="linkbutton" >查询</a></td>
			</tr>
		</table>
	</form>
	</td><td align="right" style="text-align: right; right:0;">
	<a href="#" onclick="addListConf();" class="linkbutton" id="addListConf">添加代理</a>
	</tr>
	</table>
</div>
<div id="listTable" ></div>
<br />
<div id="listConfDialog" class="easyui-dialog" ></div>
<script type="text/javascript">
	$(function() {
    	
		$('#listTable').datagrid({
			title : '代理信息',
			height : 300,
			nowrap : false,
			fitColumns: true,
			collapsible : false,//是否可折叠的  
			url : 'proxyInfo/list',
			sortName: 'comment',  
			sortOrder: 'desc',  
			singleSelect : true,//是否单选  
			pagination : true,//分页控件  
			rownumbers : true,//行号  
			toolbar: '#listToolbar',
			columns : [[ 
	            {field : 'host', title : 'IP地址', width : 100}, 
	            {field : 'port', title : '端口', width : 100}, 
				{field : 'proxy_id', title : '类型', width : 100}, 
				{field : 'protocol', title : '协议', width : 50}, 
				{field : 'status', title : '状态', width : 50},
				{field : 'error', title : '出错次数', width : 50}
	        ]]
		});
	});
	function searchListConf() {
		var params = $('#listTable').datagrid('options').queryParams; //先取得 datagrid 的查询参数
		var fields =$('#listQueryForm').serializeArray(); //自动序列化表单元素为JSON对象
		$.each( fields, function(i, field){
			params[field.name] = field.value; //设置查询参数
			console.log(field.name + ':' + field.value);
		}); 
		$('#listTable').datagrid('reload'); //设置好查询参数 reload 一下就可以了
	}
	function searchDetailConf() {
		var params = $('#listTable').datagrid('options').queryParams; //先取得 datagrid 的查询参数
		var fields =$('#listQueryForm').serializeArray(); //自动序列化表单元素为JSON对象
		$.each( fields, function(i, field){
			params[field.name] = field.value; //设置查询参数
			console.log(field.name + ':' + field.value);
		}); 
		$('#listTable').datagrid('reload'); //设置好查询参数 reload 一下就可以了
	}
</script>

<c:if test="${!ajaxRequest}">
	</body>
	</html>
</c:if>
