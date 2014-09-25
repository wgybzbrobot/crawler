<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ include file="include/include.jsp"%>
<%@ page isELIgnored="false"%>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<html>
<head>
<title>舆情网络爬虫</title>
<link href="<c:url value="/resources/form.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/index.css" />" rel="stylesheet" type="text/css" />
<script type="text/javascript">
	$(function() {
		$('#addWebsiteBtn').click(function(e) {
			$('div.form-wrapper-title').text('添加网站');
			$('#websiteForm').form('clear');
			$('div.form-wrapper').show();
		});
		$('a.form-wrapper-close').click(function() {
			$('#savemessage').text('');
			$('div.form-wrapper').hide();
		});

		$('a.moreinfo').click(
			function(e) {
				$('div.form-wrapper-title').text('编辑网站');
				$('div.form-wrapper').show();
				$('#websiteForm').form('load',
						'website/moreinfo/' + $(this).attr('id'));
			});
	});
	function doSearch(value) {
		console.log('search ' + value);
	}
	function submitForm() {
		$('#websiteForm').form('submit', {
			url : 'website/add',
			onSubmit : function(param) {
				console.log('validate');
				return $(this).form('enableValidation').form('validate');
			},
			success : function(data) {
				if (data == 'success')
					$('#savemessage').text('保存成功');
			}
		});
	}
</script>
</head>
<body>
	<jsp:include page="include/header.jsp"></jsp:include>
	<div id="body">
		<div style="padding-left: 43px;">
			<input class="easyui-searchbox" data-options="prompt:'输入网站名称',searcher:doSearch" style="width: 200px" /> <a
				id="addWebsiteBtn" class="linkbutton" href="javascript:void(0);">添加网站</a>
		</div>
		<div class="form-wrapper" style="display: none;">
			<a class="form-wrapper-close" href="javascript:;"></a>
			<div class="form-wrapper-title">编辑网站详细信息</div>
			<div class="form-wrapper-center">
				<form id="websiteForm" method="post" action="website/add" data-options="novalidate:true">
					<div>
						<label class="form-label" for="site">首页地址</label> <input class="easyui-validatebox form-input" type="text"
							name="site" data-options="required:true, validType:'url'" />
					</div>
					<div>
						<label class="form-label" for="comment">网站名称</label> <input class="easyui-validatebox form-input" type="text"
							name="comment" data-options="required:true" />
					</div>
					<div>
						<label class="form-label" for="region">区域</label> <span>境内</span><input type="radio" name="region" value="境内"
							checked="checked">&nbsp;&nbsp;&nbsp;<span>境外</span><input type="radio" name="region" value="境外">
					</div>
					<div>
						<label class="form-label" for="sitetype">网站类型</label> <select class="easyui-validatebox form-input"
							name="sitetype">
							<option value="001">类型001</option>
							<option value="002">类型002</option>
							<option value="003">类型003</option>
						</select>
					</div>
					<div>
						<label class="form-label" for="username">用户名</label> <input class="easyui-validatebox form-input" type="text"
							name="username" />
					</div>
					<div>
						<label class="form-label" for="password">密码</label> <input class="easyui-validatebox form-input" type="text"
							name="password" />
					</div>
					<div>
						<input class="form-btn" type="button" onclick="return submitForm();" value="保存" />
					</div>
					<div>
						<span id="savemessage"></span>
					</div>
				</form>
			</div>
		</div>
		<div id="content">
			<ul>
				<c:forEach items="${page.res}" var="web">
					<li class="site-li"><a href="section?websiteId=${web.id}" title="${web.site}">${web.comment }</a><span
						style="font-size: 6px; color: #e3e3e3;"><a id="${web.id}" class="moreinfo"
							href="javascript:void(0);" title="点击编辑">[${web.region}]</a></span></li>
				</c:forEach>
			</ul>
			<c:if test="${page.count > 20}">
				<div class="website-more" id="website_more">
					<a href="javascript:void(0)"><span>加载更多</span></a>
				</div>
			</c:if>

		</div>
	</div>
	<jsp:include page="include/footer.jsp"></jsp:include>
</body>