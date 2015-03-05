<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp" %>
<%@ page isELIgnored="false"%>
<meta http-equiv="Content-Type" content="text/html charset=utf-8">
<html>
<head>
<title>网站配置</title>
<script type="text/javascript">
	$(function() {
		/*
		  * 省市区联动
		  */
		  $.ajax({
				type : 'GET',
				url : 'dict/location/ajax/0',
				dataType : 'json',
				success : function(province) {
					 $.each(province, function (index, val) { 
			              var option = "<option value='" + val.id + "'>" + val.name + "</option>";
			              $("#selProvince").append(option);
			          });
				}
			});
          $("#selProvince").change(function () {
              var selValue = $(this).val(); 
              $("#selCity option:gt(0)").remove();
              $("#selDistrict option:gt(0)").remove(); 
              $.ajax({
  				type : 'GET',
  				url : 'dict/location/ajax/' + selValue,
  				dataType : 'json',
  				success : function(city) {
  					 $.each(city, function (index, val) { 
  		                      var option = "<option value='" + val.id + "'>" + val.name + "</option>";
  		                      $("#selCity").append(option);
  		              });
  				}
  			});
          });
          $("#selCity").change(function () {
              var selValue = $(this).val();
              $("#selDistrict option:gt(0)").remove(); 
              $.ajax({
    				type : 'GET',
    				url : 'dict/location/ajax/' + selValue,
    				dataType : 'json',
    				success : function(areas) {
    					   $.each(areas, function (index, val) {
    			                      var option = "<option value='" + val.id + "'>" + val.name + "</option>";
    			                      $("#selDistrict").append(option);
   			              }); 
    				}
    			});
          }); 
		
          $("input[name=region]").change(function () {
        	  if (this.value == 1) {
        		  $('#location').css('visibility', 'visible');
        	  } else {
	        	  $('#location').css('visibility', 'hidden');
        	  }
          });
		
		
		$('li.site-li').hover(function() {
			$(this).find('span.edit').toggle();
		});
		$('#addWebsiteBtn').click(function(e) {
			$('div.form-wrapper-title').text('添加网站');
			$('#websiteForm').form('clear');
			$('div.form-wrapper').show();
		});
		$('a.form-wrapper-close').click(function() {
			$('#retmsg').text('');
			$('div.form-wrapper').hide();
		});

		$('a.moreinfo').click(function(e) {
			$('div.form-wrapper-title').text('编辑网站');
			$('div.form-wrapper').show();
			$('#websiteForm').form({
				onLoadSuccess: function(data) {
					console.log(data);
					if (data.provinceId != null) {
						 $.ajax({
				  				type : 'GET',
				  				url : 'dict/location/ajax/' + data.provinceId,
				  				dataType : 'json',
				  				success : function(city) {
				  					$.each(city, function (index, val) { 
			  		                      var option = "<option value='" + val.id + "'>" + val.name + "</option>";
			  		                      $("#selCity").append(option);
			  		              	});
				  					$("#selCity").val(data.cityId);
				  				}
				  			});
						 if (data.cityId != 'null') {
							 $.ajax({
					  				type : 'GET',
					  				url : 'dict/location/ajax/' + data.cityId,
					  				dataType : 'json',
					  				success : function(city) {
					  					$.each(city, function (index, val) { 
				  		                      var option = "<option value='" + val.id + "'>" + val.name + "</option>";
				  		                      $("#selDistrict").append(option);
				  		              	});
					  					$("#selDistrict").val(data.areaId);
					  				}
					  			});
						 }
					}
				}
			});
			$('#websiteForm').form('load', 'website/ajax/moreinfo/' + $(this).attr('id'));
			return false;
		});
		$('#searchForm span.searchbox input:first').focus();
	});
	function doSearch(value) {
		$('#searchForm').submit();
	}
	function submitForm() {
		$('#retmsg').text('');
		$('#websiteForm').form('submit', {
			url : 'website/ajax/add',
			onSubmit : function(param) {
				console.log('validate');
				return $(this).form('enableValidation').form('validate');
			},
			success : function(data) {
				if (data == 'success') {
					$('#retmsg').text('保存成功, 2秒后自动刷新页面');
					setTimeout(function() {
						location.reload();
					}, 2000);
				} else if (data == 'urlExist') {
					$('#retmsg').text('网址已经存在, 请检查');
				}
			}
		});
	}
	/**
	 * 删除网站
	 */
	function delWebsite() {
		$.messager.confirm('Confirm','确定删除吗?',function(r){
		    if (r){
		    	$('#websiteForm').form('submit', {
					url : 'website/ajax/delete',
					success : function(data) {
						if (data == 'success') {
							$('#retmsg').text('删除成功, 2秒后自动刷新页面');
							setTimeout(function() {
								location.reload();
							}, 2000);
						}
					}
				});
		    }
		});
	}
