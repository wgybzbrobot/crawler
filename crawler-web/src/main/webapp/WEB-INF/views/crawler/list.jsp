<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp"%>
<%@ page session="false"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>爬虫监控</title>
<link href="<c:url	 value="/resources/form.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/index.css" />" rel="stylesheet" type="text/css" />
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
$(function() {
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
				location.reload();
			}
		});
	});
});
</script>
</head>
<body>
	<div id="body">
		<div style="margin: 5px 0 15px 0;">
			<a href="#" class="linkbutton" id="refresh" onclick="javascript:location.reload();">刷新</a> <a href="#" class="linkbutton"
				id="addInspectJobBtn" onclick="return addInspectJob();">添加网络巡检任务</a> <a href="#" class="linkbutton"
				id="addSearchJobBtn" onclick="return addSearchJob();">添加全网搜索任务</a>
		</div>
		<div id="addInspectJobDialog" style="display: none;">
			<form id="addInspectJobForm" method="post" style="width: 80%; margin: 0 auto;">
				<table class="maintable">
					<tr>
						<td>版块地址:</td>
						<td><input type="text" name="url" id="fetchurl" /></td>
						<td><label id="fetchurlerror"></label></td>
					</tr>
					<tr>
						<td colspan="3" align="center"><a href="#" class="linkbutton" id="submitInspectJob">添加</a></td>
					</tr>
				</table>
			</form>
		</div>

		<div id="addSearchJobDialog" style="display: none;">
			<form id="addSearchJobForm" method="post" style="width: 80%; margin: 0 auto;">
				<div>
					<table class="maintable">
						<tr>
							<td>关键词:</td>
							<td><input type="text" name="keyword" /></td>
							<td><label id="keyworderror"></label></td>
						</tr>
						<tr>
							<td>搜索引擎:</td>
							<td><span>百度<input name="engineId" value="http://www.baidu.com/s?wd=%s&ie=utf-8" type="checkbox"> 搜狗<input name="engineId"
									value="http://www.sogou.com/web?query=%s" type="checkbox"></span></td>
							<td></td>
						</tr>
						<tr>
							<td colspan="3" align="right"><a href="#" class="linkbutton" id="submitSearchJob">添加</a></td>
						</tr>
					</table>
				</div>
			</form>
		</div>
		<div style="text-align: center;">
			<div id="loading"></div>
			<div id="slaves">
				
			</div>
			<div id="content">
				<ul>
					<c:forEach items="${map.slaves}" var="slave">
						<c:choose>
							<c:when test='${"success" eq slave.msg}'>
								<li class="slave-li">
							</c:when>
							<c:otherwise>
								<li class="slave-li" title="${slave.msg }" style="background: #aa3333;">
							</c:otherwise>
						</c:choose>
						<a href="slaves?slaveId=${slave.machine.id}">${slave.machine.comment}</a>
						<div style="font-size: 6px; ">
							<fmt:formatNumber var="runningNum" type="number" value="${slave.runningNum}"  pattern="#"/>
							<fmt:formatNumber var="historyNum" type="number" value="${slave.historyNum}"  pattern="#"/>
							<fmt:formatNumber var="port" type="number" value="${slave.machine.port}"  pattern="#"/>
							<c:choose>
								<c:when test="${runningNum > 0 }">
									<span><a href="slaves/moreinfo/running/${slave.machine.ip}/${port}" >在运行(${runningNum})</a></span>
								</c:when>
								<c:otherwise>
									<span>在运行(${runningNum})</span>
								</c:otherwise>
							</c:choose>&nbsp;|&nbsp;
							<c:choose>
								<c:when test="${historyNum > 0 }">
									<span><a href="slaves/moreinfo/history/${slave.machine.ip}/${port}" >已完成(${historyNum})</a></span>
								</c:when>
								<c:otherwise>
									<span>已完成(${historyNum})</span>
								</c:otherwise>
							</c:choose>
						</div>
						</li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</div>
</body>
</html>
