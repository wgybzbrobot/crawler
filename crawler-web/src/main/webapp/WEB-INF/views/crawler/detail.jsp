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
			<a href="#" class="linkbutton" id="refresh" onclick="javascript:location.reload();">刷新</a> 
			<div id="content">
				<div style="text-align: center;">
					<div>爬虫${ip}:${port}</div>
					<c:choose>
						<c:when test="${'running' eq state}">
							<h3>正在运行的任务</h3>
							<c:choose>
								<c:when test="${empty list }">
									<div>没有任务</div>
								</c:when>
								<c:otherwise>
									<ul>
										<c:forEach items="${list}" var="map" varStatus="status">
										<li class="section-li">
											<span>${status.index + 1}</span>
											<c:choose>
												<c:when test="${'NETWORK_INSPECT' eq map.type }">网络巡检
													<%-- <span style="margin: 0 12px 0 0;" title="抓取数量"><fmt:formatNumber var="count" type="number" value="${map.result.count}"  pattern="#"/>${count}</span> --%>
													<span><a target="_blank" href="${map.args.url}">${map.args.url }</a></span>
												</c:when>
												<c:when test="${'NETWORK_SEARCH' eq map.type }">全网搜索
													<%-- <span style="margin: 0 12px 0 0;" title="抓取数量"><fmt:formatNumber var="count" type="number" value="${map.result.count}"  pattern="#"/>${count}</span> --%>
													<span><a target="_blank" href="${fn:replace(map.args.engineUrl, '%s', map.args.keyword)}">${fn:replace(map.args.engineUrl, '%s', map.args.keyword)}</a></span>
												</c:when>
											</c:choose>
										</li>
										</c:forEach>
									</ul>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<h3>完成的任务</h3>
							<c:choose>
								<c:when test="${empty list }">
									<div>没有任务</div>
								</c:when>
								<c:otherwise>
									<ul>
										<c:forEach items="${list}" var="map" varStatus="status">
										<li class="section-li">
											<span>${status.index + 1}</span>
											<span>
												<c:choose>
													<c:when test="${'NETWORK_INSPECT' eq map.type }">网络巡检</c:when>
													<c:when test="${'NETWORK_SEARCH' eq map.type }">全网搜索</c:when>
												</c:choose>
											</span>
											<span style="margin: 0 12px 0 0;" title="抓取数量"><fmt:formatNumber var="count" type="number" value="${map.result.count}"  pattern="#"/>${count}</span>
											<span><a target="_blank" href="${map.result.url}">${map.result.url }</a></span>
										</li>
										</c:forEach>
									</ul>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</div>
</body>