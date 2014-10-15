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
	$('div.form-wrapper-center form').form('clear');
	$('#addInspectJobDialog').show();
}
function addSearchJob() {
	$('div.form-wrapper-center form').form('clear');
	$('#addSearchJobDialog').show();
}
$(function() {
	$('a.form-wrapper-close').click(function() {
		$('div.form-wrapper').hide();
	});
	$("#submitInspectJob").click(function(e) {
		$('#addInspectJobForm').form('submit', {
			url: 'slaves/addInspectJob',
			onSubmit: function() {
				var url = $('#fetchurl').val();
				var regexp = /(http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;;
				if (regexp.test(url)) {
					$('#fetchurl').attr('title','');
					return true;
				} else {
					console.log('url is invalid');
					$('#fetchurl').focus();
					$('#fetchurl').attr('title', '不符合URL规范');
					return false;
				}
			}, 
			success: function (data) {
				alert('添加成功');
				$('div.form-wrapper').hide();
			}
		});
	});
	$("#submitSearchJob").click(function(e) {
		$('#addSearchJobForm').form('submit', {
			url: 'slaves/addSearchJob',
			onSubmit: function() {
				var keyword = $('#keyword').val();
				if (keyword == '') {
					$('#keyword').focus();
					return false;
				}
				return true;
			}, 
			success: function (data) {
				alert('添加成功');
				$('div.form-wrapper').hide();
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
			<a href="#" class="linkbutton" id="refresh" onclick="javascript:location.reload();">刷新</a> 
			<a href="#" class="linkbutton" id="addInspectJobBtn" onclick="return addInspectJob();">添加网络巡检任务</a> 
			<a href="#" class="linkbutton" id="addSearchJobBtn" onclick="return addSearchJob();">添加全网搜索任务</a>
		</div>
		<div id="addInspectJobDialog" class="form-wrapper" style="display: none; width: 410px; height: 250px;">
			<a class="form-wrapper-close" href="javascript:void(0);"></a>
			<div class="form-wrapper-title">添加网络巡检任务</div>
			<div class="form-wrapper-center">
				<form id="addInspectJobForm" method="post" style="width: 90%; margin: 0 auto;">
					<div>
						<label class="form-label" for="keyword">版块地址:</label>
						<input type="text" name="url" id="fetchurl" />
					</div>
					<div><input class="form-btn" type="button"  id="submitInspectJob" value="添加" /></div>
				</form>
			</div>
		</div>

		<div id="addSearchJobDialog" class="form-wrapper" style="display: none; width: 410px; height: 250px;">
			<a class="form-wrapper-close" href="javascript:void(0);"></a>
			<div class="form-wrapper-title">添加全网搜索任务</div>
			<div class="form-wrapper-center">
				<form id="addSearchJobForm" method="post" style="width: 90%; margin: 0 auto;">
					<div>
						<label class="form-label" for="keyword">关键词:</label>
						<input type="text" name="keyword" />
					</div>
					<div>
						<label class="form-label" for="url">搜索引擎:</label>
						<c:forEach items="${engines }" var="engine">
							<span style="margin-right: 2px;"><input name="engineId" value="${engine.url}" type="checkbox" />${engine.comment}</span>
						</c:forEach>
					</div>
					<div><input class="form-btn" type="button"  id="submitSearchJob" value="添加" /></div>
				</form>
			</div>
		</div>
		<div style="text-align: center;">
			<div id="content">
				<c:if test="${empty map.slaves }">
					${map.msg }
				</c:if>
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
