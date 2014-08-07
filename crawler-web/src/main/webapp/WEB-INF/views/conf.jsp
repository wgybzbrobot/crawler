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
			<form:form id="form" method="post" modelAttribute="conf" cssClass="cleanform" onkeydown="if(event.keyCode==13){return false;}">
			<div id="content_left"><!-- 左侧列表页配置 -->
				<table class="maintable">
					<tr>
						<td>
							<fieldset>
								<legend>
									<h5>列表页配置</h5>
								</legend>
								<table class="conftable">
									<tr>
										<td><form:label path="listConf.comment">网站名<span class="red">*</span> <form:errors path="listConf.comment" cssClass="error" /></form:label></td>
										<td><form:input path="listConf.comment" /></td>
									</tr>
									<tr>
										<td><form:label path="listConf.url">URL<span class="red">*</span> <form:errors path="listConf.url" cssClass="error" /></form:label></td>
										<td><form:input path="listConf.url" /></td>
									</tr>
									<tr>
										<td>页面是否是Ajax加载</td>
										<td><fieldset class="radio">
											<label><form:radiobutton path="listConf.ajax" value="false" /> 否</label> <label><form:radiobutton
													path="listConf.ajax" value="true" />是</label>
										</fieldset></td>
									</tr>
									<tr>
										<td><form:label path="listConf.fetchinterval">抓取时间间隔(分钟)<span class="red">*</span><form:errors path="listConf.fetchinterval" cssClass="error" /></form:label></td>
										<td><form:input path="listConf.fetchinterval" /></td>
									</tr>
									<tr>
										<td><form:label path="listConf.pageNum">抓取翻页数<span class="red">*</span><form:errors path="listConf.pageNum" cssClass="error" /></form:label></td>
										<td><form:input path="listConf.pageNum" /></td>
									</tr>
									<tr>
										<td><form:label path="listConf.filterurl">过滤URL的正则表达式(可设置过滤广告)</form:label></td>
										<td><form:input path="listConf.filterurl" /></td>
									</tr>
									<tr>
										<td><form:label path="listConf.listdom">列表页的DOM结构<span class="red">*</span><form:errors path="listConf.listdom" cssClass="error" /></form:label></td>
										<td><form:input path="listConf.listdom" /></td>
									</tr>
									<tr>
										<td><form:label path="listConf.linedom">列表行的DOM结构<span class="red">*</span><form:errors path="listConf.linedom" cssClass="error" /></form:label></td>
										<td><form:input path="listConf.linedom" /></td>
									</tr>
									<tr>
										<td><form:label path="listConf.urldom">详细页URL的DOM结构<span class="red">*</span><form:errors path="listConf.urldom" cssClass="error" /></form:label></td>
										<td><form:input path="listConf.urldom" /></td>
									</tr>
									<tr>
										<td><form:label path="listConf.datedom">发布日期的DOM结构</form:label></td>
										<td><form:input path="listConf.datedom" /></td>
									</tr>
									<tr>
										<td><form:label path="listConf.updatedom">最近更新时间的DOM结构</form:label></td>
										<td><form:input path="listConf.updatedom" /></td>
									</tr>
								</table>
						</fieldset>
					</td>
				</tr>
			</table>
		</div>
		
		</form:form>
		
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