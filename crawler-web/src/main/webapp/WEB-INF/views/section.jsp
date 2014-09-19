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
		$('#addSectionBtn').click(function() {
			$('div.form-wrapper').show();
		});
		$('a.form-wrapper-close').click(function() {
			$('div.form-wrapper').hide();
		});
		
	});
	
	function submitForm() {
		$('#sectionForm').form('submit', {
		    url:'section/add',
		    onSubmit: function(param){
		    	console.log('validate');
		    	return $(this).form('enableValidation').form('validate');
		    },
		    success:function(data){
		       $('#savemessage').text(data);
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
	<div>
		<a class="anchorbtn" href="javascript:history.go(-1);">返回</a>
		<h2><a href="${site }" target="_blank">${comment}</a></h2>
		<input id="addSectionBtn" type="button" value="添加" class="inputbtn" />
	</div>
	<div class="form-wrapper" style="display: none;">
		<a class="form-wrapper-close" href="javascript:;"></a>
		<div class="form-wrapper-title">添加版块</div>
		<div class="form-wrapper-center">
			<form id="sectionForm" method="post" action="section/add" data-options="novalidate:true">
				<div><input hidden="true" name="site" value="${site}" /></div>
				<div>
					<label class="form-label" for="url">版块地址</label> <input class="easyui-validatebox form-input" type="text"
						name="url" data-options="required:true" />
				</div>
				<div>
					<label class="form-label" for="comment">版块名称</label> <input class="easyui-validatebox form-input" type="text"
						name="comment" data-options="required:true" />
				</div>
				<div>
					<label class="form-label" for="category">版块类别</label> <select class="easyui-validatebox form-input" name="category">
						<option value="forum">论坛</option>
						<option value="news">咨询</option>
						<option value="search">搜索</option>
					</select>
				</div>
				<div>
					<input class="form-btn" type="button" onclick="return submitForm();" value="保存" />
				</div>
				<div><span id="savemessage"></span></div>
			</form>
		</div>
	</div>
	<div>
		<c:choose>
			<c:when test="${page.count == 0}">
				<div class="none-data">此网站没有版块，点击添加</div>
			</c:when>
			<c:otherwise>
				<div>
					<input class="easyui-searchbox" data-options="prompt:'输入版块名称',searcher:doSearch" style="width: 200px" />
				</div>
				<div style="width: 911px; min-height: 100%; padding-bottom: 36px;">
					<ul>
						<c:forEach items="${page.res}" var="section">
							<li class="section-li"><h3>
									<span>[${section.category}]</span><span><a
										href="config?urlbase64=${section.urlbase64}&comment=${section.comment}">${section.comment}</a></span>
								</h3> <a class="url" href="${section.url}">${section.url}</a></li>
						</c:forEach>
					</ul>
					<c:if test="${page.count > 3}">
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