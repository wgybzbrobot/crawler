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
</head>
<body>
	<div id="body">
		<div style="margin: 5px 0 15px 0;">
			<a href="#" class="linkbutton" id="refresh" onclick="javascript:location.reload();">刷新</a> 
			<a href="<c:url value='slaves/preys/20'/>" class="linkbutton">任务队列</a>
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
						<a href="slaves?slaveId=${slave.machine.id}" onclick="return fasle;">${slave.machine.comment}</a>
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
