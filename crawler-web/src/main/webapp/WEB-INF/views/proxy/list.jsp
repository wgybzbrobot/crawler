<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>代理配置</title>
<script type="text/javascript">
$.extend($.fn.validatebox.defaults.rules, {
	 ip: {
      validator: function (value) {
          return /^(\d+)\.(\d+)\.(\d+)\.(\d+)$/.test(value);
      },
      message: 'IP地址格式不正确'
  } 
});
$(function() {
	$('a.form-wrapper-close').click(function() {
		$('#savemessage').text('');
		$('div.form-wrapper').hide();
	});
	$('.showDialog').click(function () {
		$('.form-wrapper').show();
	});
});
function submitForm() {
	return $('#proxyForm').form('enableValidation').form('validate');
	/* $('#proxyForm').form('submit', {
		url : 'proxy/add',
		onSubmit : function(param) {
			console.log('hello');
			return $(this).form('enableValidation').form('validate');
		},
		success : function(data) {
			if (data == 'success') {
				$('#savemessage').text('保存成功, 2秒后自动刷新页面');
				setTimeout(function() {
					location.reload();
				}, 2000);
			}
		}
	}); */
}
</script>
</head>
<body>
	<div id="body">
		<c:choose>
			<c:when test="${empty page.res}">
				<div>
					没有代理, 点击<a href="proxy/add" onclick="return false;" class="showDialog">添加</a>
				</div>
			</c:when>
			<c:otherwise>
				<div>
					<div><a href="proxy/add" onclick="return false;" class="showDialog linkbutton">添加</a></div>
					<table style="text-align: left; font-size: 14px;">
						<thead>
							<tr>
								<td style="width: 35px;">序号</td>
								<td>IP</td>
								<td>端口号</td>
								<td>类型</td>
								<td>操作</td>
							</tr>
						</thead>
						<c:forEach items="${page.res}" var="proxy" varStatus="status">
							<tr>
								<td style="width: 35px;"><span>${status.index + 1}</span></td>
								<td style="width: 120px;"><span>${proxy.id.ip}</span></td>
								<td style="width: 120px;"><span>${proxy.id.port}</span></td>
								<td style="width: 120px;"><span>${proxy.siteType.comment}</span></td>
								<td ><span><a>修改</a> | <a>删除</a></span></td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="form-wrapper" style="display: none;">
		<a class="form-wrapper-close" href="javascript:;"></a>
		<div class="form-wrapper-title">代理配置</div>
		<div class="form-wrapper-center">
			<form id="proxyForm" method="post" action="proxy/add" data-options="novalidate:true">
				<div>
					<label class="form-label" for="id.ip">IP</label>
					<input class="easyui-validatebox form-input" type="text" name="id.ip" data-options="required:true"  validtype="ip"/>
				</div>
				<div>
					<label class="form-label" for="id.port">端口</label>
					<input class="easyui-validatebox form-input" type="text" name="id.port" data-options="required:true" />
				</div>
				<div>
					<label class="form-label" for="siteType.type">代理类型</label> <select class="easyui-validatebox form-input"
						name="siteType.type">
						<c:forEach items="${siteTypes}" var="siteType">
							<option value="${siteType.type}">${siteType.comment}</option>
						</c:forEach>
					</select>
				</div>
				<div>
					<input class="form-btn" type="submit" onclick="return submitForm();" value="保存" />
				</div>
				<div>
					<span id="savemessage"></span>
				</div>
			</form>
		</div>
	</div>
</body>
</html>
