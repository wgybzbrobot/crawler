<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp" %>
<%@ page isELIgnored="false"%>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<html>
<head>
<title>网站配置</title>
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

		$('a.moreinfo').click(function(e) {
			$('div.form-wrapper-title').text('编辑网站');
			$('div.form-wrapper').show();
			$('#websiteForm').form('load', 'website/ajax/moreinfo/' + $(this).attr('id'));
			return false;
		});
		$('#searchForm span.searchbox input:first').focus();
	});
	function doSearch(value) {
		$('#searchForm').submit();
	}
	function submitForm() {
		$('#websiteForm').form('submit', {
			url : 'website/ajax/add',
			onSubmit : function(param) {
				console.log('validate');
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
		});
	}
</script>
</head>
<body>
	<div id="body">
		<div style="padding-left: 43px;">
			<form id="searchForm" action="website" method="get" style="display:inline; margin-right: 14px;">
				<input value="${website.comment }" name="comment" class="easyui-searchbox" data-options="prompt:'输入网站名称进行搜索',searcher:doSearch" style="width: 200px" />
			</form>
			<a id="addWebsiteBtn" class="linkbutton" href="javascript:void(0);">添加网站</a>
		</div>
		<div class="form-wrapper" style="display: none;">
			<a class="form-wrapper-close" href="javascript:;"></a>
			<div class="form-wrapper-title">编辑网站详细信息</div>
			<div class="form-wrapper-center">
				<form id="websiteForm" method="post" action="website/add" data-options="novalidate:true">
					<div>
					<input type="hidden" name="id">
						<label class="form-label" for="site">首页地址</label> <input class="easyui-validatebox form-input" type="text"
							name="site" data-options="required:true, validType:'url'" />
					</div>
					<div>
						<label class="form-label" for="comment">网站名称</label> <input class="easyui-validatebox form-input" type="text"
							name="comment" data-options="required:true" />
					</div>
					<div>
						<label class="form-label" for="region">区域:</label><span>境内</span><input type="radio" name="region" value="境内"
							checked="checked">&nbsp;&nbsp;&nbsp;<span>境外</span><input type="radio" name="region" value="境外">
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<label class="form-label" for="status">状态:</label><span>启用</span><input type="radio" name="status" value="open"
							checked="checked">&nbsp;&nbsp;&nbsp;<span>禁用</span><input type="radio" name="status" value="close">
					</div>
					<div>
						<label class="form-label" for="sitetype">访问代理类型</label> <select class="easyui-validatebox form-input"
							name="sitetype">
							<c:forEach items="${siteTypes}" var="siteType">
								<option value="${siteType.type}">${siteType.comment}</option>
							</c:forEach>
						</select>
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
					<li class="site-li">
						<a href="section?websiteId=${web.id}" title="${web.site}" class="easyui-tooltip" data-options="
		                    hideEvent: 'none',
		                    content: function(){
		                        return $('#toolbar${web.id}');
		                    },
		                    onShow: function(){
		                        var t = $(this);
		                        t.tooltip('tip').focus().unbind().bind('blur',function(){
		                            t.tooltip('hide');
		                        });
		                    }
		                ">${web.comment }</a></li>
					<div style="display:none">
				        <div id="toolbar${web.id}" >
							<span style="font-size: 6px; ">${web.region}</span>
				            <a href="website/moreinfo/${web.id}" onclick="return false" id="${web.id}" class="moreinfo linkbutton" >编辑</a>
				            <c:choose>
				            	<c:when test="${web.status eq 'close'}">
				            		<span style="font-size: 6px; ">已禁用</span>
				            	</c:when>
				            	<c:otherwise>
						            <span style="font-size: 6px; ">已启用</span>
				            	</c:otherwise>
				            </c:choose>
				        </div>
				    </div>
				</c:forEach>
			</ul>
			<c:if test="${page.count > 50}">
				<div class="website-more" id="website_more">
					<a href="javascript:void(0)"><span>加载更多</span></a>
				</div>
			</c:if>

		</div>
	</div>
</body>