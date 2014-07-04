<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<c:if test="${!ajaxRequest}">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>网络爬虫【论坛类】配置</title>
<link href="<c:url value="/resources/form.css" />" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<c:url value="/resources/jquery/1.6/jquery.js" />"></script>
<style type="text/css">
h2 {
	color: #8cab4e;
	text-align: center;
}
#content_left {
	width: 636px;
	float:left;
	padding-left:34px;
}
#content_right {
	width: 555px;
	float:right;
	border-left: 2px solid #8cab4e;
}
</style>
</head>
<body>
</c:if>		
	<div id="formsContent">
		<div id="content_left">
		<form:form id="form" method="post" modelAttribute="newsConf" cssClass="cleanform" onkeydown="if(event.keyCode==13){return false;}">
		<table class="maintable">
			<tr>
				<td><jsp:include page="listConf.jsp"></jsp:include></td>
				<td>
					<fieldset>
						<legend>
							<h5>详细页配置</h5>
						</legend>
						<table class="conftable">
							<tr>
								<td><form:label path="testUrl">测试页URL<span class="red">*</span> <form:errors path="testUrl" cssClass="error" /></form:label></td>
								<td><form:input path="testUrl" /></td>
							</tr>
							<tr>
								<td><form:label path="detailConf.replyNum">回复数的节点DOM<span class="red">*</span> <form:errors path="detailConf.replyNum" cssClass="error" /></form:label></td>
								<td><form:input path="detailConf.replyNum" /></td>
							</tr>
							<tr>
								<td><form:label path="detailConf.reviewNum">浏览数的节点DOM <form:errors path="detailConf.reviewNum" cssClass="error" /></form:label></td>
								<td><form:input path="detailConf.reviewNum" /></td>
							</tr>
							<tr>
								<td><form:label path="detailConf.forwardNum">转发数的节点DOM <form:errors path="detailConf.forwardNum" cssClass="error" /></form:label></td>
								<td><form:input path="detailConf.forwardNum" /></td>
							</tr>
							<tr>
								<td><form:label path="detailConf.releaseDate">发布时间DOM<form:errors path="detailConf.releaseDate" cssClass="error" /></form:label></td>
								<td><form:input path="detailConf.releaseDate" /></td>
							</tr>
							<tr>
								<td><form:label path="detailConf.content">内容DOM<span class="red">*</span><form:errors path="detailConf.content" cssClass="error" /></form:label></td>
								<td><form:input path="detailConf.content" /></td>
							</tr>
							<tr>
								<td><form:label path="detailConf.sources">来源DOM<span class="red">*</span><form:errors path="detailConf.sources" cssClass="error" /></form:label></td>
								<td><form:input path="detailConf.sources" /></td>
							</tr>
						</table>
					</fieldset>
				</td>
			</tr>
			<tr>
				<td align="right"><input id="test" class="btn" type="submit" name="action" value="验证" /></td>
				<td align="left"><input id="save" class="btn" type="submit" name="action" value="保存" /></td>
			</tr>
		</table>
			<!-- <p>
				<input class="btn" type="submit" name="action" value="验证" /><input class="btn" type="submit" name="action" value="保存" />
			</p> -->
		</form:form>
		</div>
		
		<div id="content_right">
			<c:if test="${not empty listRes.list}">
		    <h3>列表页配置结果：</h3>
				<div id="message">
					<table>
						<c:forEach items="${listRes.list }" var="conf" varStatus="status">
							<tr>
								<td class="link">${status.index + 1 }</td>
								<td><a class="link" href="${conf.url }">${conf.title }</a></td>
								<td>${conf.releasedate }</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</c:if>
			<c:if test="${not empty detailRes.info}">
			<h3>详细页配置结果：</h3>
				<div id="message">
					<c:forEach items="${detailRes.info }" var="conf" varStatus="status">
						<strong>${conf.key }:</strong> ${conf.value } <br />
					</c:forEach>
				</div>
			</c:if>
		</div>
		
		<script type="text/javascript">
			$(document).ready(function() {
				$(document).ready(function() {
					$("input[name=action]").click(function() {
						$("#form").submit(function() {
							$.post($(this).attr("action"), $(this).serialize(), function(html) {
								$("#formsContent").replaceWith(html);
								$('html, body').animate({scrollTop : $("#message").offset().top}, 500);
							});
							return false;
						});
					}); 
				});
			});
		</script>
	</div>
	
<c:if test="${!ajaxRequest}">
</body>
</html>
</c:if>