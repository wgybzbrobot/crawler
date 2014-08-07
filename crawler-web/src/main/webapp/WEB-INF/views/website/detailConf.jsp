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
<title>详细页配置</title>
<link href="<c:url value="/resources/form.css" />" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<c:url value="/resources/jquery/1.6/jquery.js" />"></script>
</head>
<body>
</c:if>
<div id="formsContent">
	<form:form id="form" method="post" modelAttribute="detailConf" cssClass="cleanform" onkeydown="if(event.keyCode==13){return false;}">
		<table class="maintalbe">
			<tr>
				<td valign="top">
				<fieldset>
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
							<td><form:label path="detailConf.sources">来源DOM<span class="red">*</span><form:errors path="detailConf.sources" cssClass="error" /></form:label></td>
							<td><form:input path="detailConf.sources" /></td>
						</tr>
						<tr>
							<td>抓取顺序</td>
							<td>
								<fieldset class="radio">
								<label><form:radiobutton path="detailConf.fetchorder" value="true" /> 从最后一页开始</label> <label><form:radiobutton
										path="detailConf.fetchorder" value="false" />从第一页开始</label>
								</fieldset>
							</td>
						</tr>
					</table>
					<fieldset>
						<legend>主帖</legend>
						<table class="conftable">
							<tr>
								<td><form:label path="detailConf.master">主帖DOM<span class="red">*</span><form:errors path="detailConf.master" cssClass="error" /></form:label></td>
								<td><form:input path="detailConf.master" /></td>
							</tr>
							<tr>
								<td><form:label path="detailConf.masterAuthor">楼主DOM<span class="red">*</span><form:errors path="detailConf.masterAuthor" cssClass="error" /></form:label></td>
								<td><form:input path="detailConf.masterAuthor" /></td>
							</tr>
							<tr>
								<td><form:label path="detailConf.masterDate">主帖发布时间DOM<form:errors path="detailConf.masterDate" cssClass="error" /></form:label></td>
								<td><form:input path="detailConf.masterDate" /></td>
							</tr>
							<tr>
								<td><form:label path="detailConf.masterContent">主帖内容DOM<span class="red">*</span><form:errors path="detailConf.masterContent" cssClass="error" /></form:label></td>
								<td><form:input path="detailConf.masterContent" /></td>
							</tr>
						</table>
					</fieldset>
		
					<fieldset>
						<legend>回复</legend>
						<table class="conftable">
							<tr>
								<td><form:label path="detailConf.reply">回复DOM<span class="red">*</span><form:errors path="detailConf.reply" cssClass="error" /></form:label></td>
								<td><form:input path="detailConf.reply" /></td>
							</tr>
							<tr>
								<td><form:label path="detailConf.replyAuthor">回复作者DOM<span class="red">*</span><form:errors path="detailConf.replyAuthor" cssClass="error" /></form:label></td>
								<td><form:input path="detailConf.replyAuthor" /></td>
							</tr>
							<tr>
								<td><form:label path="detailConf.replyDate">回复时间DOM<span class="red">*</span><form:errors path="detailConf.replyDate" cssClass="error" /></form:label></td>
								<td><form:input path="detailConf.replyDate" /></td>
							</tr>
							<tr>
								<td><form:label path="detailConf.replyContent">回复内容DOM<span class="red">*</span><form:errors path="detailConf.replyContent" cssClass="error" /></form:label></td>
								<td><form:input path="detailConf.replyContent" /></td>
							</tr>
						</table>
					</fieldset>
					<fieldset>
						<legend>子回复</legend>
						<table class="conftable">
							<tr>
								<td><form:label path="detailConf.subReply">子回复DOM</form:label></td>
								<td><form:input path="detailConf.subReply" /></td>
							</tr>
							<tr>
								<td><form:label path="detailConf.subReplyAuthor">子回复作者DOM</form:label></td>
								<td><form:input path="detailConf.subReplyAuthor" /></td>
							</tr>
							<tr>
								<td><form:label path="detailConf.subReplyDate">子回复日期DOM</form:label></td>
								<td><form:input path="detailConf.subReplyDate" /></td>
							</tr>
							<tr>
							 	<td><form:label path="detailConf.subReplyContent">子回复内容DOM</form:label></td>
							 	<td><form:input path="detailConf.subReplyContent" /></td>
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
