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
<title>网站信息</title>
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
			<td><span>网站名称:</span><input id="comment" name="comment" style="line-height:16px;border:1px solid #ccc"></td>
			<td><a href="#" onclick="searchListConf();" class="linkbutton" >查询</a></td>
			</tr>
		</table>
	</form>
	</td><td align="right" style="text-align: right; right:0;">
	<a href="#" onclick="addListConf();" class="linkbutton" id="addListConf">添加列表页配置</a>
	</tr>
	</table>
</div>
<div id="detailToolbar">
	<table>
	 <tr><td>
	 <form id="detailQueryForm">
		<table >
			<tr>
			<td><span>Host:</span><input id="host" name="host" style="line-height:16px;border:1px solid #ccc"></td>
			<td><a href="#" onclick="searchDetailConf();" class="linkbutton">查询</a></td>
			</tr>
		</table>
	</form>
	</td><td>
	<a href="#" onclick="addDetailConf();" class="linkbutton" id="addDetailConf">添加详细页配置</a>
	</tr>
	</table>
</div>
<div id="listTable" ></div>
<br />
<div id="detailTable"></div>
<div id="listConfDialog" class="easyui-dialog" >
</div>
<div id="detailConfDialog" class="easyui-dialog" >
</div>

<script type="text/javascript">
	$(function() {
    	
		$('#listTable').datagrid({
			title : '列表页配置信息',
			height : 300,
			nowrap : false,
			fitColumns: true,
			collapsible : false,//是否可折叠的  
			url : 'websiteInfo/list',
			sortName: 'comment',  
			sortOrder: 'desc',  
			singleSelect : true,//是否单选  
			pagination : true,//分页控件  
			rownumbers : true,//行号  
			toolbar: '#listToolbar',
			columns : [[ 
	            {field : 'comment', title : '名称', width : 100}, 
	            {field : 'url', title : 'URL', width : 300, formatter:function(value, rowDate, rowIndex) {
	            	return '<a target="_blank" href="' + value + '">' + value + '</a>';
	            } }, 
				{field : 'category', title : '类别', width : 100, formatter:function(value, rowDate, rowIndex) {
					if (value == 'forum')return '论坛';
					if (value == 'news')return '新闻资讯';
					if (value == 'tieba')return '百度贴吧';
					if (value == 'search') return '搜索';
				}}, 
				{field : 'fetchinterval', title : '抓取时间间隔', width : 70}, 
				{field : 'numThreads', title : '线程数', width : 50}
	        ]]
		});
		$('#detailTable').datagrid({
			title : '详细页配置信息',
			/* width : 900, */
			height : 300,
			nowrap : false,
			border : true,
			collapsible : true,  
			url : 'websiteInfo/detail',
			singleSelect : true,//是否单选  
			pagination : true,//分页控件  
			rownumbers : true,//行号  
			toolbar: '#detailToolbar',
			columns : [[ 
	            {field : 'listUrl', title : '列表页URL', width : 300}, 
	            {field : 'host', title : 'Host', width : 200}, 
				{field : 'fetchorder', title : '抓取顺序', width : 70, formatter:function(value, rowDate, rowIndex){
					if (value == true) return '最后一页';
					return '第一页';
				}},
	            {field : 'replyNum', title : '回复数DOM', width : 180 }, 
				{field : 'reviewNum', title : '阅览数DOM', width : 180}, 
				{field : 'forwardNum', title : '转发数DOM', width : 150}, 
				{field : 'sources', title : '来源DOM', width : 150} 
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
		var params = $('#detailTable').datagrid('options').queryParams; //先取得 datagrid 的查询参数
		var fields =$('#detailQueryForm').serializeArray(); //自动序列化表单元素为JSON对象
		$.each( fields, function(i, field){
			params[field.name] = field.value; //设置查询参数
			console.log(field.name + ':' + field.value);
		}); 
		$('#detailTable').datagrid('reload'); //设置好查询参数 reload 一下就可以了
	}
	function addListConf() {
		$('#listConfDialog').dialog({
			title:'添加列表页配置',
  			href:'websiteInfo/addListConf',
  			width:1220,
  			height:620
		});
	}
	function addDetailConf() {
		$('#detailConfDialog').dialog({
			title:'添加详细页配置',
  			href:'websiteInfo/addDetailConf',
  			width:1220,
  			height:620
		});
	}
</script>

<c:if test="${!ajaxRequest}">
	</body>
	</html>
</c:if>
