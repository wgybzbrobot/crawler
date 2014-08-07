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
<title>列表页配置</title>
<link href="<c:url value="/resources/form.css" />" rel="stylesheet" type="text/css" />
</head>
<body>
</c:if>		
<div id="formsContent">
	<form:form id="form" method="post" modelAttribute="listConf" cssClass="cleanform" onkeydown="if(event.keyCode==13){return false;}">
		<table class="maintable">
			<tr>
				<td>
					<fieldset>
						<table class="conftable">
							<tr>
								<td><form:label path="comment">网站名<span class="red">*</span> <form:errors path="comment" cssClass="error" /></form:label></td>
								<td><form:input path="comment" /></td>
								<td><label class="error" id="commenterror"></label></td>
							</tr>
							<tr>
								<td><form:label path="url">网站地址<span class="red">*</span> <form:errors path="url" cssClass="error" /></form:label></td>
								<td><form:input path="url" /></td>
								<td><label class="error" id="urlerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="category">类别</form:label></td>
								<td>
									<form:select path="category">
										<form:option value="forum">论坛</form:option>
										<form:option value="news">新闻资讯</form:option>
										<form:option value="tieba">百度贴吧</form:option>
									</form:select>
								</td>
								<td></td>
							</tr>
							<tr>
								<td>是否js加载</td>
								<td><fieldset class="radio" style="border:1px solid #ccc;" >
									<label><form:radiobutton path="ajax" value="false" /> 否</label> <label><form:radiobutton
											path="ajax" value="true" />是</label>
								</fieldset></td>
								<td></td>
							</tr>
							<tr>
								<td><form:label path="numThreads">线程数<span class="red">*</span></form:label></td>
								<td><form:input path="numThreads" /></td>
								<td></td>
							</tr>
							<tr>
								<td><form:label path="fetchinterval">时间间隔(分钟)<span class="red">*</span><form:errors path="fetchinterval" cssClass="error" /></form:label></td>
								<td><form:input path="fetchinterval" /></td>
								<td></td>
							</tr>
							<tr>
								<td><form:label path="filterurl">过滤URL的正则表达式</form:label></td>
								<td><form:input path="filterurl" /></td>
							</tr>
							<tr>
								<td><form:label path="listdom">列表页的DOM结构<span class="red">*</span><form:errors path="listdom" cssClass="error" /></form:label></td>
								<td><form:input path="listdom" /></td>
								<td><label class="error" id="listdomerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="linedom">列表行的DOM结构<span class="red">*</span><form:errors path="linedom" cssClass="error" /></form:label></td>
								<td><form:input path="linedom" /></td>
								<td><label class="error" id="linedomerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="urldom">详细页URL的DOM结构<span class="red">*</span><form:errors path="urldom" cssClass="error" /></form:label></td>
								<td><form:input path="urldom" /></td>
								<td><label class="error" id="urldomerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="datedom">发布日期的DOM结构</form:label></td>
								<td><form:input path="datedom" /></td>
								<td></td>
							</tr>
							<tr>
								<td><form:label path="updatedom">最近更新时间的DOM结构</form:label></td>
								<td><form:input path="updatedom" /></td>
								<td></td>
							</tr>
							<tr>
								<td align="right"><a href="#" class="linkbutton" id="test">验证</a></td>
								<td align="left"><a href="#" class="linkbutton" id="save">保存</a></td>
								<td></td>
							</tr>
						</table>
				</fieldset>
			</td>
		</tr>
	</table>
</form:form>
<div id="message"></div>
<script type="text/javascript">
	$(function() {
		$("#save").click(function() {
			$.post('websiteInfo/addListConf', $('#form').serialize(), function(data) {
				
				if (data.error != undefined) {
					console.log (data.error.field);
					$('#' + data.error.field).text(data.error.defaultMessage);
					return;
				}
				console.log('success');		
				
				var html = '<strong>配置结果:</strong><br />分页栏:' + data.pagebar;
				html += '<table><tr><td>编号</td><td>详细页</td><td>更新时间</td></tr>';
				$.each(data.list, function(i, item){
					html += '<tr>';
					html += '<td class="link">' + (i+1) + '</td>';
					html += '<td><a class="link" target="_blank" href="' + item.url + '">' + item.title + '</a></td>';
					html += '<td>' + item.releasedate + '</td>';
					html += '</tr>';
				});
				html += '</table>';
				
				$("#message").append(html);
			});
		}); 
		$("#test").click(function() {
			$.post('websiteInfo/testListConf', $('#form').serialize(), function(html) {
				console.log('success');		
				
				/* $("#formsContent").replaceWith(html);
				$('html, body').animate({scrollTop : $("#message").offset().top}, 500); */
			});
		}); 
	});
</script>
</div>
	
<c:if test="${!ajaxRequest}">
</body>
</html>
</c:if>