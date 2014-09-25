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
		$('.addSectionBtn').click(function() {
			$('#sectionForm').form('clear');
			$('div.form-wrapper').show();
		});
		$('a.form-wrapper-close').click(function() {
			$('#savemessage').text('');
			$('div.form-wrapper').hide();
		});
	});

	function submitForm() {
		$('#sectionForm').form('submit', {
			url : 'section/add',
			onSubmit : function(param) {
				console.log('validate');
				return $(this).form('enableValidation').form('validate');
			},
			success : function(data) {
				if (data == 'success')
					$('#savemessage').text('保存成功');
				location.reload();
			}
		});
	}

	function doSearch(value) {
		console.log('search ' + value);
		$.ajax({
			type : 'GET',
			url : 'section/list',
			dataType : 'json',
			success : function(data) {

			},
			error : function(xhr, status, error) {
			}
		});
	}
</script>
</head>
<body>
	<jsp:include page="include/header.jsp"></jsp:include>
	<div id="body">
		<div style="padding:4px 0 4px 22px ;">
			<a class="linkbutton" href="javascript:history.go(-1);">返回</a>
			<h2>
				<a href="${site }" target="_blank">${comment}</a>
			</h2>
			<a class="addSectionBtn linkbutton" href="javascript:void(0);">添加版块</a>
		</div>

		<div class="form-wrapper" style="display: none;">
			<a class="form-wrapper-close" href="javascript:;"></a>
			<div class="form-wrapper-title">添加版块</div>
			<div class="form-wrapper-center">
				<form id="sectionForm" method="post" action="section/add" data-options="novalidate:true">
					<div>
					<label></label>
						<input type="hidden" name="website.id" value="${website.id}" />
					</div>
					<div>
						<label class="form-label" for="url">版块地址</label> <input class="easyui-validatebox form-input" type="text"
							name="url" data-options="required:true" />
					</div>
					<div>
						<label class="form-label" for="comment">版块名称</label> <input class="easyui-validatebox form-input" type="text"
							name="comment" data-options="required:true" />
					</div>
					<div>
						<label class="form-label" for="category">版块类别</label> 
						<select class="easyui-validatebox form-input" name="category.id" data-options="required:true">
							<option value="forum">论坛</option>
							<option value="news">咨询</option>
							<option value="search">搜索</option>
							<c:if test="${fn:contains(website.site, 'baidu.com')}">
								<option value="tieba">百度贴吧</option>
							</c:if>
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
		<c:choose>
			<c:when test="${page.count == 0}">
				<div class="none-data">
					此网站没有版块，点击<a href="javascript:void(0)" class="addSectionBtn">添加</a>
				</div>
			</c:when>
			<c:otherwise>
				<div style="padding:4px 0 4px 22px ;">
					<input class="easyui-searchbox" data-options="prompt:'输入版块名称',searcher:doSearch" style="width: 200px" />
				</div>
				<div id="content">
					<ul>
						<c:forEach items="${page.res}" var="section">
							<li class="section-li"><h3>
									<span>[${section.category.comment}]</span><span><a
										href="config?sectionId=${section.id}">${section.comment}</a></span>
								</h3> <a class="url" href="${section.url}">${section.url}</a></li>
						</c:forEach>
					</ul>
					<c:if test="${page.count > 15}">
						<div class="section-more" id="section_more">
							<a href="javascript:void(0)"><span>加载更多</span></a>
						</div>
					</c:if>

				</div>
			</c:otherwise>
		</c:choose>
	</div>
	<jsp:include page="include/footer.jsp"></jsp:include>
</body>