<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<fieldset>
	<legend>
		<h5>列表页配置</h5>
	</legend>
	<table class="conftable">
	<tr>
		<td><form:label path="listConf.comment">网站名<span class="red">*</span> <form:errors path="listConf.comment" cssClass="error" /></form:label></td>
		<td><form:input path="listConf.comment" /></td>
	</tr>
	<tr>
		<td><form:label path="listConf.url">URL<span class="red">*</span> <form:errors path="listConf.url" cssClass="error" /></form:label></td>
		<td><form:input path="listConf.url" /></td>
	</tr>
	<tr>
		<td>页面是否是Ajax加载</td>
		<td><fieldset class="radio">
			<label><form:radiobutton path="listConf.ajax" value="false" /> 否</label> <label><form:radiobutton
					path="listConf.ajax" value="true" />是</label>
		</fieldset></td>
	</tr>
	<tr>
		<td><form:label path="listConf.fetchinterval">抓取时间间隔(分钟)<span class="red">*</span><form:errors path="listConf.fetchinterval" cssClass="error" /></form:label></td>
		<td><form:input path="listConf.fetchinterval" /></td>
	</tr>
	<tr>
		<td><form:label path="listConf.pageNum">抓取翻页数<span class="red">*</span><form:errors path="listConf.pageNum" cssClass="error" /></form:label></td>
		<td><form:input path="listConf.pageNum" /></td>
	</tr>
	<tr>
		<td><form:label path="listConf.filterurl">过滤URL的正则表达式(可设置过滤广告)</form:label></td>
		<td><form:input path="listConf.filterurl" /></td>
	</tr>
	<tr>
		<td><form:label path="listConf.listdom">列表页的DOM结构<span class="red">*</span><form:errors path="listConf.listdom" cssClass="error" /></form:label></td>
		<td><form:input path="listConf.listdom" /></td>
	</tr>
	<tr>
		<td><form:label path="listConf.linedom">列表行的DOM结构<span class="red">*</span><form:errors path="listConf.linedom" cssClass="error" /></form:label></td>
		<td><form:input path="listConf.linedom" /></td>
	</tr>
	<tr>
		<td><form:label path="listConf.urldom">详细页URL的DOM结构<span class="red">*</span><form:errors path="listConf.urldom" cssClass="error" /></form:label></td>
		<td><form:input path="listConf.urldom" /></td>
	</tr>
	<tr>
		<td><form:label path="listConf.datedom">发布日期的DOM结构</form:label></td>
		<td><form:input path="listConf.datedom" /></td>
	</tr>
	<tr>
		<td><form:label path="listConf.updatedatedom">最近更新时间的DOM结构</form:label></td>
		<td><form:input path="listConf.updatedatedom" /></td>
	</tr>
	<%-- <tr>
		<td><form:label path="listConf.page">分页<span class="red">*</span><form:errors path="listConf.page" cssClass="error" /></form:label></td>
		<td><form:input path="listConf.page" /></td>
	</tr> --%>
	</table>
</fieldset>