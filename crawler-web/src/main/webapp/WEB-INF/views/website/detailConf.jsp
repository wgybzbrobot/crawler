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
<script type="text/javascript" src="<c:url value="/resources/jquery/1.6/jquery.validate.js" />"></script>
<%-- <script type="text/javascript" src="<c:url value="/resources/jquery/1.6/localization.js" />"></script> --%>
</head>
<body>
</c:if>
<div id="formsContent">
	<form:form id="form" method="post" modelAttribute="detailConf" cssClass="cleanform" onkeydown="if(event.keyCode==13){return false;}">
		<table class="maintalbe">
			<tr>
				<td colspan="2" valign="top">
				<fieldset>
					<table class="conftable">
						<tr>
							<td><form:label path="testUrl">测试页URL地址<span class="red">*</span> <form:errors path="testUrl" cssClass="error" /></form:label></td>
							<td><form:input path="testUrl" class="required" /></td>
							<td><label class="error" id="testUrlerror"></label></td>
						</tr>
						<tr>
							<td><form:label path="listUrl">列表页URL<span class="red">*</span> <form:errors path="listUrl" cssClass="error" /></form:label></td>
							<td><form:input path="listUrl" class="required"/></td>
							<td><label class="error" id="listUrlerror"></label></td>
						</tr>
						<tr>
							<td><form:label path="host">Host<span class="red">*</span> <form:errors path="host" cssClass="error" /></form:label></td>
							<td><form:input path="host" class="required"/></td>
							<td><label class="error" id="hosterror"></label></td>
						</tr>
						<tr>
							<td><form:label path="replyNum">回复数DOM<span class="red">*</span> <form:errors path="replyNum" cssClass="error" /></form:label></td>
							<td><form:input path="replyNum" class="required" /></td>
							<td><label class="error" id="replyNumerror"></label></td>
						</tr>
						<tr>
							<td><form:label path="reviewNum">浏览数DOM <form:errors path="reviewNum" cssClass="error" /></form:label></td>
							<td><form:input path="reviewNum" /></td>
							<td><label class="error" id="reviewNumerror"></label></td>
						</tr>
						<tr>
							<td><form:label path="forwardNum">转发数DOM <form:errors path="forwardNum" cssClass="error" /></form:label></td>
							<td><form:input path="forwardNum" /></td>
							<td><label class="error" id="forwardNumerror"></label></td>
						</tr>
						<tr>
							<td><form:label path="sources">来源DOM<form:errors path="sources" cssClass="error" /></form:label></td>
							<td><form:input path="sources" /></td>
							<td><label class="error" id="sourceserror"></label></td>
						</tr>
						<tr>
							<td>抓取顺序</td>
							<td>
								<fieldset class="radio">
								<label><form:radiobutton path="fetchorder" value="false" /> 从第一页开始</label> <label><form:radiobutton
										path="fetchorder" value="true" />从最后一页开始</label>
								</fieldset>
							</td>
						</tr>
					</table>
					<fieldset style="border: 1px solid #e3e3de">
						<legend>主帖</legend>
						<table class="conftable">
							<tr>
								<td><form:label path="master">主帖DOM<span class="red">*</span><form:errors path="master" cssClass="error" /></form:label></td>
								<td><form:input path="master" class="required" /></td>
								<td><label class="error" id="mastererror"></label></td>
							</tr>
							<tr>
								<td><form:label path="author">楼主DOM<span class="red">*</span><form:errors path="author" cssClass="error" /></form:label></td>
								<td><form:input path="author" class="required" /></td>
								<td><label class="error" id="authorerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="date">发布时间DOM<form:errors path="date" cssClass="error" /></form:label></td>
								<td><form:input path="date" /></td>
								<td><label class="error" id="dateerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="content">内容DOM<span class="red">*</span><form:errors path="content" cssClass="error" /></form:label></td>
								<td><form:input path="content" class="required" /></td>
								<td><label class="error" id="contenterror"></label></td>
							</tr>
						</table>
					</fieldset>
		
					<fieldset style="border: 1px solid #e3e3de">
						<legend>回复</legend>
						<table class="conftable">
							<tr>
								<td><form:label path="reply">回复DOM<form:errors path="reply" cssClass="error" /></form:label></td>
								<td><form:input path="reply" /></td>
								<td><label class="error" id="replyerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="replyAuthor">作者DOM<form:errors path="replyAuthor" cssClass="error" /></form:label></td>
								<td><form:input path="replyAuthor" /></td>
								<td><label class="error" id="replyAuthorerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="replyDate">发布时间DOM<form:errors path="replyDate" cssClass="error" /></form:label></td>
								<td><form:input path="replyDate" /></td>
								<td><label class="error" id="replyDateerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="replyContent">内容DOM<form:errors path="replyContent" cssClass="error" /></form:label></td>
								<td><form:input path="replyContent" /></td>
								<td><label class="error" id="replyContenterror"></label></td>
							</tr>
						</table>
					</fieldset>
					<fieldset style="border: 1px solid #e3e3de">
						<legend>子回复</legend>
						<table class="conftable">
							<tr>
								<td><form:label path="subReply">子回复DOM</form:label></td>
								<td><form:input path="subReply" /></td>
								<td><label class="error" id="subReplyerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="subReplyAuthor">作者DOM</form:label></td>
								<td><form:input path="subReplyAuthor" /></td>
								<td><label class="error" id="subReplyAuthorerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="subReplyDate">发布时间DOM</form:label></td>
								<td><form:input path="subReplyDate" /></td>
								<td><label class="error" id="subReplyDateerror"></label></td>
							</tr>
							<tr>
							 	<td><form:label path="subReplyContent">内容DOM</form:label></td>
							 	<td><form:input path="subReplyContent" /></td>
							 	<td><label class="error" id="subReplyContenterror"></label></td>
							 </tr>
						</table>
					</fieldset>
				</fieldset>
				</td>
			</tr>
			<tr>
				<td align="right"><a href="#" class="linkbutton" id="test">验证</a></td>
				<td align="left"><a href="#" class="linkbutton" id="save">保存</a></td>
				<td><div id="saveMessage" style="color:#ff0000; text-align:center; font-size:13px; font-family: Verdana, Helvetica, Arial, sans-serif;"></div></td>
			</tr>
		</table>
	</form:form>
