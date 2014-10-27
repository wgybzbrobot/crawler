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
			url: '../addInspectJob',
			onSubmit: function() {
				var url = $('#fetchurl').val();
				console.log(url);
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
				location.reload();
			}
		});
	});
	$("#submitSearchJob").click(function(e) {
		$('#addSearchJobForm').form('submit', {
			url: '../addSearchJob',
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
	$('li.section-li').hover(function() {
		$(this).find('.editmore span').toggle();
	});
});
</script>
</head>
<body>
	<div id="body">
	<c:choose>
		<c:when test="${code eq 5000 }">
			<div>${msg}</div>
		</c:when>
		<c:otherwise>
		<div style="margin: 5px 0 15px 0;">
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
				<div>Redis任务队列总共有${count}个任务, 下面为您显示
					<form id="preyForm" action="" style="display: inline;"><input style="width: 90px;" type="text" value="${fn:length(preys)}" title="输入个数后回车"/></form>个任务
				</div>
				<div>
				<table style="text-align: left; font-size: 14px;">
					<thead>
						<tr>
							<td style="width: 35px;">序号</td>
							<td>任务</td>
							<td>上次抓取时间</td>
							<td>时间间隔</td>
							<td>预计下次抓取时间</td>
							<td>操作</td>
						</tr>
					</thead>
					<c:forEach items="${preys}" var="prey" varStatus="status">
						<tr>
							<td><span>${status.index + 1}</span></td>
							<td><span> <c:choose>
										<c:when test="${prey.jobType eq 'NETWORK_INSPECT' }">
											<a href="${prey.url}" target="_blank" title="网络巡检">${prey.comment}</a>
										</c:when>
										<c:when test="${prey.jobType eq 'NETWORK_SEARCH' }">
											<a href="${prey.url}" target="_blank" title="全网搜索">${prey.comment}</a>
										</c:when>
									</c:choose>
							</span></td>
							<td><span title="上次抓取时间"> <jsp:useBean id="prevFetchTime" class="java.util.Date" /> <jsp:setProperty
										name="prevFetchTime" property="time" value="${prey.prevFetchTime }" /> <fmt:formatDate type="both"
										value="${prevFetchTime}" pattern="yyyy-MM-dd HH:mm:ss" var="prevFetchTimef" /> ${prevFetchTimef}
							</span></td>
							<td><span title="抓取间隔时间">${prey.fetchinterval}分钟</span></td>
							<td><span title="预计下次抓取时间"> <jsp:useBean id="nextFetchTime" class="java.util.Date" /> <jsp:setProperty
										name="nextFetchTime" property="time" value="${prey.prevFetchTime + prey.fetchinterval * 60 * 1000}" /> <fmt:formatDate
										type="both" value="${nextFetchTime}" pattern="yyyy-MM-dd HH:mm:ss" var="nextFetchTimef" />
									${nextFetchTimef }
							</span></td>
							<td><span><a href="javascript:void(0);">删除</a></span> <span> <c:choose>
										<c:when test="${prey.state eq 1 }">
											<a href="javascript:void(0);">暂停</a>
										</c:when>
										<c:otherwise>
											<a href="javascript:void(0);">执行</a>
										</c:otherwise>
									</c:choose>
							</span></td>
						</tr>
					</c:forEach>
				</table>
				</div>
			</div>
		</div>
		</c:otherwise>
		</c:choose>
	</div>
</body>
</html>
