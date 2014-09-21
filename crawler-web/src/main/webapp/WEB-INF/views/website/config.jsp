<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ include file="../include/include.jsp"%>
<%@ page isELIgnored="false"%>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<html>
<head>
<title>舆情网络爬虫</title>
<link href="<c:url value="/resources/form.css" />" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/index.css" />" rel="stylesheet" type="text/css" />
<script type="text/javascript">
	$(function() {
		$('fieldset > legend').next('table').hide();
		$('fieldset > legend').click(function() {
			if ($(this).next('table').is(':hidden')) {
				$(this).next('table').show();
			} else {
				$(this).next('table').hide();
			}
		});
		$('#addSectionBtn').click(function() {
			$('div.form-wrapper').show();
		});
		$('a.form-wrapper-close').click(function() {
			$('div.form-wrapper').hide();
		});
		if ($('#comment').val() == '') {
			$('#addSectionBtn').hide();
		}
	});
</script>
</head>
<body>
	<jsp:include page="../include/header.jsp"></jsp:include>
	<div>
		<a href="javascript:history.go(-1);">返回</a>
		<h3>${comment }</h3>
		<input id="addSectionBtn" type="button" value="以此规则添加版块" />
	</div>
	<div class="form-wrapper" style="display: none;">
		<a class="form-wrapper-close" href="javascript:;"></a>
		<div class="form-wrapper-title">以此规则添加新版块</div>
		<div class="form-wrapper-center">
			<form id="sectionForm" method="post" data-options="novalidate:true">
				<div>
					<label class="form-label" for="name">版块地址</label> <input class="easyui-validatebox form-input" type="text"
						name="url" data-options="required:true" />
				</div>
				<div>
					<label class="form-label" for="email">版块名称</label> <input class="easyui-validatebox form-input" type="text"
						name="comment" data-options="required:true" />
				</div>
				<div>
					<input class="form-btn" type="submit" value="保存" />
				</div>
			</form>
		</div>
	</div>
	<!-- 列表页配置, 一个版块只有一个列表页配置 -->
	<div class="easyui-panel" title="列表页配置" style="width: 400px">
		<div style="padding: 10px 60px 20px 60px">
			<form id="conflist" method="post">
				<table cellpadding="5">
					<tr>
						<td>网站名<span class="red">*</span></td>
						<td><input class="easyui-textbox" type="text" name="comment" id="comment" value="${confList.comment }"
							data-options="required:true"></input></td>
						<td><label class="error" id="commenterror"></label></td>
					</tr>
					<tr>
						<td>版块地址<span class="red">*</span></td>
						<td><input class="easyui-textbox" type="text" name="url" id="url" value="${confList.url }"
							data-options="required:true,validType:'url'"></input></td>
						<td><label class="error" id="urlerror"></label></td>
					</tr>
					<tr>
						<td><label>类别</label></td>
						<td><select class="easyui-combobox" name="category">
								<option value="forum">论坛</option>
								<option value="news">新闻资讯</option>
								<option value="search">搜索</option>
								<option value="tieba">百度贴吧</option>
						</select></td>
						<td></td>
					</tr>
					<tr>
						<td>是否js加载</td>
						<td><label><input type="radio" name="ajax" value="false" /> 否</label> <label><input type="radio"
								name="ajax" value="true" />是</label></td>
						<td></td>
					</tr>
					<tr>
						<td>是否需要登录</td>
						<td><label><input type="radio" name="auth" value="false" /> 否</label> <label><input type="radio"
								name="auth" value="true" />是</label></td>
						<td></td>
					</tr>
					<tr>
						<td><label>线程数<span class="red">*</span></label></td>
						<td><input name="numThreads" data-options="required:true,validType:'int'" value="${confList.numThreads }" /></td>
						<td></td>
					</tr>
					<tr>
						<td><label>时间间隔(分钟)<span class="red">*</span></label></td>
						<td><input name="fetchinterval" data-options="required:true,validType:'int'"
							value="${confList.fetchinterval }" /></td>
						<td></td>
					</tr>
					<tr>
						<td><label>过滤URL的正则表达式</label></td>
						<td><input name="filterurl" value="${confList.filterurl }" /></td>
						<td></td>
					</tr>
					<tr>
						<td><label>列表页DOM<span class="red">*</span></label></td>
						<td><input name="listdom" /></td>
						<td><label class="error" id="listdomerror"></label></td>
					</tr>
					<tr>
						<td><label>列表行DOM<span class="red">*</span></label></td>
						<td><input name="linedom" /></td>
						<td><label class="error" id="linedomerror"></label></td>
					</tr>
					<tr>
						<td><label>详细页URL DOM<span class="red">*</span></label></td>
						<td><input name="urldom" /></td>
						<td><label class="error" id="urldomerror"></label></td>
					</tr>
					<tr>
						<td><label>发布日期DOM</label></td>
						<td><input name="datedom" /></td>
						<td><label class="error" id="datedomerror"></label></td>
					</tr>
					<tr>
						<td><label>更新时间DOM</label></td>
						<td><input name="updatedom" /></td>
						<td><label class="error" id="updatedomerror"></label></td>
					</tr>
					<tr>
						<td><label>简介DOM</label></td>
						<td><input name="synopsisdom" /></td>
						<td><label class="error" id="synopsisdomerror"></label></td>
					</tr>
				</table>
			</form>
			<div style="text-align: center; padding: 5px">
				<a href="javascript:void(0)" class="easyui-linkbutton" onclick="validateConfList()">验证</a> <a
					href="javascript:void(0)" class="easyui-linkbutton" onclick="saveConfList()">保存</a>
			</div>
		</div>
	</div>

	<!-- 详细配置, 一个版块可能包含多个详细页配置 -->
	<div>
		<c:choose>
			<c:when test="${empty confDetails}">
				<div class="easyui-panel" title="详细页配置" style="width: 400px">
					<div style="padding: 10px 60px 20px 60px">
						<form id="confdetail" method="post">
							<table cellpadding="5">
								<tr>
									<td colspan="2" valign="top">
										<table class="conftable">
											<tr>
												<td><label>测试页URL地址<span class="red">*</span>
												</label></td>
												<td><input name="testUrl" class="required" /></td>
												<td><label class="error" id="testUrlerror"></label></td>
											</tr>
											<%-- <tr>
												<td><label>列表页URL<span class="red">*</span>
												</label></td>
												<td><input name="listUrl" value="${confDetail.id.listurl }" class="required" /></td>
												<td><label class="error" id="listUrlerror"></label></td>
											</tr> --%>
											<tr>
												<td><label>Host<span class="red">*</span>
												</label></td>
												<td><input name="host" class="required" /></td>
												<td><label class="error" id="hosterror"></label></td>
											</tr>
											<tr>
												<td><label>回复数DOM<span class="red">*</span>
												</label></td>
												<td><input name="replyNum" class="required" /></td>
												<td><label class="error" id="replyNumerror"></label></td>
											</tr>
											<tr>
												<td><label>浏览数DOM </label></td>
												<td><input name="reviewNum" /></td>
												<td><label class="error" id="reviewNumerror"></label></td>
											</tr>
											<tr>
												<td><label>转发数DOM </label></td>
												<td><input name="forwardNum" /></td>
												<td><label class="error" id="forwardNumerror"></label></td>
											</tr>
											<tr>
												<td><label>来源DOM </label></td>
												<td><input name="sources" /></td>
												<td><label class="error" id="sourceserror"></label></td>
											</tr>
											<tr>
												<td>抓取顺序</td>
												<td><label><input type="radio" name="fetchorder" value="false" /> 从第一页开始</label> <label><input
														type="radio" name="fetchorder" value="true" />从最后一页开始</label></td>
											</tr>
										</table>
										<fieldset style="border: 1px solid #e3e3de">
											<legend>主帖</legend>
											<table class="conftable">
												<tr>
													<td><label>主帖DOM<span class="red">*</span>
													</label></td>
													<td><input name="master" class="required" /></td>
													<td><label class="error" id="mastererror"></label></td>
												</tr>
												<tr>
													<td><label>楼主DOM<span class="red">*</span>
													</label></td>
													<td><input name="author" class="required" /></td>
													<td><label class="error" id="authorerror"></label></td>
												</tr>
												<tr>
													<td><label>发布时间DOM </label></td>
													<td><input name="date" /></td>
													<td><label class="error" id="dateerror"></label></td>
												</tr>
												<tr>
													<td><label>内容DOM<span class="red">*</span>
													</label></td>
													<td><input name="content" class="required" /></td>
													<td><label class="error" id="contenterror"></label></td>
												</tr>
											</table>
										</fieldset>
										<fieldset style="border: 1px solid #e3e3de">
											<legend>回复</legend>
											<table class="conftable">
												<tr>
													<td><label>回复DOM </label></td>
													<td><input name="reply" /></td>
													<td><label class="error" id="replyerror"></label></td>
												</tr>
												<tr>
													<td><label>作者DOM </label></td>
													<td><input name="replyAuthor" /></td>
													<td><label class="error" id="replyAuthorerror"></label></td>
												</tr>
												<tr>
													<td><label>发布时间DOM> </label></td>
													<td><input name="replyDate" /></td>
													<td><label class="error" id="replyDateerror"></label></td>
												</tr>
												<tr>
													<td><label>内容DOM </label></td>
													<td><input name="replyContent" /></td>
													<td><label class="error" id="replyContenterror"></label></td>
												</tr>
											</table>
										</fieldset>
										<fieldset style="border: 1px solid #e3e3de">
											<legend>子回复</legend>
											<table class="conftable">
												<tr>
													<td><label>子回复DOM</label></td>
													<td><input name="subReply" /></td>
													<td><label class="error" id="subReplyerror"></label></td>
												</tr>
												<tr>
													<td><label>作者DOM</label></td>
													<td><input name="subReplyAuthor" /></td>
													<td><label class="error" id="subReplyAuthorerror"></label></td>
												</tr>
												<tr>
													<td><label>发布时间DOM</label></td>
													<td><input name="subReplyDate" /></td>
													<td><label class="error" id="subReplyDateerror"></label></td>
												</tr>
												<tr>
													<td><label>内容DOM</label></td>
													<td><input name="subReplyContent" /></td>
													<td><label class="error" id="subReplyContenterror"></label></td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
							</table>
						</form>
					</div>
					<div style="text-align: center; padding: 5px">
						<a href="javascript:void(0)" class="easyui-linkbutton" onclick="validateConfDetail()">验证</a> <a
							href="javascript:void(0)" class="easyui-linkbutton" onclick="saveConfDetail()">保存</a>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<c:forEach items="${confDetails}" var="confDetail">
					<div class="easyui-panel" title="详细页配置" style="width: 400px">
						<div style="padding: 10px 60px 20px 60px">
							<form id="confdetail" method="post">
								<table cellpadding="5">
									<tr>
										<td colspan="2" valign="top">
											<table class="conftable">
												<tr>
													<td><label>测试页URL地址<span class="red">*</span>
													</label></td>
													<td><input name="testUrl" class="required" /></td>
													<td><label class="error" id="testUrlerror"></label></td>
												</tr>
												<%-- <tr>
												<td><label>列表页URL<span class="red">*</span>
												</label></td>
												<td><input name="listUrl" value="${confDetail.id.listurl }" class="required" /></td>
												<td><label class="error" id="listUrlerror"></label></td>
											</tr> --%>
												<tr>
													<td><label>Host<span class="red">*</span>
													</label></td>
													<td><input name="host" value="${confDetail.id.host }" class="required" /></td>
													<td><label class="error" id="hosterror"></label></td>
												</tr>
												<tr>
													<td><label>回复数DOM<span class="red">*</span>
													</label></td>
													<td><input name="replyNum" value="${confDetail.replyNum }" class="required" /></td>
													<td><label class="error" id="replyNumerror"></label></td>
												</tr>
												<tr>
													<td><label>浏览数DOM </label></td>
													<td><input name="reviewNum" /></td>
													<td><label class="error" id="reviewNumerror"></label></td>
												</tr>
												<tr>
													<td><label>转发数DOM </label></td>
													<td><input name="forwardNum" /></td>
													<td><label class="error" id="forwardNumerror"></label></td>
												</tr>
												<tr>
													<td><label>来源DOM </label></td>
													<td><input name="sources" /></td>
													<td><label class="error" id="sourceserror"></label></td>
												</tr>
												<tr>
													<td>抓取顺序</td>
													<td><label><input type="radio" name="fetchorder" value="false" /> 从第一页开始</label> <label><input
															type="radio" name="fetchorder" value="true" />从最后一页开始</label></td>
												</tr>
											</table>
											<fieldset style="border: 1px solid #e3e3de">
												<legend>主帖</legend>
												<table class="conftable">
													<tr>
														<td><label>主帖DOM<span class="red">*</span>
														</label></td>
														<td><input name="master" value="${confDetail.master }" class="required" /></td>
														<td><label class="error" id="mastererror"></label></td>
													</tr>
													<tr>
														<td><label>楼主DOM<span class="red">*</span>
														</label></td>
														<td><input name="author" value="${confDetail.author}" class="required" /></td>
														<td><label class="error" id="authorerror"></label></td>
													</tr>
													<tr>
														<td><label>发布时间DOM </label></td>
														<td><input name="date" /></td>
														<td><label class="error" id="dateerror"></label></td>
													</tr>
													<tr>
														<td><label>内容DOM<span class="red">*</span>
														</label></td>
														<td><input name="content" class="required" /></td>
														<td><label class="error" id="contenterror"></label></td>
													</tr>
												</table>
											</fieldset>
											<fieldset style="border: 1px solid #e3e3de">
												<legend>回复</legend>
												<table class="conftable">
													<tr>
														<td><label>回复DOM </label></td>
														<td><input name="reply" /></td>
														<td><label class="error" id="replyerror"></label></td>
													</tr>
													<tr>
														<td><label>作者DOM </label></td>
														<td><input name="replyAuthor" /></td>
														<td><label class="error" id="replyAuthorerror"></label></td>
													</tr>
													<tr>
														<td><label>发布时间DOM> </label></td>
														<td><input name="replyDate" /></td>
														<td><label class="error" id="replyDateerror"></label></td>
													</tr>
													<tr>
														<td><label>内容DOM </label></td>
														<td><input name="replyContent" /></td>
														<td><label class="error" id="replyContenterror"></label></td>
													</tr>
												</table>
											</fieldset>
											<fieldset style="border: 1px solid #e3e3de">
												<legend>子回复</legend>
												<table class="conftable">
													<tr>
														<td><label>子回复DOM</label></td>
														<td><input name="subReply" /></td>
														<td><label class="error" id="subReplyerror"></label></td>
													</tr>
													<tr>
														<td><label>作者DOM</label></td>
														<td><input name="subReplyAuthor" /></td>
														<td><label class="error" id="subReplyAuthorerror"></label></td>
													</tr>
													<tr>
														<td><label>发布时间DOM</label></td>
														<td><input name="subReplyDate" /></td>
														<td><label class="error" id="subReplyDateerror"></label></td>
													</tr>
													<tr>
														<td><label>内容DOM</label></td>
														<td><input name="subReplyContent" /></td>
														<td><label class="error" id="subReplyContenterror"></label></td>
													</tr>
												</table>
											</fieldset>
										</td>
									</tr>
								</table>
							</form>
						</div>
						<div style="text-align: center; padding: 5px">
							<a href="javascript:void(0)" class="easyui-linkbutton" onclick="validateConfDetail()">验证</a> <a
								href="javascript:void(0)" class="easyui-linkbutton" onclick="saveConfDetail()">保存</a>
						</div>
					</div>
				</c:forEach>
			</c:otherwise>
		</c:choose>
	</div>

	<jsp:include page="../include/footer.jsp"></jsp:include>
</body>