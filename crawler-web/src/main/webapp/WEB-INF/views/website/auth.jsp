<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page isELIgnored="false"%>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<html>
<head>
<title>舆情网络爬虫</title>

<link href="<c:url value="/resources/form.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/index.css" />" rel="stylesheet" type="text/css" />
<script type="text/javascript">
$(function() {
	$('a.form-wrapper-close').click(function() {
		$('#savemessage').text('');
		$('div.auth-form').hide();
	});
	$('div.authInfo').hover(function() {
		$(this).find('span.deleteAuth').toggle();	
	});
	$('span.deleteAuth a').click(function() {
		var $del = $(this);
		$.messager.confirm('', '确定删除帐号吗?', function(r){
	        if (r){
	        	$.ajax({
	    			type : 'GET',
	    			url : $del.attr('href'),
	    			success: function(data) {
				        location.reload();
	    			}
	    		});
	        }
	    });
		return false;
	});
});
function showAddAuth() {
	$('div.auth-form input[name=id]').val('');
	$('div.auth-form input[name=username]').val('');
	$('div.auth-form input[name=password]').val('');
	$('div.auth-form').show();
}
function editAuth(url) {
	$('div.auth-form').show();
	$('#authForm').form('load', url);
	return false;
}
function saveAuth() {
	$('#authForm').form('submit', {
		url: 'website/auth/add',
		onSubmit : function(param) {
			return $(this).form('enableValidation').form('validate');
		},
		success : function(data) {
			if (data == 'success') {
				location.reload();
			} else {
				console.log('add Auth failure');
			}
		}
	});
}
</script>
</head>
<body>
	<div>
		<div class="right-promotion-title">登录帐号</div>
		<div style="margin: 0 10px; padding: 0 10px; text-align: left;">
			<c:forEach items="${auths}" var="auth">
				<div class="authInfo" style="margin: 2px 0; font-size: 13px;">
					<a href="website/auth/info/${auth.id}" onclick="return editAuth('website/auth/info/${auth.id}');">
					${auth.username} &nbsp;/&nbsp;${auth.password}</a>
					<span class="deleteAuth" style="color: #ff0000;display: none;">
						<a href="website/auth/delete/${auth.id}" onclick="return false;">&nbsp;&nbsp;删除</a>
					</span>
				</div>
			</c:forEach>
			<div>
				<input type="button" onclick="return showAddAuth();" value="添加登录帐号" style="margin:10px auto; background: #E1EFD6; border: none; cursor: pointer;"/>
			</div>
		</div>
	</div>
	
	<div class="form-wrapper auth-form" style="display: none; height: 270px;">
			<a class="form-wrapper-close" href="javascript:void(0);"></a>
			<div class="form-wrapper-title">登录帐号</div>
			<div class="form-wrapper-center">
				<form id="authForm" method="post" action="website/auth/add" >
					<div>
						<input type="hidden"  name="id" />
						<input type="hidden"  name="website.id" value="${website.id}" />
					</div>
					<div>
						<label class="form-label" for="username">帐号</label> <input class="easyui-validatebox form-input" type="text"
							name="username" data-options="required:true" />
					</div>
					<div>
						<label class="form-label" for="password">密码</label> <input class="easyui-validatebox form-input" type="text"
							name="password" data-options="required:true" />
					</div>
					<div>
						<input class="form-btn" type="button" onclick="return saveAuth();" value="保存" />
					</div>
				</form>
			</div>
		</div>
</body>