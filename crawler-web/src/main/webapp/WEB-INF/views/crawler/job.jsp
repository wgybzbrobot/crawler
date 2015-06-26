<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>爬虫监控-${reptile.name}</title>
<script type="text/javascript">
$(function() {
	$('a.form-wrapper-close').click(function() {
		$('div.form-wrapper').hide();
	});
	$('form input').keydown(function() {
		$('div.message').text('');
	});
	
	$("#submitInspectJob").click(function(e) {
		if ($(this).val() != '添加' || !$('#addInspectJobForm').form('validate')) {
			return ;
		}
		$(this).val('正在添加中...');
		$('#addInspectJobForm').form('submit', {
			success: function (data) {
				data = $.parseJSON(data);
				if (data.msg == 'noconflist') {
					$('div.message').text('该版块没有配置, 不能执行任务');
					$("#submitInspectJob").val('添加');
					return false;
				} else if (data.msg == 'jobexist') {
					$('div.message').text('该版块已添加成任务, 不能再次添加');
					$("#submitInspectJob").val('添加');
					return false;
				} else if (data.msg == 'jedisconnectionexception') {
					$('div.message').text('无法连接redis');
					$("#submitInspectJob").val('添加');
					return false;
				}
				alert('添加成功');
				$('div.form-wrapper').hide();
				location.reload();
			},
		});
	});
	$("#submitSearchJob").click(function(e) {
		if ($(this).val() != '添加' || !$('#addSearchJobForm').form('validate')) {
			return ;
		}
		$(this).val('正在添加中...');
		$('#addSearchJobForm').form('submit', {
			url: '../ajax/addSearchJob',
			onSubmit: function() {
				var keyword = $('#keyword').val();
				if (keyword == '') {
					$('#keyword').focus();
					return false;
				}
				return true;
			}, 
			success: function (data) {
				console.log('data: ' + data);
				/* data = $.parseJSON(data); */
				alert(data); 
				$('div.form-wrapper').hide();
				location.reload();
			}
		});
	});
	
	$('#sectionFilter').keydown(function(e){
		  if(e.keyCode==13){
			  $.ajax({
      			type : 'POST',
      			url : '../ajax/preys',
      			dataType : 'json',
    			data: {name: $(this).val()},
      			success: function(data) {
      				if (data.length == 0) {
      					$('div.right-panel ol').text('没有记录');
      					return false;
      				} else {
      					$('div.right-panel ol').text('');
      				}
      				$('div.right-panel ol li').remove();
      				$.each(data, function(i, val) {
      					$('div.right-panel ol').append('<li style="line-height: 22px;"><a  target="_blank" href="'+ val.url + '">' + val.comment + '</a></li>');
      				});
      			}
      		});
		  }
	});
	
	$('#jobFilterForm input').keydown(function(e){
		  if(e.keyCode==13){
			 $('#jobFilterForm').submit();
		  }
	});
/* 	$('a.delJob').click(function(e){
		var $tr = $(this).parents('tr')[0];
		var jobId = $tr.select("span:eq(1)").val();
		$.ajax({
   			type : 'GET',
   			url : $(this).attr('href') ,
   			dataType : 'json',
 			data: {jobId: jobId},
   			success: function(data) {
   				console.log(data);
   				if (data.code == 1) {
   					// $tr.remove();
   					location.reload();
   				} else {
   					
   				}
   			}
   		});
	}); */
	$('a.haltJob').click(function(e){
		var comment = $(this).attr('comment');
		var start = $(this).attr('start');
		var $tr = $(this).parents('tr')[0];
		$.ajax({
   			type : 'POST',
   			url : $(this).attr('href') ,
   			dataType : 'json',
 			data: {comment: comment, start: start},
   			success: function(data) {
   				console.log(data);
   				if (data.code == 1) {
   					location.reload();
   				} else {
   					
   				}
   			}
   		});
	});
$(".edit").click(function () {
    console.log('hello');
   /*  $('#jobForm').form('load', $(this).attr("href"));
    $('#jobInfo').show(); */
    $('#jobForm').find("input[type=text]").val("");
//    var jobFormData = $("#jobForm").serialize(); //自动将form表单封装成json 
    $.ajax({
   	   type: "GET",
   	   url: this.href,
//    	   contentType: "application/json", //WebService 会返回Json类型 
//    	   data: jobFormData, //这里是要传递的参数
//        dataType: 'json',    
   	   success: function(jobConf){
   	     //console.log( "Data Saved: " + jobConf.url );
	   	 for (var field in jobConf) {
	         $('input[name="'+ field +'"]').val(jobConf[field]);
	         if(field=="listRule"){
	        	 for (var rule in jobConf.listRule) {
	        		 $('input[name="listRule.'+ rule +'"]').val(jobConf.listRule[rule]);
	        	 }
	         }
	         if(field=="detailRules"){
	        	 for (var detail in jobConf.detailRules[0]) {
	        		 $('input[name="detailRules.'+ detail +'"]').val(jobConf.detailRules[0][detail]);
	        	 }
	         }
	     }
	   	 $('#jobInfo').show();
// 	   	console.log(jobFormData);
   	   }
   	});
    return false;
});

});
</script>
</head>
<body>
    <!-- job detail data -->
    <div id="jobInfo" class="form-wrapper"
        style="display: none; height: 686px; width: 645px;">
        <a class="form-wrapper-close" href="javascript:;"></a>
        <div class="form-wrapper-title">编辑任务信息</div>
        <div class="form-wrapper-center">
            <form id="jobForm" method="post" action="<c:url value='/job/addJob/${reptileId}' />">
