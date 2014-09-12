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
<title>爬虫监控</title>
<link href="<c:url	 value="/resources/form.css" />" rel="stylesheet" type="text/css" />
</head>
<body>
</c:if>
<div>
	<div style="margin:5px 0 15px 0;">
		<a href="#" class="linkbutton" id="refresh" onclick="return loadSlaves();">刷新</a>
		<a href="#" class="linkbutton" id="addInspectJobBtn" onclick="return addInspectJob();">添加网络巡检任务</a>
		<a href="#" class="linkbutton" id="addSearchJobBtn" onclick="return addSearchJob();">添加全网搜索任务</a>
	</div>
	<div id="addInspectJobDialog" style="display:none;">
		<form id="addInspectJobForm" method="post" style="width:80%; margin:0 auto;">
	    	<table class="maintable">
	    		<tr>
	    			<td>URL</td>
	    			<td><input type="text" name="url" id="fetchurl" /></td>
	    			<td><label id="fetchurlerror"></label></td>
	    		</tr>
	    		<%-- <tr>
	    			<td>网站类型(对应代理类型)</td>
	    			<td><select><option>国内</option><option>国外</option></select></td>
	    		</tr> --%>
	    		<tr><td colspan="3" align="center"><a href="#" class="linkbutton" id="submitInspectJob">添加</a>
	    		</td></tr>
	    	</table>
    	</form>
	</div>
	
	<div id="addSearchJobDialog" style="display:none;">
		<form id="addSearchJobForm" method="post" style="width:80%; margin:0 auto;">
			<div>
		    	<table class="maintable">
		    		<tr>
		    			<td>关键词</td>
		    			<td><input type="text" name="keyword" /></td>
		    			<td><label id="keyworderror"></label></td>
		    		</tr>
		    		<tr>
		    			<td>搜索引擎</td>
		    			<td>
		    			<span>百度<input name="engineId" value="baidu" type="checkbox">
		    			搜狗<input name="engineId" value="sougou" type="checkbox"></span>
		    			</td>
		    			<td></td>
		    		</tr>
		    		<%-- <tr>
		    			<td>网站类型(对应代理类型)</td>
		    			<td><select><option>国内</option><option>国外</option></select></td>
		    		</tr> --%>
		    		<tr><td colspan="3" align="right"><a href="#" class="linkbutton" id="submitSearchJob">添加</a></td></tr>
		    	</table>
	    	</div>
    	</form>
	</div>
	<div style="text-align:center;">
		<div id="loading" ></div>
		<div id="slaves">
		</div>
	</div>
</div>
<script type="text/javascript">
function addInspectJob() {
	$('#addInspectJobDialog').show();
	$('#addInspectJobDialog').dialog({
	    title: '添加网络巡检任务',
	    width: 400,
	    height: 200,
	    closed: false
	});
}

function addSearchJob() {
	$('#addSearchJobDialog').show();
	$('#addSearchJobDialog').dialog({
	    title: '添加全网搜索任务',
	    width: 400,
	    height: 200,
	    closed: false
	});
}
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
$(function() {
	console.log('test');
	loadSlaves();
	$("#submitInspectJob").click(function(e) {
		$('#addInspectJobForm').form('submit', {
			url: 'slaves/addInspectJob',
			onSubmit: function() {
				var url = $('#fetchurl').val();
				var regexp = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;;
				if (regexp.test(url)) {
					console.log('url is valid');
					$('#fetchurlerror').text('');
					return true;
				} else {
					console.log('url is invalid');
					$('#fetchurlerror').text('不符合URL规范');
					return false;
				}
			}, 
			success: function (data) {
				alert('添加成功');
				$('#addInspectJobDialog').dialog('close');
			}
		});
	});
	$("#submitSearchJob").click(function(e) {
		$('#addSearchJobForm').form('submit', {
			url: 'slaves/addSearchJob',
			onSubmit: function() {
				var keyword = $('#keyword').val();
				if (keyword == '') {
					$('#keyworderror').text('必填');
					return false;
				}
				return true;
			}, 
			success: function (data) {
				alert('添加成功');
				$('#addSearchJobDialog').dialog('close');
			}
		});
	});
});
</script>
<c:if test="${!ajaxRequest}">
	</body>
	</html>
</c:if>
