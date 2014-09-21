<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>
<script type="text/javascript" src="<c:url value="/resources/jquery-easyui/jquery.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/jquery-easyui/jquery.easyui.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/jquery-easyui/locale/easyui-lang-zh_CN.js" />"></script>

<link href="<c:url value="/resources/css/header.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/jquery-easyui/themes/default/easyui.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/jquery-easyui/themes/icon.css" />" rel="stylesheet" type="text/css" />