<!--                 <input type="hidden" name="jobId" > -->
                <div>
                	url:<input  type="text" name="url"/>
                	来源:<input  type="text" name="source_name" /><br />
					来源id:<input  type="text" name="source_id" />
					版块名称:<input  type="text" name="type" /><br />
					时间间隔:<input  type="text" name="fetchinterval" />
					循环:<input  type="text" name="recurrence" /><br />
					网页编码:<input type="text" name="encode" />
					关键字:<input type="text" name="keywordEncode" /><br />
					动态url:<input type="text" name="autoUrl" />
					平台类型:<input type="text" name="platform" /><br />
					jobId:<input  type="text" name="jobId" />
					ip:<input  type="text" name="ip" /><br />
					境内外标识:<input  type="text" name="country_code" />
					省份代码:<input  type="text" name="province_code" /><br />
					城市代码:<input  type="text" name="city_code" />
					版块id:<input  type="text" name="sectionId" /><br />
                </div>
                <div style="display:none">
                	<input type="text" name="jobType" />
                    <input type="text" name="workerId" />
                    <input type="text" name="keyword" />
                    <input type="text" name="state" />
                    <input type="text" name="prevFetchTime" />
                    <input type="text" name="start" />
                    <input type="text" name="count" />
                    <input type="text" name="retry" />
                    <input type="text" name="username" />
                    <input type="text" name="password" />
                    <input type="text" name="auth" />
                    <input type="text" name="location" />
                    <input type="text" name="locationCode" />
                </div>
                <div>
                    列表页规则:<br />
                    ajax加载:<input type="text" name="listRule.ajax"/>
                    列表页dom:<input type="text" name="listRule.listdom" /><br />
                    列表行dom:<input type="text" name="listRule.linedom" />
                    详细页url:<input type="text" name="listRule.urldom" /><br />
                    更新时间:<input type="text" name="listRule.updatedom" />
                    类型:<input type="text" name="listRule.category" /><br />
                    时间dom:<input type="text" name="listRule.datedom" />
                    简介dom:<input type="text" name="listRule.synopsisdom" /><br />
                    作者dom:<input type="text" name="listRule.authordom" />
                </div>
                <div>
                    详细页规则:<br />
                    来源:<input type="text" name="detailRules.sources" />
                    host:<input type="text" name="detailRules.host" /><br />
                    回复数:<input type="text" name="detailRules.replyNum" />
                    浏览数:<input type="text" name="detailRules.reviewNum" /><br />
                    forwardNum:<input type="text" name="detailRules.forwardNum" />
                    抓取顺序（true从最后一页开始抓）:<input type="text" name="detailRules.fetchorder" /><br />
                    ajax:<input type="text" name="detailRules.ajax" /><br />
                    主帖模块:<br />
                    主贴dom:<input type="text" name="detailRules.master" />
                    作者dom:<input type="text" name="detailRules.author" /><br />
                    发布时间:<input type="text" name="detailRules.date" />
                    内容dom:<input type="text" name="detailRules.content" /><br />
                    回复模块:<br />
                    reply:<input type="text" name="detailRules.reply" />
                    回复用户:<input type="text" name="detailRules.replyAuthor" /><br />
                    回复日期:<input type="text" name="detailRules.replyDate" />
                    回复内容:<input type="text" name="detailRules.replyContent" /><br />
                    <div style="display:none">
                    <input type="text" name="detailRules.subReply" />
                    <input type="text" name="detailRules.subReplyAuthor" /><br />
                    <input type="text" name="detailRules.subReplyDate" />
                    <input type="text" name="detailRules.subReplyContent" /><br />
                    </div>

                </div>
                <div>
                    <input style="width: 160px;" class="form-btn" type="submit" value="保存" />
                </div>
            </form>
        </div>
    </div>
	<div id="body">
	爬虫区域：${reptile.name}
	<c:choose>
		<c:when test="${code ge 5000 }">
			<div>${msg}</div>
		</c:when>
		<c:otherwise>
		<div style="text-align: center;">
			<div id="content">
				<div>当前系统时间:${currentTime },Redis任务队列总共有${page.count}个任务</div>
				<div>
					<form id="jobFilterForm" action="<c:url value='/job/search/${reptileId}' />" method="get">
					   查询任务:<input id="jobText" name="query" type="text" value="${query }" title="输入任务名称或网址后回车搜索" />
					   显示第<input  name="start" value="${start }" type="text"  style="width: 30px;" />条至第<input name="end"  value="${end }" type="text" style="width: 30px;"/>条
					</form>
				</div>
				<div>
				<table style="text-align: left; font-size: 14px;">
					<thead>
						<tr>
							<td style="width: 35px;">序号</td>
							<td>任务ID</td>
							<td>任务</td>
							<td>开始时间</td>
							<td>上次抓取时间</td>
							<td>时间间隔</td>
							<td>预计下次抓取时间</td>
							<td>抓取次数</td>
							<td>是否循环</td>
							<td>操作</td>
						</tr>
					</thead>
					<c:forEach items="${page.res}" var="jobConf" varStatus="status">
						<tr>
							<td><span>${status.index + 1}</span></td>
							<td><span>${jobConf.jobId}</span></td>
							<td><span> <c:choose>
										<c:when test="${jobConf.jobType eq 'NETWORK_INSPECT' }">
											<a href="${jobConf.url}" target="_blank" title="网络巡检">${jobConf.source_name.concat('-').concat(jobConf.type) }</a>
										</c:when>
										<c:when test="${jobConf.jobType eq 'NETWORK_SEARCH' }">
											<a href="${jobConf.url}" target="_blank" title="全网搜索">${jobConf.source_name.concat('-').concat(jobConf.type)}-${jobConf.keyword}</a>
										</c:when>
									</c:choose>
							</span></td>
							<td><span title="开始时间"> <jsp:useBean id="startTime" class="java.util.Date" /> <jsp:setProperty
										name="startTime" property="time" value="${jobConf.start }" /> <fmt:formatDate type="both"
										value="${startTime}" pattern="yyyy-MM-dd HH:mm:ss" var="startTimef" /> ${startTimef}
							</span></td>
							<td><span title="上次抓取时间"> <jsp:useBean id="prevFetchTime" class="java.util.Date" /> <jsp:setProperty
										name="prevFetchTime" property="time" value="${jobConf.prevFetchTime }" /> <fmt:formatDate type="both"
										value="${prevFetchTime}" pattern="yyyy-MM-dd HH:mm:ss" var="prevFetchTimef" /> ${prevFetchTimef}
							</span></td>
							<td><span title="抓取间隔时间">${jobConf.fetchinterval}分钟</span></td>
							<td><span title="预计下次抓取时间"> <jsp:useBean id="nextFetchTime" class="java.util.Date" /> <jsp:setProperty
										name="nextFetchTime" property="time" value="${jobConf.prevFetchTime + jobConf.fetchinterval * 60 * 1000}" /> <fmt:formatDate
										type="both" value="${nextFetchTime}" pattern="yyyy-MM-dd HH:mm:ss" var="nextFetchTimef" />
									${nextFetchTimef }
							</span></td>
							<td>${jobConf.count}</td>
							<td>
							<c:choose>
							 <c:when test="${jobConf.recurrence}">循环</c:when>
							 <c:otherwise>不循环</c:otherwise>
							</c:choose>
							</td>
							<td>
								<span>
									<a class="delJob"  href="<c:url value='/job/delete/${reptileId}/${jobConf.jobId }' />"  >删除</a>
								</span> 
								<span>
								    <a class="edit" href="<c:url value='/job/ajax/edit/${reptileId}/${jobConf.jobId }' />" onclick="return false;">详细</a>
								</span>
											<div class="form-wrapper">
												<a class="form-wrapper-close" href="javascript:void(0);"></a>
												<div class="form-wrapper-title">添加网络巡检任务</div>
												<div class="form-wrapper-center">
													<form action="<c:url value='/job/update/${reptileId}'/>" method="post">
														<input type="hidden" value="${jobConf.jobId }" /> 
														网站名:<input type="text" value="${jobConf.source_name }" />
													</form>
												</div>
											</div>
								 <span> <c:choose>
										<c:when test="${jobConf.state eq 'JOB_EXCUTING' }">
											<a  class="haltJob" href="<c:url value='/job/ajax/control/${reptileId}/${jobConf.jobId }' />" onclick="return false;" title="处于执行状态,点击暂停">暂停</a>
										</c:when>
										<c:otherwise>
											<a class="haltJob"  href="<c:url value='/job/ajax/control/${reptileId}/${jobConf.jobId }' />" onclick="return false;" title="处于暂停状态,点击执行">执行</a>
										</c:otherwise>
									</c:choose>
							</span></td>
						</tr>
					</c:forEach>
				</table>
				</div>
			</div>
		</div>
		</c:otherwise>
		</c:choose>
	</div>
</body>
</html>
