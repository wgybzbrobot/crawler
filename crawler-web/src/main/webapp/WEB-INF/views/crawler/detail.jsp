<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ include file="/common/include.jsp"%>
<%@ page isELIgnored="false"%>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<html>
<head>
<title>爬虫详细信息</title>
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
					<div style="text-align: center;">爬虫${ip}:${port}</div>
					<c:choose>
						<c:when test="${'running' eq state}">
							<h3  style="text-align: center;">正在运行的任务</h3>
							<c:choose>
								<c:when test="${empty list }">
									<div>没有任务</div>
								</c:when>
								<c:otherwise>
									<div>当前系统时间:${currentTime}</div>
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
							<h3  style="text-align: center;">完成的任务</h3>
							<c:choose>
								<c:when test="${empty list }">
									<div>没有任务</div>
								</c:when>
								<c:otherwise>
								<table style="text-align:left; font-size: 14px;">
									<thead><tr><td style="width: 35px;">序号</td><td>版块</td><td>抓取时间</td><td>数量</td><td>消息</td><td>Status</td><td>State</td></tr></thead>
									<c:forEach items="${list}" var="map" varStatus="status">
										<tr>
											<td style="width: 35px;"><span>${status.index + 1}</span></td>
											<td style="width: 120px;"><span>
												<c:choose>
													<c:when test="${'NETWORK_INSPECT' eq map.type }"><a target="_blank" href="${map.result.url}" title="网络巡检">${map.comment}</a></c:when>
													<c:when test="${'NETWORK_SEARCH' eq map.type }"><a target="_blank" href="${map.result.url}" title="全网搜索">${map.comment}</a></c:when>
												</c:choose></span>
											</td>
											<jsp:useBean id="dateValue" class="java.util.Date"/>
											<jsp:setProperty name="dateValue" property="time" value="${map.result.starttime}"/>
											<fmt:formatDate var="newdate" value="${dateValue}" pattern="MM/dd/yyyy HH:mm"/>
											<td style="width: 150px;"><span title="开始抓取时间">${newdate}</span></td>
											<td style="width: 45px;"><span style="margin: 0 12px 0 0;" title="抓取数量"><fmt:formatNumber var="count" type="number" value="${map.result.count}"  pattern="#"/>${count}</span></td>
											<td>
												<span title="${map.result.message }">
												<c:choose>   
												    <c:when test="${fn:length(map.result.message) > 40}">   
												        <c:out value="${fn:substring(map.result.message, 0, 40)}..." />   
												    </c:when>   
												   <c:otherwise>   
												      <c:out value="${map.result.message }" />   
												    </c:otherwise>   
												</c:choose> 
												</span>
											</td>
											<td>${map.result.status }</td>
											<td>${map.state }</td>
										</tr>
									</c:forEach>
								</table>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</div>
</body>