</div>

<div id="message"></div>
		
<script type="text/javascript">
	$(function() {
		$('#host').blur(function() {
			$.get('websiteInfo/detailConfExist', function(data) {
				console.log(data);
				if (date == true) {
					$('#hosterror').text('Host已存在');
				} else {
					$('#hosterror').text('');
				}
			});
		});
		
		$("#test").click(function(e) {
			e.preventDefault();
			
			$('label.error').text('');
			$('#saveMessage').html('');
			var requiredFields = $('input.required');
			console.log(requiredFields);
			if (requiredFields.length != 0) {
				var i = 0;
				$.each(requiredFields, function(i, field){
					var cur = $(this);
					if ($.trim(cur.val()) == '') {
						i += 1;
						cur.parent().next().find('label.error').text('必填');
					}
				});
				if (i > 0) {
					$('#saveMessage').html('配置有误');
					return;
				}
			}
			
			$.post('websiteInfo/testDetailConf', $('#form').serialize(), function(data) {
				$("label[id$='error']").text('');
				if (data.errors != undefined && data.errors.length != 0) {
					$.each(data.errors, function(i, val) {
						$('#' + val.field).text(val.msg);
					});
				}
				var html = '<div class="conftable"><strong text>配置结果:</strong><br />';
				html += '<div>';
				if (data.info != undefined){
					$.each(data.info, function(i, val) {
						html += '<p><strong>' + i + ':</strong>' + val + '</p>';
					});
				}
				html += '</div></div>';
				
				$('#message').html(html);
			});
		}); 
		$("#message").click(function (event) { 
		    event.stopPropagation();//在Div区域内的点击事件阻止冒泡到document 
		}); 
		$('#save').click(function(e){
			e.preventDefault();
			$('label.error').text('');
			$('#saveMessage').html('');
			var requiredFields = $('input.required');
			if (requiredFields.length != 0) {
				var i = 0;
				$.each(requiredFields, function(i, field){
					var cur = $(this);
					if ($.trim(cur.val()) == '') {
						i += 1;
						cur.parent().next().find('label.error').text('必填');
					}
				});
				if (i > 0) {
					$('#saveMessage').html('配置有误');
					return;
				}
			}
			$.post('websiteInfo/saveDetailConf', $('#form').serialize(), function(data) {
				if (data.msg == 'success') {
					$('#saveMessage').html('保存成功');
				} else {
					$('#saveMessage').html('配置有误,保存失败');
				}
				$("label[id$='error']").text('');
				if (data.errors != undefined && data.errors.length != 0) {
					$.each(data.errors, function(i, val) {
					$('#' + val.field).text(val.msg);
					});
				}
				var html = '<div class="conftable"><strong text>配置结果:</strong><br />';
				html += '<div>';
				if (data.info != undefined){
					$.each(data.info, function(i, val) {
						html += '<p><strong>' + i + ':</strong>' + val + '</p>';
					});
				}
				html += '</div></div>';
				
				$('#message').html(html);
			});
		});
	});
</script>
	
<c:if test="${!ajaxRequest}">
</body>
</html>
</c:if>
