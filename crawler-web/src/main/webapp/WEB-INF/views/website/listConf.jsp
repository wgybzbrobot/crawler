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
<div>
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
								<td><form:input path="url" id="url" /></td>
								<td><label class="error" id="urlerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="category">类别</form:label></td>
								<td>
									<form:select path="category">
										<form:option value="forum">论坛</form:option>
										<form:option value="news">新闻资讯</form:option>
										<form:option value="search">搜索</form:option>
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
								<td>是否需要登录</td>
								<td><fieldset class="radio" style="border:1px solid #ccc;" >
									<label><form:radiobutton path="auth" value="false" /> 否</label> <label><form:radiobutton
											path="auth" value="true" />是</label>
								</fieldset></td>
								<td></td>
							</tr>
							<tr>
								<td><form:label path="numThreads">线程数<span class="red">*</span></form:label></td>
								<td><form:input path="numThreads" value="1"/></td>
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
								<td><form:label path="listdom">列表页DOM<span class="red">*</span><form:errors path="listdom" cssClass="error" /></form:label></td>
								<td><form:input path="listdom" /></td>
								<td><label class="error" id="listdomerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="linedom">列表行DOM<span class="red">*</span><form:errors path="linedom" cssClass="error" /></form:label></td>
								<td><form:input path="linedom" /></td>
								<td><label class="error" id="linedomerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="urldom">详细页URL DOM<span class="red">*</span><form:errors path="urldom" cssClass="error" /></form:label></td>
								<td><form:input path="urldom" /></td>
								<td><label class="error" id="urldomerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="datedom">发布日期DOM</form:label></td>
								<td><form:input path="datedom" /></td>
								<td><label class="error" id="datedomerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="updatedom">更新时间DOM</form:label></td>
								<td><form:input path="updatedom" /></td>
								<td><label class="error" id="updatedomerror"></label></td>
							</tr>
							<tr>
								<td><form:label path="synopsisdom">简介DOM</form:label></td>
								<td><form:input path="synopsisdom" /></td>
								<td><label class="error" id="synopsisdomerror"></label></td>
							</tr>
							<tr>
								<td align="right"><a href="#" class="linkbutton" id="test">验证</a></td>
								<td align="left"><a href="#" class="linkbutton" id="save">保存</a></td>
								<td><div id="saveMessage" style="color:#ff0000; text-align:center; font-size:13px; font-family: Verdana, Helvetica, Arial, sans-serif;"></div></td>
							</tr>
						</table>
					</fieldset>
				</td>
			</tr>
		</table>
	</form:form>
	</div>
	<div id="message"></div>
	<div style="text-align:center;">
		<div id="loading" ></div>
	</div>
</div>
<script type="text/javascript">
	$(function() {
		$('#url').blur(function() {
			var url = $('#url').val();
			if (url != '') {
				$.get('websiteInfo/listConfExist',{url: url}, function(data) {
					console.log(data);
					if (data == true) {
						$('#urlerror').text('已存在');
					} else {
						$('#urlerror').text('');
					}
				});
			}
		});
		
		$("#test").click(function() {
			$('#loading').show();
			$("label.error").text('');
			$.post('websiteInfo/testListConf', $('#form').serialize(), function(data) {
				$('#loading').hide();
				if (data.errors != undefined) {
					console.log (data.errors);
					$.each(data.errors, function(key, val) {
						$('#' + key).text(val);
					});
				}
				var html = '<strong>配置结果:</strong><p>分页栏:' + data.pagebar + '</p>';
				html += '<table><tr><td>编号</td><td>详细页</td><td>更新时间</td></tr>';
				$.each(data.list, function(i, item){
					html += '<tr>';
					html += '<td class="link">' + (i+1) + '</td>';
					html += '<td><a class="link" target="_blank" href="' + item.url + '">' + item.title + '</a></td>';
					html += '<td>' + (item.update == null ? '' : new Date(item.update).toLocaleString()) + '</td>';
					html += '</tr>';
				});
				html += '</table>';
				$("#message").html(html);
			});
		});
		$("#save").click(function() {
			$('#loading').show();
			$.post('websiteInfo/addListConf', $('#form').serialize(), function(data) {
				$('#loading').hide();
				if (data.errors != undefined) {
					console.log (data.errors);
					$.each(data.errors, function(key, val) {
						$('#' + key).text(val);
					});
				}
				
				var html = '<strong>配置结果:</strong><br /><p>分页栏:' + data.pagebar + '</p>';
				html += '<table><tr><td>编号</td><td>详细页</td><td>更新时间</td></tr>';
				$.each(data.list, function(i, item){
					html += '<tr>';
					html += '<td class="link">' + (i+1) + '</td>';
					html += '<td><a class="link" target="_blank" href="' + item.url + '">' + item.title + '</a></td>';
					html += '<td>' + (item.update == null ? '' : new Date(item.update).toLocaleString()) + '</td>';
					html += '</tr>';
				});
				html += '</table>';
				$("#message").html(html);
			});
		}); 
	});
</script>
	
<c:if test="${!ajaxRequest}">
</body>
</html>
</c:if>