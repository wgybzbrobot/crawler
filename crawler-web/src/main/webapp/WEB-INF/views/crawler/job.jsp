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
    $('#jobForm').form('load', $(this).attr("href"));
    $('#jobInfo').show();
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
                <input type="hidden" name="jobId" >
                <div>
                    url<input  type="text" name="url" />
                    来源<input  type="text" name="source_name" />
                </div>
                <div>
                版块名称<input  type="text" name="type" />
                时间间隔<input  type="text" name="fetchinterval" />
                </div>
                <div>
                    循环<input  type="text" name="recurrence" />
                    网页编码<input type="text" name="encode" />
                </div>
                <div>
                    列表页规则:
                    是否ajax加载<input type="radio" name="listRule.ajax" /><br />
                    listdom<input type="text" name="listRule.listdom" /><br />
                    linedom<input type="text" name="listRule.linedom" /><br />
                    urldom<input type="text" name="listRule.urldom" /><br />
                </div>
                <div>
                    详细页规则:
                    
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
