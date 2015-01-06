<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp"%>
<%@ page isELIgnored="false"%>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<html>
<head>
<title>版块配置</title>
<script type="text/javascript">
	$(function() {
		$('.addNewSectionBtn').click(function() {
			$('#sectionForm input[type!=hidden][type!=button]').val('');
			$('#sectionForm input[name=id]').val('');
			$('#sectionForm input[name=copy]').val('');
			$('div.form-wrapper').show();
		});
		$('a.form-wrapper-close').click(function() {
			$('#savemessage').text('');
			$('div.form-wrapper').hide();
		});
		/* 编辑 */
		$('a.moreinfo').click(
			function(e) {
				$('div.form-wrapper-title').text('版块详细信息');
				$('div.form-wrapper').show();
				var oldurl = $(this).attr('idx');
				console.log(oldurl);
				$('#sectionForm input[name=oldUrl]').val(oldurl);				
				$('#sectionForm').form('load', 'section/ajax/moreinfo/' + $(this).attr('id'));
		});
		/* 创建 */
		$('a.addSectionBtn').click(function(e) {
				$('div.form-wrapper-title').text($(this).attr('title'));
				$('#sectionForm input[name=id]').val($(this).attr('idx'));
				$('#sectionForm input[name=copy]').val('true');
				$('div.form-wrapper').show();
		});
		/* 删除 */
		$('a.deleteSectionBtn').click(function(e) {
			var id = $(this).attr('idx');
			$.messager.confirm('', '确定删除版块吗?', function(r){
                if (r){
                	$.ajax({
            			type : 'GET',
            			url : 'section/ajax/delete/' + id,
            			success: function(data) {
							location.reload();
            			}
            		});
                }
            });
		});
		$('li.section-li').hover(function() {
			$(this).find('.editmore span').toggle();
		});
	});

	function submitForm() {
		$('#sectionForm').form('submit', {
			url : 'section/ajax/add',
			onSubmit : function(param) {
				return $(this).form('enableValidation').form('validate');
			},
			success : function(data) {
				if (data == 'success') {
					$('#savemessage').text('保存成功, 1秒后自动刷新页面');
					setTimeout(function() {
						location.reload();
					}, 1000);
				} else if (data == 'NoConfList'){
					$('#savemessage').text('列表页没有配置，不能复制规则');
				} else if (data == 'NoAccess') {
					$('#savemessage').text('请先登录');
				}
			}
		});
	}

	function doSearch(value) {
		console.log('search ' + value);
		$.ajax({
			type : 'GET',
			url : 'section/ajax/list',
			dataType : 'json',
			data: {comment: value},
			success : function(data) {
				if ('success' == data) {
					
				}
			},
			error : function(xhr, status, error) {
			}
		});
	}
</script>
</head>
<body>
	<div id="body">
		<div style="padding:4px 0 4px 22px ;">
			<a class="linkbutton" href='javascript:history.go(-1);'>返回</a>
<%-- 			<a class="linkbutton" href='<c:url value="/website"/>'>返回</a> --%>
			<h2>
				<a href="${website.site }" target="_blank">${website.comment}</a>
			</h2>
			<a class="addNewSectionBtn linkbutton" href="javascript:void(0);">添加版块</a>
		</div>

		<div class="form-wrapper" style="display: none;">
			<a class="form-wrapper-close" href="javascript:void(0);"></a>
			<div class="form-wrapper-title">添加版块</div>
			<div class="form-wrapper-center">
				<form id="sectionForm" method="post" action="section/add" >
					<div>
						<input type="hidden"  name="website.id" value="${website.id}" />
						<input type="hidden"  name="id" />
						<input type="hidden" name="copy" />
						<input type="hidden" name="oldUrl" />
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
							<c:forEach items="${categories }" var="category">
								<option value="${category.id }">${category.comment }</option>
							</c:forEach>
							<%-- <c:if test="${fn:contains(website.site, 'baidu.com')}">
								<option value="tieba">百度贴吧</option>
							</c:if> --%>
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
					此网站没有版块，点击<a href="javascript:void(0)" class="addNewSectionBtn">添加</a>
				</div>
			</c:when>
			<c:otherwise>
				<div style="padding:4px 0 4px 22px ;">
					<input class="easyui-searchbox" data-options="prompt:'输入版块名称',searcher:doSearch" style="width: 200px" />
				</div>
				<div id="content">
					<ul>
						<c:forEach items="${page.res}" var="section">
							<li class="section-li">
								<h3>
									<span><a href="config?sectionId=${section.id}">${section.comment}</a></span>
									</h3>
									<span>[${section.category.comment}]</span>
									<div class="editmore">
										<span title="创建者">[${section.account.username}]</span>
										<span><a id="${section.id }" idx="${section.url}" class="moreinfo" href="javascript:void(0);" title="修改版块信息">&nbsp;编辑&nbsp;|</a></span>
										<span><a href="javascript:void(0);" idx="${section.id }" class="addSectionBtn" title="创建与【${section.comment}】相同规则的版块">&nbsp;创建版块&nbsp;|</a></span>
										<span><a href="javascript:void(0);" idx="${section.id }" class="deleteSectionBtn" title="删除此版块">&nbsp;删除</a></span>
									</div>
								 <br>
								<a class="url" target="_blank" href="${section.url}">${section.url}</a>
							</li>
						</c:forEach>
					</ul>
					<c:if test="${page.count > 50}">
						<div class="section-more" id="section_more">
							<a href="javascript:void(0)"><span>加载更多</span></a>
						</div>
					</c:if>

				</div>
			</c:otherwise>
		</c:choose>
	</div>
<%-- 	<div class="right-promotion">
		<c:import url="/website/auth/${website.id}" charEncoding="utf-8" />
	</div> --%>
</body>