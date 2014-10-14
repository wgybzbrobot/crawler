<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ include file="/common/include.jsp"%>
<%@ page isELIgnored="false"%>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<html>
<head>
<title>爬虫详细信息</title>
<link href="<c:url value="/resources/form.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/index.css" />" rel="stylesheet" type="text/css" />
<script type="text/javascript">
	$(function() {
		
	});
		
</script>
</head>
<body>
	<div id="body">
		<div style="padding:4px 0 4px 22px ;">
			<a class="linkbutton" href="javascript:history.go(-1);">返回</a>
			<div id="content">
				<div>
					<c:choose>
						<c:when test="${'running' eq state}">
							正在运行的任务
						</c:when>
						<c:otherwise>
							完成的任务
						</c:otherwise>
					</c:choose>
				</div>
				<c:choose>
					<c:when test="${empty list }">
						<div>没有任务</div>
					</c:when>
					<c:otherwise>
						<ul>
							<c:forEach items="${list}" var="map">
							<li class="section-li">
								<span><a href="${map.id}">${map.args.url }</a></span>
								<span>
									<c:choose>
										<c:when test="${'NETWORK_INSPECT' eq map.type }">网络巡检</c:when>
										<c:when test="${'NETWORK_SEARCH' eq map.type }">全网搜索</c:when>
									</c:choose>
								</span>
								<span><fmt:formatNumber var="count" type="number" value="${map.result.count}"  pattern="#"/>${count}</span>
							</li>
							</c:forEach>
						</ul>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</body>