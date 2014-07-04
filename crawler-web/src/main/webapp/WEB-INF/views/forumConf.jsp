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
</head>
<body>
</c:if>
			
	<div id="formsContent">
		<div id="content_left">
		<form:form id="form" method="post" modelAttribute="forumConf" cssClass="cleanform" onkeydown="if(event.keyCode==13){return false;}">
		<table class="maintalbe">
		<tr>
			<td valign="top"><jsp:include page="listConf.jsp"></jsp:include></td>
			<td valign="top">
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
						<td><form:label path="forumDetailConf.replyNum">回复数的节点DOM<span class="red">*</span> <form:errors path="forumDetailConf.replyNum" cssClass="error" /></form:label></td>
						<td><form:input path="forumDetailConf.replyNum" /></td>
					</tr>
					<tr>
						<td><form:label path="forumDetailConf.reviewNum">浏览数的节点DOM <form:errors path="forumDetailConf.reviewNum" cssClass="error" /></form:label></td>
						<td><form:input path="forumDetailConf.reviewNum" /></td>
					</tr>
					<tr>
						<td><form:label path="forumDetailConf.forwardNum">转发数的节点DOM <form:errors path="forumDetailConf.forwardNum" cssClass="error" /></form:label></td>
						<td><form:input path="forumDetailConf.forwardNum" /></td>
					</tr>
					<%-- <tr>
						<td><form:label path="forumDetailConf.pagebar">分页栏的节点DOM<span class="red">*</span> <form:errors path="forumDetailConf.pagebar" cssClass="error" /></form:label></td>
						<td><form:input path="forumDetailConf.pagebar" /></td>
					</tr> --%>
					<tr>
						<td>抓取顺序</td>
						<td>
							<fieldset class="radio">
							<label><form:radiobutton path="forumDetailConf.fetchorder" value="true" /> 从最后一页开始</label> <label><form:radiobutton
									path="forumDetailConf.fetchorder" value="false" />从第一页开始</label>
							</fieldset>
						</td>
					</tr>
				</table>
				<fieldset>
					<legend>主帖</legend>
					<table class="conftable">
						<tr>
							<td><form:label path="forumDetailConf.master">主帖DOM<span class="red">*</span><form:errors path="forumDetailConf.master" cssClass="error" /></form:label></td>
							<td><form:input path="forumDetailConf.master" /></td>
						</tr>
						<tr>
							<td><form:label path="forumDetailConf.masterAuthor">楼主DOM<span class="red">*</span><form:errors path="forumDetailConf.masterAuthor" cssClass="error" /></form:label></td>
							<td><form:input path="forumDetailConf.masterAuthor" /></td>
						</tr>
						<tr>
							<td><form:label path="forumDetailConf.masterDate">主帖发布时间DOM<form:errors path="forumDetailConf.masterDate" cssClass="error" /></form:label></td>
							<td><form:input path="forumDetailConf.masterDate" /></td>
						</tr>
						<tr>
							<td><form:label path="forumDetailConf.masterContent">主帖内容DOM<span class="red">*</span><form:errors path="forumDetailConf.masterContent" cssClass="error" /></form:label></td>
							<td><form:input path="forumDetailConf.masterContent" /></td>
						</tr>
					</table>
				</fieldset>

				<fieldset>
					<legend>回复</legend>
					<table class="conftable">
						<tr>
							<td><form:label path="forumDetailConf.reply">回复DOM<span class="red">*</span><form:errors path="forumDetailConf.reply" cssClass="error" /></form:label></td>
							<td><form:input path="forumDetailConf.reply" /></td>
						</tr>
						<tr>
							<td><form:label path="forumDetailConf.replyAuthor">回复作者DOM<span class="red">*</span><form:errors path="forumDetailConf.replyAuthor" cssClass="error" /></form:label></td>
							<td><form:input path="forumDetailConf.replyAuthor" /></td>
						</tr>
						<tr>
							<td><form:label path="forumDetailConf.replyDate">回复时间DOM<span class="red">*</span><form:errors path="forumDetailConf.replyDate" cssClass="error" /></form:label></td>
							<td><form:input path="forumDetailConf.replyDate" /></td>
						</tr>
						<tr>
							<td><form:label path="forumDetailConf.replyContent">回复内容DOM<span class="red">*</span><form:errors path="forumDetailConf.replyContent" cssClass="error" /></form:label></td>
							<td><form:input path="forumDetailConf.replyContent" /></td>
						</tr>
					</table>
				</fieldset>

				<fieldset>
					<legend>子回复</legend>
					<table class="conftable">
						<tr>
							<td><form:label path="forumDetailConf.subReply">子回复DOM</form:label></td>
							<td><form:input path="forumDetailConf.subReply" /></td>
						</tr>
						<tr>
							<td><form:label path="forumDetailConf.subReplyAuthor">子回复作者DOM</form:label></td>
							<td><form:input path="forumDetailConf.subReplyAuthor" /></td>
						</tr>
						<tr>
							<td><form:label path="forumDetailConf.subReplyDate">子回复日期DOM</form:label></td>
							<td><form:input path="forumDetailConf.subReplyDate" /></td>
						</tr>
						<tr>
						 	<td><form:label path="forumDetailConf.subReplyContent">子回复内容DOM</form:label></td>
						 	<td><form:input path="forumDetailConf.subReplyContent" /></td>
						 </tr>
					</table>
				</fieldset>

			</fieldset>
			</td>
		</tr>
		<tr>
			<td align="right"><input id="test" class="btn" type="submit" name="action" value="验证" /></td>
			<td align="left"><input id="save" class="btn" type="submit" name="action" value="保存" /></td>
		</tr>
		</table>
			<!-- <p>
				<input id="test" class="btn" type="submit" name="action" value="验证" /><input id="save" class="btn" type="submit" name="action" value="保存" />
			</p> -->
		</form:form>
		</div>
		
		<div id="message">
			<c:choose>
				<c:when test="${not empty listRes.list}">
				<div class="dialog-box">
					<h5>列表页配置结果：</h5>
					<table>
						<c:forEach items="${listRes.list }" var="conf" varStatus="status">
							<tr>
								<td class="link">${status.index + 1 }</td>
								<td><a class="link" href="${conf.url }" target="_Blank">${conf.title }</a></td>
								<td>${conf.releasedate }</td>
							</tr>
						</c:forEach>
					</table>
					<strong>分页栏：</strong>${listRes.pagebar}
				</div>
				</c:when>
				<c:otherwise><h5>没有列表页配置结果</h5></c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${not empty detailRes.info}">
				<div class="dialog-box">
					<h5>详细页配置结果：</h5>
					<c:forEach items="${detailRes.info }" var="conf" varStatus="status">
						<strong>${conf.key }:</strong> ${conf.value } <br />
					</c:forEach>
				</div>
				</c:when>
				<c:otherwise><h5>没有详细页配置结果</h5></c:otherwise>
			</c:choose>
		</div>
		<div id="saveMessage"></div>
		
		<script type="text/javascript">
			$(document).ready(function() {
				/* $('#message').hide(); */
				$("#test").click(function(event) {
					$("#form").submit(function() {
						$.post($(this).attr("action"), $(this).serialize(), function(html) {
							$("#formsContent").replaceWith(html);
							
							$('#message').show();
							$(document).one("click", function () {//对document绑定一个影藏Div方法 
							    $("#message").hide(); 
						    });
						    event.stopPropagation();//点击Button阻止事件冒泡到document 
						    
							/* $('html, body').animate({scrollTop : $("#message").offset().top}, 500); */
						});
						return false;
					});
				}); 
				$("#message").click(function (event) { 
				    event.stopPropagation();//在Div区域内的点击事件阻止冒泡到document 
				}); 
				$('#save').click(function(event){
					$("#form").submit(function() {
						$.post('forumConf/save', $(this).serialize(), function(html) {
							$("#formsContent").replaceWith(html);
							alert('保存成功');
						});
						return false;
					});
				});
			});
		</script>
	</div>
	
<c:if test="${!ajaxRequest}">
</body>
</html>
</c:if>
