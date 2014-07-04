<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<c:if test="${!ajaxRequest}">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>网络爬虫查找</title>
<link href="<c:url	 value="/resources/form.css" />" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<c:url value="/resources/jquery/1.6/jquery.js" />"></script>
</head>
<body>
</c:if>

	<div id="formsContent">
		 <div style="padding:10px 20px">
          	<form:form action="search" method="post" modelAttribute="listConf" id="searchform">
          	网站名称：<form:input path="comment" /> <!-- <input type="submit" id="submit" value="查询"/> -->
	            <%-- <table class="search_box"  width="100%" cellpadding="0" cellspacing="0">
	              <tr>
	                <td class="td_10"><input type="hidden" name="pageSize" id="pageSize"><label>网站名称：</label></td>
	                <td width="30%">
	                	<form:input path="comment" />
	                </td>
	              </tr>
	            </table> --%>
            </form:form>
          </div>
		 <div style="padding:0px 20px">
		 <table width="100%" class="table_style_2">
              <thead>
                <tr>
                  <td><input id="top" type="checkbox" name="top" onclick="allselect();"></td>
                  <td>网站名称</td>
                  <td>URL</td>
                  <td>类别</td>
                  <td>线程数</td>
                  <td>抓取时间间隔(min)</td>
                  <td>URL过滤正则表达式</td>
                  <td>操作</td>
                </tr>
              </thead>
               <tbody>
				<c:forEach items="${page.res}" var="listConf" varStatus="status">
				<tr>
					<td><input id="${listConf.url}" type="checkbox" name="chb" value="${listConf.url}" /></td>
					<td>&nbsp;<c:out value="${listConf.comment}" /></td>
					<td>&nbsp;<c:out value="${listConf.url}" /></td>
					<td>&nbsp;<c:out value="${listConf.category}" /></td>
					<td>&nbsp;<c:out value="${listConf.numThreads}" /></td>
					<td>&nbsp;<c:out value="${listConf.fetchinterval}" /></td>
					<td>&nbsp;<c:out value="${listConf.filterurl}" /></td>
					<td class="operate_box_2"><a href="#">编辑</a></td>
				</tr>
				</c:forEach>
				<tr>
					<td colspan="8" height="20px">
						<pg:pager url="search" items="${page.count}" maxPageItems="${pageSize}"
							export="currentPage=pageNumber">
							<pg:param name="pageSize" />
							<pg:param name="accname" />
							<pg:index export="pages">
								<table>
									<tr>
										<td>当前<b class="fontred"><%=currentPage%></b>/<%=pages%>页&nbsp;&nbsp;&nbsp;&nbsp;
										</td>
										<td><pg:first>
												<a href="<%=pageUrl%>&pageNo=<%=pageNumber%>">首页</a>
											</pg:first></td>
										<td><pg:prev>
												<a href="<%=pageUrl%>&pageNo=<%=pageNumber%>">上一页</a>
											</pg:prev></td>
										<td><pg:pages>
												<%
													if (currentPage == pageNumber) {
												%>
												<font color="red"><%=pageNumber%></font>
												<%
													} else {
												%>
												<a href="<%=pageUrl%>&pageNo=<%=pageNumber%>"><%=pageNumber%></a>
												<%
													}
												%>
											</pg:pages></td>
										<td><pg:next>
												<a href="<%=pageUrl%>&pageNo=<%=pageNumber%>">下一页</a>
												</pg:next></td>
											<td><pg:last>
													<a href="<%=pageUrl%>&pageNo=<%=pageNumber%>">末页</a>&nbsp;&nbsp;</pg:last>
											</td>
	
											<td>&nbsp;</td>
											<td style="padding-left: 10px;">每页显示的记录数</td>
											<td><input type="text" class="inputDftText" onkeyup="value=value.replace(/[^\d]/g,'') " onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))" 
														value="${pageSize}" onchange="Ext.get('pageSize').dom.value=this.value;Ext.get('accform').dom.submit();"/>
											</td>
											<td>条</td>
										</tr>
									</table>
								</pg:index>
					</pg:pager></td>
				</tr>
              </tbody>
              <%-- <tr><td class="listr4" colspan="10" > <div id="tool_bar_1"> 
              	<pg:pager url="user!listUserPage.action" items="${page.count}" maxPageItems="20" export="currentPage=pageNumber">
					<pg:param name="user.name" />
					<pg:param name="pageSize" />
					<pg:index export="pages">
	              	</pg:index>
	              </pg:pager>
	            <input type="button" class="ip_3" value="批量冻结" onclick="frozeAccount();" />&nbsp;&nbsp;
                <input type="button" class="ip_3" value="批量解冻" onclick="unfreezeAccount();" />
            </div></td></tr> --%>
            </table>
		</div>
	
		<script type="text/javascript">
			$(document).ready(function() {
				$("#searchform").die().live('keydown', function(event) {
					if (event.keyCode == 13) {
						$("#searchform").submit(function() {
							$.post($(this).attr("action"), $(this).serialize(), function(html) {
								$("#formsContent").replaceWith(html);
							});
							return false;
						});
					}
				}); 
				$("#formsContent  a").click(function(e) {
					e.preventDefault();
					$.post($(this).attr("href"),  function(html) {
						$("#formsContent").replaceWith(html);
					});
				});
			});
		</script>
	</div>
	
<c:if test="${!ajaxRequest}">
</body>
</html>
</c:if>