</script>
</head>
<body>
	<div id="body">
		<div style="padding-left: 43px;">
			<form id="searchForm" action="website" method="post" style="display:inline; margin-right: 14px;">
				<input value="${website.comment }" name="comment" class="easyui-searchbox" data-options="prompt:'输入网站名称进行搜索',searcher:doSearch" style="width: 200px" />
			</form>
			<a id="addWebsiteBtn" class="linkbutton" href="javascript:void(0);">添加网站</a>
			<a id="addWebsiteBtn" class="linkbutton" href='<c:url value="/section/search"/>'>查找版块</a>
		</div>
		<div class="form-wrapper" style="display: none; height: 366px; width:445px;">
			<a class="form-wrapper-close" href="javascript:;"></a>
			<div class="form-wrapper-title">编辑网站详细信息</div>
			<div class="form-wrapper-center">
				<form id="websiteForm" method="post"  data-options="novalidate:true">
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
						<label class="form-label" for="tid">来源id</label> <input class="easyui-validatebox form-input" type="text"
							name="tid" data-options="required:true" />
					</div>
					<div>
						<label class="form-label" for="region">区域:</label><span>境内</span><input type="radio" name="region" value="1"
							checked="checked">&nbsp;&nbsp;&nbsp;<span>境外</span><input type="radio" name="region" value="0">
							<div id="location">
								<select id="selProvince" name="provinceId">
							        <option value="0">--请选择省份--</option>
							    </select>
							    <select id="selCity" name="cityId">
							        <option value="0">--请选择城市--</option>
							    </select>
							    <select id="selDistrict" name="areaId">
							        <option value="0">--请选择区/县--</option>
							    </select>
							</div>
							<label class="form-label" for="status">状态:</label><span>启用</span><input type="radio"  name="status" value="open"
							checked="checked">&nbsp;&nbsp;&nbsp;<span>禁用</span><input type="radio" name="status" value="close">
					</div>
				<%-- 	<div>
						<label class="form-label" for="sitetype">访问代理类型</label> <select class="easyui-validatebox form-input"
							name="sitetype">
							<c:forEach items="${siteTypes}" var="siteType">
								<option value="${siteType.type}">${siteType.comment}</option>
							</c:forEach>
						</select>
					</div> --%>
					<div>
						<input style="width:160px;" class="form-btn" type="button" onclick="return submitForm();" value="保存" />
						<input style="width:160px;" class="form-btn" type="button" onclick="return delWebsite();" value="删除" />
					</div>
					<div>
						<span id="retmsg"></span>
					</div>
				</form>
			</div>
		</div>
		<div id="content">
			<ul>
				<c:forEach items="${page.res}" var="web">
					<li class="site-li">
						<span class="edit" style="display: none;"><a href="website/moreinfo/${web.id}" onclick="return false;" id="${web.id}" class="moreinfo"  style="font-size: 8px;color:#bbE3F9;">编辑</a></span>
						<a href="section?websiteId=${web.id}" title="${web.comment}" >${web.comment }</a>
					</li>
				</c:forEach>
			</ul>
			<%-- <c:if test="${page.count > 50}">
				<div class="website-more" id="website_more">
					<a href="javascript:void(0)"><span>加载更多</span></a>
				</div>
			</c:if> --%>

		</div>
	</div>
</body>