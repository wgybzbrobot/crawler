<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>爬虫监控</title>
<script type="text/javascript">
var url;
function newReptile() {
    $("#dlg").dialog("open").dialog('setTitle', 'New User'); ;
    $("#fm").form("clear");
    url = "UserManage.aspx";
    document.getElementById("hidtype").value="submit";
}
function editReptile() {
    var row = $("#dg").datagrid("getSelected");
    if (row) {
        $("#dlg").dialog("open").dialog('setTitle', '编辑');
        $("#fm").form("load", row);
        url = "UserManage.aspx?id=" + row.ID;
    }
}
function saveReptile() {
    $("#fm").form("submit", {
        url: url,
        onsubmit: function () {
            return $(this).form("validate");
        },
        success: function (result) {
            if (result == "1") {
                $.messager.alert("提示信息", "操作成功");
                $("#dlg").dialog("close");
                $("#dg").datagrid("load");
            }
            else {
                $.messager.alert("提示信息", "操作失败");
            }
        }
    });
}
function deleteReptile() {
    var row = $('#dg').datagrid('getSelected');
    if (row) {
        $.messager.confirm('Confirm', '确定删除?', function (r) {
            if (r) {
                $.get('reptile/delete', { id: row.id }, function (result) {
                    if (result.success) {
                        $('#dg').datagrid('reload');    
                    } else {
                        $.messager.show({
                            title: 'Error',
                            msg: result.errorMsg
                        });
                    }
                }, 'json');
            }
        });
    }
}  
$(function() {
	$('#dg').datagrid({
	    url:'reptile/list',
	    method: 'get',
	    title: '区域爬虫管理',
	    width: 800,
	    height: 400,
	    toolbar: [{
	        iconCls: 'icon-add',
	        handler: function(){newReptile();}
	    },'-',{
	        iconCls: 'icon-help',
	        handler: function(){alert('help')}
	    }],
	    loadMsg: '数据加载中 ...',
	    rownumbers: true,
	    columns:[[
	        {field:'id',title:'Id',hidden:true},
	        {field:'name',title:'Name',width:150,  
	        	editor:{
	                type:'input',
	                options:{
	                    valueField:'name',
	                    textField:'name',
	                    data:name,
	                    required:true
	                }
		        }
	        },
	        {field:'redis',title:'Redis地址',width:150},
	        {field:'master',title:'主控地址',width:150},
	        {field:'action',title:'操作',width:70,align:'center',
                formatter:function(value,row,index){
                    if (row.editing){
                        var s = '<a href="#" onclick="saverow(this)">保存</a> ';
                        var c = '<a href="#" onclick="cancelrow(this)">取消</a>';
                        return s+c;
                    } else {
                        var e = '<a href="#" onclick="editrow(this)">编辑</a> ';
                        var d = '<a href="#" onclick="deleterow(this)">删除</a>';
                        return e+d;
                    }
                }
            }
	    ]]
	});
});
</script>
</head>
<body>
    <div style="margin: 0 auto; width:90%;">
        <table id="dg"></table>
    </div>
    <div id="dlg" class="easyui-dialog" style="width: 400px; height: 280px; padding: 10px 20px;"
       closed="true" buttons="#dlg-buttons">
        <form id="fm" action="">
        <input type="hidden" name="id" /> 
	       <div class="fitem"> 
	           <label>名称</label> 
	           <input name="name" class="easyui-validatebox" required="true" /> 
	       </div> 
	       <div class="fitem"> 
	           <label>Redis地址</label> 
	           <input name="redis" class="easyui-validatebox" required="true" /> 
	       </div> 
	       <div class="fitem"> 
	           <label>主控地址</label> 
	           <input name="master" class="easyui-validatebox" required="true,validType:'url'" /> 
	       </div> 
	       <input type="submit" name="保存" /> 
	       
       </form>
    </div>
</body>
</html>
