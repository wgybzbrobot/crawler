<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp"%>
<%@ page session="false"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>爬虫监控</title>
</head>
<body>
	<div id="body">
		<div style="text-align: center;">
			<div id="content">
				<c:if test="${empty workers }">
					没有工作节点
				</c:if>
				<table style="text-align: left; font-size: 14px;">
				    <thead><tr><td>ID</td><td>Host</td><td>正在执行任务个数</td><td>更新时间</td></tr></thead>
					<c:forEach items="${workers}" var="worker">
                       <tr>
                        <td>${worker.workerId }</td>
                        <td>${worker.hostPort }</td>
                        <td>${worker.runningCount }</td>
                        <jsp:useBean id="dateValue" class="java.util.Date"/>
                        <jsp:setProperty name="dateValue" property="time" value="${worker.update}"/>
                        <fmt:formatDate var="update" value="${dateValue}" pattern="yyyy/MM/dd HH:mm"/>
                        <td>${update}</td>
                       </tr>
					</c:forEach>
				</table>
			</div>
		</div>
	</div>
</body>
</html>
