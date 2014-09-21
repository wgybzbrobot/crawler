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
		
		/* $.ajax({
			type : 'GET',
			url : 'website/list',
			dataType : 'json',
			success : function(data) {

			},
			error : function(xhr,  status, error) {
			}
		});*/
		
	});
	function doSearch(value) {
		console.log('search ' + value);
	}
</script>
</head>
<body>
	<jsp:include page="include/header.jsp"></jsp:include>
	<div><input class="easyui-searchbox" data-options="prompt:'输入网站名称',searcher:doSearch" style="width:200px" /></div>
	<div style="width: 911px; min-height: 100%; padding-bottom: 36px;">
		<ul>
			<c:forEach items="${page.res}" var="web">
				<li class="site-li"><a href="section?sitebase64=${web.sitebase64}&comment=${web.comment}" title="${web.site}">${web.comment }</a><span style="font-size:6px; color:#e3e3e3;">[${web.region}]</span></li>
			</c:forEach>
		</ul>
		<c:if test="${page.count > 20}">
			<div class="website-more" id="website_more"><a href="javascript:void(0)"><span>加载更多</span></a></div>
		</c:if>

	</div>
	<jsp:include page="include/footer.jsp"></jsp:include>
</body>